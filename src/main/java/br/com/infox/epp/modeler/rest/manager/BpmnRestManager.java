package br.com.infox.epp.modeler.rest.manager;

import java.io.StringWriter;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.modeler.converter.BpmnJpdlService;
import br.com.infox.epp.modeler.rest.model.Bpmn;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BpmnRestManager {
	
	@Inject
	private FluxoManager fluxoManager;
	@Inject
	private BpmnJpdlService bpmnJpdlService;
	
	public List<Bpmn> listBpmn() {
		EntityManager entityManager = EntityManagerProducer.getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Bpmn> query = cb.createQuery(Bpmn.class);
		Root<Fluxo> fluxo = query.from(Fluxo.class);
		query.select(cb.construct(Bpmn.class, fluxo));
		query.where(cb.isTrue(fluxo.get(Fluxo_.ativo)), cb.isTrue(fluxo.get(Fluxo_.bpmn)));
		query.orderBy(cb.asc(fluxo.get(Fluxo_.fluxo)));
		return entityManager.createQuery(query).getResultList();
	}
	
	public Bpmn getBpmn(Integer id) {
		EntityManager entityManager = EntityManagerProducer.getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Bpmn> query = cb.createQuery(Bpmn.class);
		Root<Fluxo> fluxo = query.from(Fluxo.class);
		query.select(cb.construct(Bpmn.class, fluxo));
		query.where(cb.equal(fluxo.get(Fluxo_.idFluxo), id));
		return entityManager.createQuery(query).getSingleResult();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateBpmn(Bpmn bpmn) {
		Fluxo fluxo = fluxoManager.find(bpmn.getId());
		fluxo.setBpmnXml(bpmn.getBpmn());
		fluxo.setSvg(bpmn.getSvg());
		ProcessDefinition processDefinition = bpmnJpdlService.getUpdatedJbpmDefinitionFromBpmn(fluxo);
		StringWriter writer = new StringWriter();
		new JpdlXmlWriter(writer).write(processDefinition);
		fluxo.setXml(writer.toString());
		fluxoManager.update(fluxo);
	}
}
