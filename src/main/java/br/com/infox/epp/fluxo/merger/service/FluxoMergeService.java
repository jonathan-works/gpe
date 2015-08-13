package br.com.infox.epp.fluxo.merger.service;

import java.io.StringReader;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jbpm.graph.def.ProcessDefinition;
import org.xml.sax.InputSource;

import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.merger.model.MergePoint;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class FluxoMergeService {

    @Inject
    private EntityManager entityManager;
    @Inject
    private ProcessBuilder processBuilder;

    public List<MergePoint> getMergePoints(Fluxo fluxo) {
        TypedQuery<MergePoint> query = entityManager.createQuery(GET_MERGE_POINTS_QUERY, MergePoint.class);
        TypedQuery<MergePoint> parameter = query.setParameter(PROCESS_INSTANCE, fluxo.getFluxo());
        return parameter.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MergePointsBundle merge(Fluxo base, Fluxo reference) {
        ProcessDefinition processDefinition = jpdlToProcessDefinition(reference.getXml());
        MergePointsBundle mergePointsBundle = new MergePointsBundle(getMergePoints(base), processDefinition);
        if (mergePointsBundle.isValid()){
            processDefinition.setName(base.getFluxo());
            processBuilder.load(base);
            processBuilder.setInstance(processDefinition);
            processBuilder.deploy();
        }
        return mergePointsBundle;
    }

    public ProcessDefinition jpdlToProcessDefinition(String xml) {
        StringReader stringReader = new StringReader(xml);
        InfoxJpdlXmlReader jpdlReader = new InfoxJpdlXmlReader(new InputSource(stringReader));
        return jpdlReader.readProcessDefinition();
    }

    private static final String PROCESS_INSTANCE = "processDefinitionName";
    private static final String GET_MERGE_POINTS_QUERY = "select new br.com.infox.epp.fluxo.merger.model.MergePoint(node.name, count(distinct tok.id))"
            + " from org.jbpm.graph.exe.Token tok"
            + " inner join tok.processInstance procIns"
            + " inner join procIns.processDefinition pd"
            + " inner join tok.node node"
            + " where pd.name = :"+ PROCESS_INSTANCE
            + " and"
            + " tok.end IS NULL"
            + " group by node.name";
}
