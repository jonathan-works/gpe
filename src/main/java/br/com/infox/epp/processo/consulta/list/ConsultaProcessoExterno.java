package br.com.infox.epp.processo.consulta.list;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoSearch;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;

@Named
@ViewScoped
public class ConsultaProcessoExterno extends DataList<Processo> {

    private static final long serialVersionUID = 1L;

    @Inject
    private VariavelProcessoService variavelProcessoService;
    @Inject
    private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
    
    private boolean exibirTable = false;
    private List<DynamicColumnModel> dynamicColumns;
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
        dynamicColumns = null;
    }

    public void esconderTable() {
        newInstance();
        exibirTable = false;
        dynamicColumns = null;
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
    
    public List<DynamicColumnModel> getDynamicColumns() {
    	if (dynamicColumns == null && exibirTable && !getResultList().isEmpty()) {
    		dynamicColumns = new ArrayList<>();
    		// Pesquisa por número de processo, só exibe um processo
    		Processo processo = getResultList().get(0);
    		List<DefinicaoVariavelProcesso> definicoes = definicaoVariavelProcessoSearch.getDefinicoesVariaveis(processo.getNaturezaCategoriaFluxo().getFluxo(), 
    				DefinicaoVariavelProcessoRecursos.CONSULTA_EXTERNA.getIdentificador(), true);
    		for (DefinicaoVariavelProcesso definicao : definicoes) {
    			VariavelProcesso variavel = variavelProcessoService.getVariavelProcesso(processo.getIdProcesso(), definicao.getNome(), null);
    			dynamicColumns.add(new DynamicColumnModel(definicao.getLabel(), variavel != null ? variavel.getValor() : ""));
    		}
    	}
		return dynamicColumns;
	}
}
