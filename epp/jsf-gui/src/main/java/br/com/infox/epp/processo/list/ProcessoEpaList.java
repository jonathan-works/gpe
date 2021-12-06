package br.com.infox.epp.processo.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.jboss.seam.security.Identity;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.google.common.base.Strings;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.core.util.ObjectUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.loglab.dto.ProcessoTarefaLogLabDTO;
import br.com.infox.epp.loglab.dto.SetorUsuarioTarefaLogLabDTO;
import br.com.infox.epp.loglab.vo.PesquisaRequerenteVO;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica_;
import br.com.infox.epp.pessoa.entity.Pessoa_;
import br.com.infox.epp.processo.consulta.list.ConsultaProcessoDynamicColumnsController;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso_;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.tarefa.dao.ProcessoTarefaDAO;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.ibpm.task.dao.TaskInstanceSearch;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.manager.UsuarioTaskInstanceManager;
import br.gov.mt.cuiaba.pmc.gdprev.ParticipanteProcessoConsulta;
import br.gov.mt.cuiaba.pmc.gdprev.ParticipanteProcessoConsulta_;
import lombok.Getter;

@Named
@ViewScoped
public class ProcessoEpaList extends EntityList<Processo> {
    public static final String NAME = "processoEpaList";

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from Processo o left join o.prioridadeProcesso prp inner join o.naturezaCategoriaFluxo naturezaCategoriaFluxo where o.idJbpm is not null and o.processoPai is null and "
            + SigiloProcessoPermissaoManager.getPermissaoConditionFragment();
    private static final String DEFAULT_ORDER = "coalesce(prp.peso, -1) DESC, o.dataInicio ASC";
    private static final String R1 = "cast(o.dataInicio as date) >= #{processoEpaList.filtros.dataInicio.from}";
    private static final String R2 = "cast(o.dataInicio as date) <= #{processoEpaList.filtros.dataInicio.to}";
    private static final String R3 = "cast(o.dataFim as date) >= #{processoEpaList.filtros.dataFim.from}";
    private static final String R4 = "cast(o.dataFim as date) <= #{processoEpaList.filtros.dataFim.to}";
    private static final String R5 = "naturezaCategoriaFluxo.fluxo = #{processoEpaList.filtros.fluxo}";
    private static final String R6 = "naturezaCategoriaFluxo.natureza = #{processoEpaList.filtros.natureza}";
    private static final String R7 = "naturezaCategoriaFluxo.categoria = #{processoEpaList.filtros.categoria}";
    private static final String R11 = "exists(select 1 from MetadadoProcesso mp where mp.processo = o "
    		+ "and mp.valor = cast(#{processoEpaList.filtros.statusProcesso.idStatusProcesso} as string) "
    		+ "and mp.metadadoType = '" + EppMetadadoProvider.STATUS_PROCESSO.getMetadadoType() + "'"
    		+ ")";
    private static final String R12 = " o.numeroProcesso = #{processoEpaList.filtros.numeroProcesso} ";
    private static final String R13 = " o.usuarioCadastro = #{processoEpaList.filtros.usuarioLogin} ";
    private static final String R14 = " exists (select 1 from ParticipanteProcessoConsulta ppc "
            + "where ppc.participanteProcesso.processo = o and ppc.pessoaFisica.cpf = #{processoEpaList.filtros.cpf}) ";
    private static final String R15 = " exists (select 1 from ParticipanteProcessoConsulta ppc "
            + "where ppc.participanteProcesso.processo = o and ppc.participanteProcesso.pessoa.idPessoa = #{processoEpaList.filtros.requerente.idPessoa}) ";

    private static final String FILTRO_PARTICIPANTE_PROCESSO = "and exists (select 1 from ParticipanteProcesso pp "
            + "where pp.processo = o and pp.pessoa.idPessoa = %d ) " ;

    @Inject
    protected ConsultaProcessoDynamicColumnsController consultaProcessoDynamicColumnsController;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private ProcessoTarefaDAO processoTarefaDAO;
    @Inject
    private TaskInstanceSearch taskInstanceSearch;
    @Inject
    private UsuarioTaskInstanceManager usuarioTaskInstanceManager;
    @Inject
    private FiltroVariavelProcessoSearch filtroVariavelProcessoSearch;

    protected FiltrosBeanList filtros = new FiltrosBeanList();

    @Getter
    private List<FiltroVariavelProcessoVO> filtrosVariaveisProcesso;
    @Getter
    private FiltroVariavelProcessoVO filtroVariavelSelecionado;

    @Override
    @PostConstruct
    public void init() {
        super.init();
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash.containsKey("idFluxo")) {
            filtros.setFluxo(fluxoManager.find(flash.get("idFluxo")));
            onSelectFluxo();
        }
        consultaProcessoDynamicColumnsController.setRecurso(DefinicaoVariavelProcessoRecursos.CONSULTA_PROCESSOS);
    }

    private void initFiltrosFluxo() {
        if(filtros != null && filtros.getFluxo() != null) {
            this.filtrosVariaveisProcesso = filtroVariavelProcessoSearch.getFiltros(filtros.getFluxo().getIdFluxo());
        } else {
            this.filtrosVariaveisProcesso = Collections.emptyList();
        }
    }

    @Override
    protected void addSearchFields() {
        addSearchField("numeroProcesso", SearchCriteria.IGUAL, R12);
        addSearchField("usuarioCadastro", SearchCriteria.IGUAL, R13);
        addSearchField("dataInicioDe", SearchCriteria.MAIOR_IGUAL, R1);
        addSearchField("dataInicioAte", SearchCriteria.MENOR_IGUAL, R2);
        addSearchField("dataFimDe", SearchCriteria.MAIOR_IGUAL, R3);
        addSearchField("dataFimAte", SearchCriteria.MENOR_IGUAL, R4);
        addSearchField("fluxo", SearchCriteria.IGUAL, R5);
        addSearchField("natureza", SearchCriteria.IGUAL, R6);
        addSearchField("categoria", SearchCriteria.IGUAL, R7);
        addSearchField("statusProcesso", SearchCriteria.IGUAL, R11);
        addSearchField("cpf", SearchCriteria.IGUAL, R14);
        addSearchField("requerente", SearchCriteria.IGUAL, R15);
    }


    @Override
    public List<Processo> getResultList() {
        setEjbql(getDefaultEjbql());
        return super.getResultList();
    }

    @Override
    public void newInstance() {
    	filtros.setFluxo(null);
    	onSelectFluxo();
    }

    public void onChangeTipoFiltroVariavelProcesso() {
        this.filtroVariavelSelecionado = null;
        getFiltros().setValorFiltroVariavelProcesso(null);
        getFiltros().setValorFiltroVariavelProcessoComplemento(null);
        if(getFiltros().getIdTipoFiltroVariavelProcesso() != null) {
            for (FiltroVariavelProcessoVO filtroVariavelProcessoVO : filtrosVariaveisProcesso) {
                if(getFiltros().getIdTipoFiltroVariavelProcesso().equals(filtroVariavelProcessoVO.getValue())) {
                    filtroVariavelSelecionado = filtroVariavelProcessoVO;
                    break;
                }
            }
        }
    }

    @Override
    protected String getDefaultEjbql() {
        String resultado = DEFAULT_EJBQL;
        PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
        if (pessoaFisica != null && Identity.instance().hasRole(Parametros.PAPEL_USUARIO_EXTERNO.getValue())) {
            resultado = resultado + String.format(FILTRO_PARTICIPANTE_PROCESSO, pessoaFisica.getIdPessoa());
        }

        resultado = filtroVariavelProcessoSearch.getHqlFiltroVariavelProcesso(
            resultado
            ,getFiltros().getIdTipoFiltroVariavelProcesso()
            ,getFiltros().getValorFiltroVariavelProcesso()
            ,getFiltros().getValorFiltroVariavelProcessoComplemento()
        );

        return resultado;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    public List<UsuarioLogin> getUsuarios(String search) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioLogin> query = cb.createQuery(UsuarioLogin.class);
		Root<Processo> processo = query.from(Processo.class);
		Join<Processo, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
		Join<Processo, UsuarioLogin> usuario = processo.join(Processo_.usuarioCadastro, JoinType.INNER);
		query.select(usuario);
		query.distinct(true);
		query.orderBy(cb.asc(usuario.get(UsuarioLogin_.nomeUsuario)));
		query.where(cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), filtros.getFluxo()));
		if (!Strings.isNullOrEmpty(search)) {
			query.where(query.getRestriction(), cb.like(cb.lower(usuario.get(UsuarioLogin_.nomeUsuario)), "%" + search.toLowerCase() + "%"));
		}
		return getEntityManager().createQuery(query).getResultList();
    }

    public StatusProcesso getStatusProcesso(Processo processo) {
        MetadadoProcesso mp = processo.getMetadado(EppMetadadoProvider.STATUS_PROCESSO);
        return mp != null ? (StatusProcesso) mp.getValue() : null;
    }

    public void onSelectFluxo() {
        filtros.clear();
        consultaProcessoDynamicColumnsController.setFluxo(filtros.getFluxo());
        setEntity(new Processo());
        initFiltrosFluxo();
    }

	public void search() {
		if (filtros.getFluxo() == null && (!Strings.isNullOrEmpty(filtros.getNumeroProcesso()) || !Strings.isNullOrEmpty(filtros.getCpf()) || !ObjectUtil.isEmpty(filtros.getRequerente()))) {
			List<Processo> results = getResultList();
			if (!results.isEmpty()) {
				consultaProcessoDynamicColumnsController.setFluxo(results.get(0).getNaturezaCategoriaFluxo().getFluxo());
			}
		}
	}

    private List<ProcessoTarefa> getListProcessoTarefaAberto(Processo processo) {
        if (!ObjectUtil.isEmpty(processo))
            return processoTarefaDAO.getByProcesso(processo).stream().filter(t -> ObjectUtil.isEmpty(t.getDataFim())).collect(Collectors.toList());
        return null;
    }

    public List<ProcessoTarefaLogLabDTO> getProcessoTarefasAberto(Processo processo) {
        List<ProcessoTarefaLogLabDTO> results = new ArrayList<>();
        if (!ObjectUtil.isEmpty(processo)) {
            for (ProcessoTarefa tarefa : getListProcessoTarefaAberto(processo)) {
                String assignee = taskInstanceSearch.getAssignee(tarefa.getTaskInstance());
                results.add(new ProcessoTarefaLogLabDTO(
                        tarefa.getTaskInstance(),
                        tarefa.getProcesso().getIdProcesso(),
                        tarefa.getTarefa().getTarefa(),
                        assignee
                ));
            }
        }
        return results.stream().sorted((t1, t2) -> t1.getDescricao().compareTo(t2.getDescricao())).collect(Collectors.toList());
    }

    public List<SetorUsuarioTarefaLogLabDTO> getSetorUsuarioTarefasAberto(Processo processo) {
        List<SetorUsuarioTarefaLogLabDTO> results = new ArrayList<>();
        if (!ObjectUtil.isEmpty(processo)) {
            for (ProcessoTarefa tarefa : getListProcessoTarefaAberto(processo)) {
                TaskInstance taskInstance = taskInstanceSearch.getTaskInstance(tarefa.getTaskInstance());
                UsuarioTaskInstance usuarioTaskInstance = usuarioTaskInstanceManager.find(tarefa.getTaskInstance());

                results.add(new SetorUsuarioTarefaLogLabDTO(
                        taskInstance.getSwimlaneInstance().getName(),
                        !ObjectUtil.isEmpty(usuarioTaskInstance) ? usuarioTaskInstance.getUsuario().getNomeUsuario() : null
                ));
            }
        }
        return results.stream().distinct().sorted((su1, su2) -> (su1.getSwimlane().compareTo(su2.getSwimlane()))).collect(Collectors.toList());
    }

    public List<PesquisaRequerenteVO> getRequerentes(String search) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PesquisaRequerenteVO> query = cb.createQuery(PesquisaRequerenteVO.class);
        Root<ParticipanteProcessoConsulta> participanteProcConsulta = query.from(ParticipanteProcessoConsulta.class);
        Join<?, ParticipanteProcesso> participanteProcesso = participanteProcConsulta.join(ParticipanteProcessoConsulta_.participanteProcesso, JoinType.INNER);
        Join<?, Pessoa> pessoa = participanteProcesso.join(ParticipanteProcesso_.pessoa, JoinType.INNER);
        Join<?, PessoaFisica> pessoaFisica = participanteProcConsulta.join(ParticipanteProcessoConsulta_.pessoaFisica, JoinType.LEFT);
        Join<?, PessoaJuridica> pessoaJuridica = participanteProcConsulta.join(ParticipanteProcessoConsulta_.pessoaJuridica, JoinType.LEFT);
        query.select(cb.construct(PesquisaRequerenteVO.class,
           pessoa.get(Pessoa_.idPessoa),
           pessoaFisica.get(PessoaFisica_.cpf),
           pessoaJuridica.get(PessoaJuridica_.cnpj),
           participanteProcesso.get(ParticipanteProcesso_.nome)
        ));
        query.distinct(true);
        if (!Strings.isNullOrEmpty(search)) {
            query.where(cb.like(cb.lower(participanteProcesso.get(ParticipanteProcesso_.nome)), "%" + search.toLowerCase() + "%"));
        }
        query.orderBy(cb.asc(participanteProcesso.get(ParticipanteProcesso_.nome)));
        return getEntityManager().createQuery(query).getResultList();
    }

	public List<Categoria> getCategorias(String search) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Categoria> query = cb.createQuery(Categoria.class);
		Root<Processo> processo = query.from(Processo.class);
		Join<Processo, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
		Join<NaturezaCategoriaFluxo, Categoria> categoria = ncf.join(NaturezaCategoriaFluxo_.categoria, JoinType.INNER);
		query.select(categoria);
		query.distinct(true);
		query.where(cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), filtros.getFluxo()));
		if (!Strings.isNullOrEmpty(search)) {
			query.where(query.getRestriction(), cb.like(cb.lower(categoria.get(Categoria_.categoria)), "%" + search.toLowerCase() + "%"));
		}
		query.orderBy(cb.asc(categoria.get(Categoria_.categoria)));
		return getEntityManager().createQuery(query).getResultList();
	}

	public List<Natureza> getNaturezas(String search) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Natureza> query = cb.createQuery(Natureza.class);
		Root<Processo> processo = query.from(Processo.class);
		Join<Processo, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
		Join<NaturezaCategoriaFluxo, Natureza> natureza = ncf.join(NaturezaCategoriaFluxo_.natureza, JoinType.INNER);
		query.select(natureza);
		query.distinct(true);
		query.where(cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), filtros.getFluxo()));
		if (!Strings.isNullOrEmpty(search)) {
			query.where(query.getRestriction(), cb.like(cb.lower(natureza.get(Natureza_.natureza)), "%" + search.toLowerCase() + "%"));
		}
		query.orderBy(cb.asc(natureza.get(Natureza_.natureza)));
		return getEntityManager().createQuery(query).getResultList();
	}

	public List<StatusProcesso> getStatusProcessos(String search) {
		List<String> idsStatus = getListaIds(EppMetadadoProvider.STATUS_PROCESSO.getMetadadoType());
		List<StatusProcesso> statusProcessos = getValoresMetadados(StatusProcesso.class, StatusProcesso_.idStatusProcesso, StatusProcesso_.nome,
				idsStatus, search);
		Collections.sort(statusProcessos, new Comparator<StatusProcesso>() {
			@Override
			public int compare(StatusProcesso o1, StatusProcesso o2) {
				return o1.getNome().compareToIgnoreCase(o2.getNome());
			}
		});
		return statusProcessos;
	}

	private List<String> getListaIds(String metadadoType) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<Processo> processo = query.from(Processo.class);
		Join<Processo, MetadadoProcesso> mp = processo.join(Processo_.metadadoProcessoList, JoinType.INNER);
		Join<Processo, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
		query.select(mp.get(MetadadoProcesso_.valor));
		query.distinct(true);
		query.where(cb.equal(mp.get(MetadadoProcesso_.metadadoType), metadadoType),
				cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), filtros.getFluxo()));
		return getEntityManager().createQuery(query).getResultList();
	}

	private <T> List<T> getValoresMetadados(Class<T> rootClass, SingularAttribute<? super T, Integer> id, SingularAttribute<? super T, String> nome,
			List<String> entityIds, String search) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(rootClass);
		Root<T> root = query.from(rootClass);
		String pattern = !Strings.isNullOrEmpty(search) ? "%" + search.toLowerCase() + "%" : null;
		List<T> results = new ArrayList<>();

		for (String entityId : entityIds) {
			query.where(cb.equal(root.get(id), Integer.valueOf(entityId)));
			if (pattern != null) {
				query.where(query.getRestriction(), cb.like(cb.lower(root.get(nome)), pattern));
			}
			try {
				results.add(getEntityManager().createQuery(query).getSingleResult());
			} catch (NoResultException e) {
			}
		}

		return results;
	}

	public FiltrosBeanList getFiltros() {
		return filtros;
	}

	public void setFiltros(FiltrosBeanList filtros) {
		this.filtros = filtros;
	}

}
