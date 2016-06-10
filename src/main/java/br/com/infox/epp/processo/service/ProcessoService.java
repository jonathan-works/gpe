package br.com.infox.epp.processo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.dao.ProcessoSearch;
import br.com.infox.epp.processo.dao.ProcessoSearch.ValorMetadado;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessoService extends PersistenceController {
    
    @Inject
    private IniciarProcessoService iniciarProcessoService;
    @Inject
    private VariavelInicioProcessoService variavelInicioProcessoService;
    @Inject
    private MetadadoProcessoManager metadadoProcessoManager;
    @Inject
    private ProcessoSearch processoSearch; 
    
	public boolean isTipoProcessoDocumento(Processo processo) {
		return isTipoProcesso(TipoProcesso.DOCUMENTO.toString(), processo);
	}
	
	public boolean isTipoProcessoComunicacao(Processo processo) {
		return isTipoProcesso(TipoProcesso.COMUNICACAO.toString(), processo);
	}
	
	public boolean isTipoProcesso(String tipoProcesso, Processo processo){
		TipoProcesso byName = TipoProcesso.getByName(tipoProcesso);
		MetadadoProcesso metadadoTipoProcesso = processo.getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
		if(metadadoTipoProcesso != null){
		    TipoProcesso tpProcesso = metadadoTipoProcesso.getValue();
		    return byName.equals(tpProcesso);
		}
		return false; 
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ProcessInstance iniciarProcessoRemoverMetadadoStatus(Processo processo, Map<String, Object> variables) {
	    processo = getEntityManager().merge(processo);
	    MetadadoProcesso metadadoStatus = metadadoProcessoManager.getMetadado(EppMetadadoProvider.STATUS_PROCESSO, processo);
	    metadadoProcessoManager.remove(metadadoStatus);
	    processo.removerMetadado(EppMetadadoProvider.STATUS_PROCESSO);
	    variavelInicioProcessoService.removeAll(processo);
	    ProcessInstance processInstance = iniciarProcessoService.iniciarProcesso(processo, variables, false);
	    return processInstance;
	}
	
	/**
	 * Localiza os processos que contêm todos os metadados informados
	 * @param definicoesMetadados Definições específicas de metadados que serão utilizados para converter valores para pesquisa no banco
	 * @param metadados Mapa contendo chaves e valores dos metadados que serão utilizados como filtro na pesquisa
	 * @return Lista de processos contendo todos os metadados informados
	 */
	public List<Processo> getProcessosContendoMetadados(Map<String, MetadadoProcessoDefinition> definicoesMetadados, Map<String, Object> metadados) {
		Map<String, ValorMetadado> valoresMetadadosBanco = new HashMap<>();
		for(String metadado : metadados.keySet()) {
			Object valorOriginal = metadados.get(metadado);
			
			Class<?> classe = MetadadoProcessoProvider.getClasseMetadado(definicoesMetadados, metadado, valorOriginal);
			String valor = MetadadoProcessoProvider.getValorMetadado(metadado, valorOriginal);
			
			ValorMetadado valorMetadadoBanco = new ValorMetadado(classe, valor);
			valoresMetadadosBanco.put(metadado, valorMetadadoBanco);
		}
		
		return processoSearch.getProcessosContendoMetadados(valoresMetadadosBanco);		
	}
	
}
