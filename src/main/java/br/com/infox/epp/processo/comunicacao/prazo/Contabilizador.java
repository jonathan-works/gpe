package br.com.infox.epp.processo.comunicacao.prazo;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.timer.TaskExpirationInfo;
import br.com.infox.epp.processo.timer.TaskExpirationProcessor;
import br.com.infox.ibpm.process.definition.annotations.DefinitionAvaliable;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(Contabilizador.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
@DefinitionAvaliable
public class Contabilizador {
    public static final String NAME = "contabilizador";
    
    @In
    private ComunicacaoService comunicacaoService;
    @In("org.jboss.seam.bpm.taskInstance")
    private TaskInstance taskInstance;
    @In
    private TaskExpirationProcessor taskExpirationProcessor;
    
    public void contabilizarPrazoCiencia(String transition) {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	Date fimPrazo = comunicacaoService.contabilizarPrazoCiencia(comunicacao);
    	
    	contabilizarPrazo(fimPrazo, transition);
    }
    
    public void contabilizarPrazoCumprimento(String transition) {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	Date fimPrazo = comunicacaoService.contabilizarPrazoCumprimento(comunicacao);
    	
    	if (fimPrazo == null) {
    		taskInstance.end(transition);
    	} else {
    		contabilizarPrazo(fimPrazo, transition);
    	}
    }
    
    private void contabilizarPrazo(Date fimPrazo, String transition) {
    	TaskExpirationInfo info = new TaskExpirationInfo();
    	info.setTransition(transition);
    	info.setTaskId(taskInstance.getId());
    	info.setExpiration(fimPrazo);
    	
    	taskExpirationProcessor.endTask(info.getExpiration(), info);
    }
}
