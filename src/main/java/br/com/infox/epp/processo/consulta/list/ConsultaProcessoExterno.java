package br.com.infox.epp.processo.consulta.list;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;

@Named
@ViewScoped
public class ConsultaProcessoExterno extends DataList<Processo> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ConsultaProcessoDynamicColumnsController consultaProcessoDynamicColumnsController;
    
    private boolean exibirTable = false;
    private String numeroProcesso;

    @Override
    protected void addRestrictionFields() {
    	addRestrictionField("numeroProcesso", RestrictionType.igual);
    }

    @Override
    protected String getDefaultEjbql() {
        return "select o from Processo o";
    }

    @Override
    protected String getDefaultOrder() {
        return "dataInicio";
    }
    
    @Override
    protected String getDefaultWhere() {
    	return "where o.processoPai is null and " + SigiloProcessoPermissaoManager.getPermissaoConditionFragment();
    }

    public void exibirTable() {
        exibirTable = true;
        List<Processo> result = getResultList();
        if (!result.isEmpty()) {
        	consultaProcessoDynamicColumnsController.setFluxo(result.get(0).getNaturezaCategoriaFluxo().getFluxo());
        }
    }

    public void esconderTable() {
        newInstance();
        exibirTable = false;
        consultaProcessoDynamicColumnsController.setFluxo(null);
    }

    public boolean isExibirTable() {
        return this.exibirTable;
    }

    public String getNumeroProcesso() {
		return numeroProcesso;
	}
    
    public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}
}
