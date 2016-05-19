package br.com.infox.epp.processo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.joda.time.DateTime;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;

@Stateless
public class IniciarProcessoService {

    @Inject
    private NaturezaManager naturezaManager;
    @Inject
    private PastaManager pastaManager;
    @Inject
    private MetadadoProcessoManager metadadoProcessoManager;

    public ProcessInstance iniciarProcesso(Processo processo) throws DAOException {
        return iniciarProcesso(processo, null, null, null, true);
    }
    
    public ProcessInstance iniciarProcesso(Processo processo, String transitionName) throws DAOException {
        return iniciarProcesso(processo, null, null, transitionName, true);
    }
    
    public ProcessInstance iniciarProcesso(Processo processo, Map<String, Object> variaveis) throws DAOException {
        return iniciarProcesso(processo, variaveis, null, null, true);
    }
    public ProcessInstance iniciarProcesso(Processo processo, List<MetadadoProcesso> metadados) throws DAOException {
        return iniciarProcesso(processo, null, metadados, null, true);
    }
    
    public ProcessInstance iniciarProcesso(Processo processo, Map<String, Object> variaveis, boolean createDefaultFolders) throws DAOException {
        return iniciarProcesso(processo, variaveis, null, null, createDefaultFolders);
    }
    
    public ProcessInstance iniciarProcesso(Processo processo, Map<String, Object> variaveis, List<MetadadoProcesso> metadados, String transitionName, 
            boolean createDefaultFolders) throws DAOException {
        processo = getEntityManager().merge(processo);
        processo.setDataInicio(DateTime.now().toDate());
        createMetadadosProcesso(processo, metadados);
        variaveis = adicionarVariaveisDefault(processo, variaveis);
        ProcessInstance processInstance = criarProcessInstance(processo, variaveis);
        processo.setIdJbpm(processInstance.getId());
        naturezaManager.lockNatureza(processo.getNaturezaCategoriaFluxo().getNatureza());
        if (createDefaultFolders) {
            pastaManager.createDefaultFolders(processo);
        }
        getEntityManager().flush();
        movimentarProcesso(processInstance, transitionName);
        ManagedJbpmContext.instance().getSession().flush();
        return processInstance;
    }

    protected void createMetadadosProcesso(Processo processo, List<MetadadoProcesso> metadados) {
        if (metadados != null) {
            for (MetadadoProcesso metadadoProcesso : metadados) {
                metadadoProcesso.setProcesso(processo);
                metadadoProcessoManager.persist(metadadoProcesso);
            }
        }
    }

    protected ProcessInstance criarProcessInstance(Processo processo, Map<String, Object> variaveis) {
        String processDefinitionName = processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo();
        ProcessInstance processInstance = ManagedJbpmContext.instance().newProcessInstanceForUpdate(processDefinitionName, variaveis);
        return processInstance;
    }
    
    protected Map<String, Object> adicionarVariaveisDefault(Processo processo, Map<String, Object> variaveis) {
        if (variaveis == null) variaveis = new HashMap<>();
        variaveis.put(VariaveisJbpmProcessosGerais.PROCESSO, processo.getIdProcesso());
        variaveis.put(VariaveisJbpmProcessosGerais.DATA_INICIO_PROCESSO, processo.getDataInicio());
        if (processo.getProcessoPai() == null) {
            variaveis.put(VariaveisJbpmProcessosGerais.NATUREZA, processo.getNaturezaCategoriaFluxo().getNatureza().getNatureza());
            variaveis.put(VariaveisJbpmProcessosGerais.CATEGORIA, processo.getNaturezaCategoriaFluxo().getCategoria().getCategoria());
            variaveis.put(VariaveisJbpmProcessosGerais.NUMERO_PROCESSO, processo.getNumeroProcesso());
        }
        return variaveis;
    }
    
    protected void movimentarProcesso(ProcessInstance processInstance, String transitionName) {
        if (StringUtil.isEmpty(transitionName)) {
            processInstance.signal();
        } else {
            processInstance.signal(transitionName);
        }
    }
    
    protected EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
