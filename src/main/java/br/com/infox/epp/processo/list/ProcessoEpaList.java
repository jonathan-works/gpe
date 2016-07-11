package br.com.infox.epp.processo.list;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoSearch;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
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
    private static final String DYNAMIC_COLUMN_EXPRESSION = "#'{'processoEpaList.getValor(row, ''{0}'')'}";

    @Inject
    private VariavelProcessoService variavelProcessoService;
    @Inject
    private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private PapelManager papelManager;
    
    private List<UsuarioLogin> listaUsuarios;
    private List<DynamicColumnModel> dynamicColumns;
    private Fluxo fluxo;
    private List<Fluxo> fluxos;
    private List<String> controleMensagensValidacao = new ArrayList<>();
    
    private Periodo dataInicio;
    private Periodo dataFim;
    
    @Override
    protected void addSearchFields() {
        addSearchField("numeroProcesso", SearchCriteria.IGUAL);
        addSearchField("usuarioCadastro", SearchCriteria.IGUAL);
        addSearchField("dataInicioDe", SearchCriteria.MAIOR_IGUAL, R1);
        addSearchField("dataInicioAte", SearchCriteria.MENOR_IGUAL, R2);
        addSearchField("dataFimDe", SearchCriteria.MAIOR_IGUAL, R3);
        addSearchField("dataFimAte", SearchCriteria.MENOR_IGUAL, R4);
        addSearchField("fluxo", SearchCriteria.IGUAL, R5);
        iniciaListaUsuarios();
    }
    
    @Override
    public void newInstance() {
    	super.newInstance();
    	dataInicio = new Periodo();
    	dataFim = new Periodo();
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

	@SuppressWarnings(UNCHECKED)
    private void iniciaListaUsuarios() {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct user from Processo o ");
        sb.append("join o.usuarioCadastro user");
        listaUsuarios = getEntityManager().createQuery(sb.toString()).getResultList();
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

    public List<UsuarioLogin> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<UsuarioLogin> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
    
    public StatusProcesso getStatusProcesso(Processo processo) {
        MetadadoProcesso mp = processo.getMetadado(EppMetadadoProvider.STATUS_PROCESSO);
        return mp != null ? (StatusProcesso) mp.getValue() : null;
    }
    
    public Fluxo getFluxo() {
		return fluxo;
	}
    
    public void setFluxo(Fluxo fluxo) {
    	if (!Objects.equals(fluxo, this.fluxo)) {
			this.fluxo = fluxo;
			dynamicColumns = null;
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
    
    public List<Fluxo> getFluxos() {
    	if (fluxos == null) {
    		fluxos = fluxoManager.getFluxosAtivosList();
    	}
		return fluxos;
	}
    
    public void clearMensagensValidacao() {
    	controleMensagensValidacao = new ArrayList<>();
    }
}
