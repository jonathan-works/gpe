package br.com.infox.epp.processo.list;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.util.time.Periodo;

@Name(ProcessoEpaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
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

    private List<UsuarioLogin> listaUsuarios;
    
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
        iniciaListaUsuarios();
    }
    
    @Override
    public void newInstance() {
    	super.newInstance();
    	dataInicio = new Periodo();
    	dataFim = new Periodo();
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
    
}
