package br.com.infox.epp.processo.consulta.list;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;

@Named
@ViewScoped
public class ConsultaProcessoExternoPorPartes extends DataList<Processo> {

    private static final long serialVersionUID = 1L;

    private static final String R1 = "exists (select pp from ParticipanteProcesso pp "
            + "where lower(pp.nome) like lower(concat('%', #{consultaProcessoExternoPorPartes.nomePartes}, '%'))"
            + " and pp.processo = o)";

    @Inject
    private ConsultaProcessoDynamicColumnsController consultaProcessoDynamicColumnsController;
    
    private boolean exibirTable = false;
    private Fluxo fluxo;
    private String nomePartes;
    
    @PostConstruct
    @Override
    public void init() {
    	super.init();
    	consultaProcessoDynamicColumnsController.setRecurso(DefinicaoVariavelProcessoRecursos.CONSULTA_EXTERNA);
    }
    
    @Override
    protected void addRestrictionFields() {
    	addRestrictionField("nomePartes", R1);
    	addRestrictionField("fluxo", "ncf.fluxo", RestrictionType.igual);
    }

    @Override
    protected String getDefaultEjbql() {
        return "select o from Processo o inner join o.naturezaCategoriaFluxo ncf";
    }

    @Override
    protected String getDefaultOrder() {
        return "o.dataInicio";
    }
    
    @Override
    protected String getDefaultWhere() {
    	return "where o.processoPai is null and o.idJbpm is not null and " + SigiloProcessoPermissaoManager.getPermissaoConditionFragment();
    }

    public void exibirTable() {
        exibirTable = true;
    }

    public void esconderTable() {
        newInstance();
        exibirTable = false;
    }

    public boolean isExibirTable() {
        return this.exibirTable;
    }

    public String getNomePartes() {
        return nomePartes;
    }

    public void setNomePartes(String nomePartes) {
        this.nomePartes = nomePartes;
    }
    
    public Fluxo getFluxo() {
		return fluxo;
	}
    
    public void setFluxo(Fluxo fluxo) {
    	if (fluxo == null || !Objects.equals(fluxo, this.fluxo)) {
    		this.fluxo = fluxo;
    		nomePartes = null;
    		consultaProcessoDynamicColumnsController.setFluxo(fluxo);
    	}
	}
}
