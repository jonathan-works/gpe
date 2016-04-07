package br.com.infox.epp.fluxo.merger.service;

import java.io.StringReader;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.xml.sax.InputSource;

import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.cdi.transaction.Transactional.TxType;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.merger.model.MergePoint;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FluxoMergeService {

    @Inject
    private EntityManager entityManager;
    @Inject
    private ProcessBuilder processBuilder;

    private List<MergePoint> getMergePoints(ProcessDefinition processDefinition){
        TypedQuery<MergePoint> query = entityManager.createQuery(GET_MERGE_POINTS_QUERY, MergePoint.class);
        query = query.setParameter(PROCESS_INSTANCE, processDefinition.getName());
        return query.getResultList();
    }

    public MergePointsBundle verifyMerge(String xmlBase, String xmlReference){
        ProcessDefinition base = jpdlToProcessDefinition(xmlBase);
        ProcessDefinition reference = jpdlToProcessDefinition(xmlReference);
        return verifyMerge(base, reference);
    }
    
    public MergePointsBundle verifyMerge(ProcessDefinition base, ProcessDefinition reference){
        return new MergePointsBundle(getMergePoints(base), reference);
    }
    
    public MergePointsBundle verifyMerge(Fluxo fluxo){
        String modifiedXml = fluxo.getXml();
        String publishedXml = fluxo.getXmlExecucao();
        if (Objects.equals(modifiedXml, publishedXml)) {
            return null;
        }
        ProcessDefinition modifiedProcessDef = jpdlToProcessDefinition(modifiedXml);
        ProcessDefinition publishedProcessDef = jpdlToProcessDefinition(publishedXml);
        return verifyMerge(publishedProcessDef, modifiedProcessDef);
    }
    
    public ProcessDefinition jpdlToProcessDefinition(String xml) {
        StringReader stringReader = new StringReader(xml);
        InfoxJpdlXmlReader jpdlReader = new InfoxJpdlXmlReader(new InputSource(stringReader));
        return xml == null || xml.isEmpty() ? null : jpdlReader.readProcessDefinition();
    }

    public boolean hasActiveNode(ProcessDefinition processDefinition, Node node){
        TypedQuery<Node> query = entityManager.createQuery(GET_ACTIVE_NODE, Node.class);
        query = query.setParameter(PROCESS_INSTANCE, processDefinition.getName());
        query = query.setParameter(NODE_NAME, node.getName());
        List<Node> nodes = query.getResultList();
        return nodes != null && nodes.size() > 0;
    }
    
    @Transactional(value = TxType.REQUIRED, timeout = 1800)
    public MergePointsBundle publish(Fluxo fluxo, MergePointsBundle mergePointsBundle) {
        String modifiedXml = fluxo.getXml();
        String publishedXml = fluxo.getXmlExecucao();
        if (publishedXml == null || publishedXml.isEmpty()) {
            ProcessDefinition modifiedProcessDef = jpdlToProcessDefinition(modifiedXml);
            modifiedProcessDef.setName(fluxo.getFluxo());
            processBuilder.load(fluxo);
            processBuilder.setInstance(modifiedProcessDef);
            boolean isDeployed = processBuilder.deploy();
            if(!isDeployed){
                throw new RuntimeException("Erro ao executar deploy!");
            }
        } else if (!Objects.equals(modifiedXml, publishedXml)) {
            ProcessDefinition modifiedProcessDef = jpdlToProcessDefinition(modifiedXml);
            ProcessDefinition publishedProcessDef = jpdlToProcessDefinition(publishedXml);
            mergePointsBundle = verifyMerge(publishedProcessDef, modifiedProcessDef);
            if (mergePointsBundle.isValid()) {
                modifiedProcessDef.setName(fluxo.getFluxo());
                processBuilder.load(fluxo);
                processBuilder.setInstance(modifiedProcessDef);
                boolean isDeployed = processBuilder.deploy();
                if(!isDeployed){
                    throw new RuntimeException("Erro ao executar deploy!");
                }
            }
            return mergePointsBundle;
        }
        return mergePointsBundle;
    }
    
    private static final String PROCESS_INSTANCE = "processDefinitionName";
    private static final String NODE_NAME = "node_name";
    
    private static final String GET_MERGE_POINTS_QUERY = "select new br.com.infox.epp.fluxo.merger.model.MergePoint(node.name, count(distinct tok.id))"
            + " from org.jbpm.graph.exe.Token tok"
            + " inner join tok.processInstance procIns"
            + " inner join procIns.processDefinition pd"
            + " inner join tok.node node"
            + " where pd.name = :"+ PROCESS_INSTANCE
            + " and"
            + " tok.end IS NULL"
            + " group by node.name";
    
    private static final String GET_ACTIVE_NODE = "select node"
            + " from org.jbpm.graph.exe.Token tok"
            + " inner join tok.processInstance procIns"
            + " inner join procIns.processDefinition pd"
            + " inner join tok.node node"
            + " where pd.name = :"+ PROCESS_INSTANCE
            + " and"
            + " tok.end IS NULL"
            + " AND node.name = :" + NODE_NAME;
}
