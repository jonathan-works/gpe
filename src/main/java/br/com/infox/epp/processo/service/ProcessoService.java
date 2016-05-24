package br.com.infox.epp.processo.service;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
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
	    ProcessInstance processInstance = iniciarProcessoService.iniciarProcesso(processo, variables);
	    return processInstance;
	}
	
}
