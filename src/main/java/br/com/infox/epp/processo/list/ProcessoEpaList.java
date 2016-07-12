package br.com.infox.epp.processo.list;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Strings;

import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoSearch;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada_;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica_;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.util.time.Periodo;

@Named
@ViewScoped
public class ProcessoEpaList extends EntityList<Processo> {
    public static final String NAME = "processoEpaList";

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from Processo o where o.idJbpm is not null and o.processoPai is null and "
            + SigiloProcessoPermissaoManager.getPermissaoConditionFragment();
    private static final String DEFAULT_ORDER = "coalesce(o.prioridadeProcesso, -1) DESC, o.dataInicio ASC";
    private static final String R1 = "cast(dataInicio as date) >= #{processoEpaList.dataInicio.from}";
    private static final String R2 = "cast(dataInicio as date) <= #{processoEpaList.dataInicio.to}";
    private static final String R3 = "cast(dataFim as date) >= #{processoEpaList.dataFim.from}";
    private static final String R4 = "cast(dataFim as date) <= #{processoEpaList.dataFim.to}";
    private static final String R5 = "naturezaCategoriaFluxo.fluxo = #{processoEpaList.fluxo}";
    private static final String R6 = "naturezaCategoriaFluxo.natureza = #{processoEpaList.natureza}";
    private static final String R7 = "naturezaCategoriaFluxo.categoria = #{processoEpaList.categoria}";
    private static final String R8 = "exists(select 1 from MetadadoProcesso mp where mp.processo = o "
    		+ "and mp.valor = cast(#{processoEpaList.relator.idPessoa} as string) "
    		+ "and mp.metadadoType = '" + EppMetadadoProvider.RELATOR.getMetadadoType() + "'"
    		+ ")";
    private static final String R9 = "exists(select 1 from MetadadoProcesso mp where mp.processo = o "
    		+ "and mp.valor = cast(#{processoEpaList.unidadeDecisoraMonocratica.idUnidadeDecisoraMonocratica} as string) "
    		+ "and mp.metadadoType = '" + EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType() + "'"
    		+ ")";
    private static final String R10 = "exists(select 1 from MetadadoProcesso mp where mp.processo = o "
    		+ "and mp.valor = cast(#{processoEpaList.unidadeDecisoraColegiada.idUnidadeDecisoraColegiada} as string) "
    		+ "and mp.metadadoType = '" + EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType() + "'"
    		+ ")";
    private static final String R11 = "exists(select 1 from MetadadoProcesso mp where mp.processo = o "
    		+ "and mp.valor = cast(#{processoEpaList.statusProcesso.idStatusProcesso} as string) "
    		+ "and mp.metadadoType = '" + EppMetadadoProvider.STATUS_PROCESSO.getMetadadoType() + "'"
    		+ ")";
    private static final String DYNAMIC_COLUMN_EXPRESSION = "#'{'processoEpaList.getValor(row, ''{0}'')'}";

    @Inject
    private VariavelProcessoService variavelProcessoService;
    @Inject
    private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
    @Inject
    private PapelManager papelManager;
    
    private List<DynamicColumnModel> dynamicColumns;
    private Fluxo fluxo;
    private List<String> controleMensagensValidacao = new ArrayList<>();
    
    private Periodo dataInicio;
    private Periodo dataFim;
    private PessoaFisica relator;
    private UnidadeDecisoraMonocratica unidadeDecisoraMonocratica;
    private UnidadeDecisoraColegiada unidadeDecisoraColegiada;
    private Natureza natureza;
    private Categoria categoria;
    private StatusProcesso statusProcesso;
    
    @Override
    protected void addSearchFields() {
        addSearchField("numeroProcesso", SearchCriteria.IGUAL);
        addSearchField("usuarioCadastro", SearchCriteria.IGUAL);
        addSearchField("dataInicioDe", SearchCriteria.MAIOR_IGUAL, R1);
        addSearchField("dataInicioAte", SearchCriteria.MENOR_IGUAL, R2);
        addSearchField("dataFimDe", SearchCriteria.MAIOR_IGUAL, R3);
        addSearchField("dataFimAte", SearchCriteria.MENOR_IGUAL, R4);
        addSearchField("fluxo", SearchCriteria.IGUAL, R5);
        addSearchField("natureza", SearchCriteria.IGUAL, R6);
        addSearchField("categoria", SearchCriteria.IGUAL, R7);
        addSearchField("relator", SearchCriteria.IGUAL, R8);
        addSearchField("unidadeDecisoraMonocratica", SearchCriteria.IGUAL, R9);
        addSearchField("unidadeDecisoraColegiada", SearchCriteria.IGUAL, R10);
        addSearchField("statusProcesso", SearchCriteria.IGUAL, R11);
    }
    
    @Override
    public void newInstance() {
    	setFluxo(null);
    }
    
    public Periodo getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Periodo dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Periodo getDataFim() {
		return dataFim;
	}

	public void setDataFim(Periodo dataFim) {
		this.dataFim = dataFim;
	}

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
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
		query.where(cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), fluxo));
		if (!Strings.isNullOrEmpty(search)) {
			query.where(query.getRestriction(), cb.like(cb.lower(usuario.get(UsuarioLogin_.nomeUsuario)), "%" + search.toLowerCase() + "%"));
		}
		return getEntityManager().createQuery(query).getResultList();
    }

    public StatusProcesso getStatusProcesso(Processo processo) {
        MetadadoProcesso mp = processo.getMetadado(EppMetadadoProvider.STATUS_PROCESSO);
        return mp != null ? (StatusProcesso) mp.getValue() : null;
    }
    
    public Fluxo getFluxo() {
		return fluxo;
	}
    
    public void setFluxo(Fluxo fluxo) {
    	if (fluxo == null || !Objects.equals(fluxo, this.fluxo)) {
			this.fluxo = fluxo;
			dynamicColumns = null;
			dataInicio = new Periodo();
	    	dataFim = new Periodo();
	    	setCategoria(null);
	    	setNatureza(null);
	    	setRelator(null);
	    	setStatusProcesso(null);
	    	setUnidadeDecisoraColegiada(null);
	    	setUnidadeDecisoraMonocratica(null);
	    	super.newInstance();
    	}
	}
    
    public List<DynamicColumnModel> getDynamicColumns() {
    	if (dynamicColumns == null) {
    		dynamicColumns = new ArrayList<>();
    		for (DefinicaoVariavelProcesso definicaoVariavel : definicaoVariavelProcessoSearch.getDefinicoesVariaveis(fluxo, 
    				DefinicaoVariavelProcessoRecursos.CONSULTA_PROCESSOS.getIdentificador(), papelManager.isUsuarioExterno(Authenticator.getPapelAtual().getIdentificador()))) {
    			DynamicColumnModel model = new DynamicColumnModel(definicaoVariavel.getLabel(), MessageFormat.format(DYNAMIC_COLUMN_EXPRESSION, definicaoVariavel.getNome()));
    			dynamicColumns.add(model);
    		}
    	}
		return dynamicColumns;
	}
    
    public String getValor(Processo processo, String nomeVariavel) {
    	try {
	    	VariavelProcesso variavel = variavelProcessoService.getVariavelProcesso(processo.getIdProcesso(), nomeVariavel);
	    	if (variavel != null) {
	    		return variavel.getValor();
	    	}
    	} catch (BusinessException e) {
    		if (!controleMensagensValidacao.contains(e.getMessage())) {
    			FacesMessages.instance().add(e.getMessage());
    			controleMensagensValidacao.add(e.getMessage());
    		}
    	}
    	return null;
    }
    
    public List<Fluxo> getFluxos(String search) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Fluxo> query = cb.createQuery(Fluxo.class);
    	Root<Processo> processo = query.from(Processo.class);
    	Join<Processo, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
    	Join<NaturezaCategoriaFluxo, Fluxo> fluxo = ncf.join(NaturezaCategoriaFluxo_.fluxo, JoinType.INNER);
    	query.select(fluxo);
    	query.orderBy(cb.asc(fluxo.get(Fluxo_.fluxo)));
    	query.distinct(true);
    	if (!Strings.isNullOrEmpty(search)) {
    		query.where(cb.like(cb.lower(fluxo.get(Fluxo_.fluxo)), "%" + search.toLowerCase() + "%"));
    	}
    	return getEntityManager().createQuery(query).getResultList();
	}
    
    public void clearMensagensValidacao() {
    	controleMensagensValidacao = new ArrayList<>();
    }

	public PessoaFisica getRelator() {
		return relator;
	}

	public void setRelator(PessoaFisica relator) {
		this.relator = relator;
	}

	public UnidadeDecisoraMonocratica getUnidadeDecisoraMonocratica() {
		return unidadeDecisoraMonocratica;
	}

	public void setUnidadeDecisoraMonocratica(UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) {
		this.unidadeDecisoraMonocratica = unidadeDecisoraMonocratica;
	}

	public UnidadeDecisoraColegiada getUnidadeDecisoraColegiada() {
		return unidadeDecisoraColegiada;
	}

	public void setUnidadeDecisoraColegiada(UnidadeDecisoraColegiada unidadeDecisoraColegiada) {
		this.unidadeDecisoraColegiada = unidadeDecisoraColegiada;
	}

	public Natureza getNatureza() {
		return natureza;
	}

	public void setNatureza(Natureza natureza) {
		this.natureza = natureza;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public StatusProcesso getStatusProcesso() {
		return statusProcesso;
	}

	public void setStatusProcesso(StatusProcesso statusProcesso) {
		this.statusProcesso = statusProcesso;
	}
	
	public List<Categoria> getCategorias(String search) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Categoria> query = cb.createQuery(Categoria.class);
		Root<Processo> processo = query.from(Processo.class);
		Join<Processo, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
		Join<NaturezaCategoriaFluxo, Categoria> categoria = ncf.join(NaturezaCategoriaFluxo_.categoria, JoinType.INNER);
		query.select(categoria);
		query.distinct(true);
		query.where(cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), fluxo));
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
		query.where(cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), fluxo));
		if (!Strings.isNullOrEmpty(search)) {
			query.where(query.getRestriction(), cb.like(cb.lower(natureza.get(Natureza_.natureza)), "%" + search.toLowerCase() + "%"));
		}
		query.orderBy(cb.asc(natureza.get(Natureza_.natureza)));
		return getEntityManager().createQuery(query).getResultList();
	}
	
	public List<PessoaFisica> getRelatores(String search) {
		List<String> idsRelatores = getListaIds(EppMetadadoProvider.RELATOR.getMetadadoType());
		List<PessoaFisica> relatores = getValoresMetadados(PessoaFisica.class, PessoaFisica_.idPessoa, PessoaFisica_.nome, idsRelatores, search);
 		Collections.sort(relatores, new Comparator<PessoaFisica>() {
			@Override
			public int compare(PessoaFisica o1, PessoaFisica o2) {
				return o1.getNome().compareToIgnoreCase(o2.getNome());
			}
		});
		return relatores;
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
	
	public List<UnidadeDecisoraMonocratica> getUnidadesDecisorasMonocraticas(String search) {
		List<String> idsUdms = getListaIds(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType());
		List<UnidadeDecisoraMonocratica> unidadesDecisorasMonocraticas = getValoresMetadados(UnidadeDecisoraMonocratica.class, UnidadeDecisoraMonocratica_.idUnidadeDecisoraMonocratica,
				UnidadeDecisoraMonocratica_.nome, idsUdms, search);
		Collections.sort(unidadesDecisorasMonocraticas, new Comparator<UnidadeDecisoraMonocratica>() {
			@Override
			public int compare(UnidadeDecisoraMonocratica o1, UnidadeDecisoraMonocratica o2) {
				return o1.getNome().compareToIgnoreCase(o2.getNome());
			}
		});
		return unidadesDecisorasMonocraticas;
	}
	
	public List<UnidadeDecisoraColegiada> getUnidadesDecisorasColegiadas(String search) {
		List<String> idsUdcs = getListaIds(EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA.getMetadadoType());
		List<UnidadeDecisoraColegiada> unidadesDecisorasColegiadas = getValoresMetadados(UnidadeDecisoraColegiada.class, UnidadeDecisoraColegiada_.idUnidadeDecisoraColegiada,
				UnidadeDecisoraColegiada_.nome, idsUdcs, search);
		Collections.sort(unidadesDecisorasColegiadas, new Comparator<UnidadeDecisoraColegiada>() {
			@Override
			public int compare(UnidadeDecisoraColegiada o1, UnidadeDecisoraColegiada o2) {
				return o1.getNome().compareToIgnoreCase(o2.getNome());
			}
		});
		return unidadesDecisorasColegiadas;
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
				cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), fluxo));
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
}
