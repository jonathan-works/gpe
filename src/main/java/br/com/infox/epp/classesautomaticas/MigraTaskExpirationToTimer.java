package br.com.infox.epp.classesautomaticas;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.TransactionManager;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.contexts.Lifecycle;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.merger.service.FluxoMergeService;
import br.com.infox.log.Logging;

/**
 * Classe temporária para migrar as task expirations para os timers do jbpm
 * @author malu
 *
 */
public class MigraTaskExpirationToTimer implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "migraTaskExpiration";
	
	private EntityManager entityManager;
	private ControleClassesAutomaticas controle;
	
	private MigraTaskExpirationToTimer () {
	}
	
	public static MigraTaskExpirationToTimer instance() {
		return new MigraTaskExpirationToTimer();
	}
	
	public void init() throws Exception {
		TransactionManager transactionManager = ApplicationServerService.instance().getTransactionManager();
		transactionManager.begin();
		transactionManager.setTransactionTimeout(3600);
		Lifecycle.beginCall();
		entityManager = EntityManagerProducer.instance().getEntityManagerTransactional();
		try {
			if (isExecucaoValida()) {
				migraTaskExpirationsDefinicao();
				deleteTaskExpirations();
				updateExecucao();
			}
			transactionManager.commit();
		} catch (Exception e) {
			Logging.getLogProvider(MigraTaskExpirationToTimer.class).error("", e);
			transactionManager.rollback();
			throw e;
		} finally {
			Lifecycle.endCall();
		}
		
	}
	
	private EntityManager getEntityManager() {
		return entityManager;
	}

	private boolean isExecucaoValida() {
        try {
            controle = getControle();
			return controle.isExecutar();
        } catch (NoResultException e) {
        	controle = new ControleClassesAutomaticas();
        	controle.setNomeClasse(NAME);
        	controle.setExecutar(true);
            getEntityManager().persist(controle);
            return true;
        }
    }
	
	private ControleClassesAutomaticas getControle() {
		return getEntityManager().createQuery("select cca from ControleClassesAutomaticas cca where cca.nomeClasse = ?", ControleClassesAutomaticas.class)
				.setParameter(1, NAME).getSingleResult();
	}

	private void updateExecucao() {
		controle.setExecutar(false);
		getEntityManager().merge(controle);
	}
	
	public void migraTaskExpirationsDefinicao() throws Exception {
		for (Fluxo fluxo : getFluxos()) {
			List<Object[]> taskExpirations = getTaskExpirationsByIdFluxo(fluxo.getIdFluxo());
			if (taskExpirations != null && !taskExpirations.isEmpty()) {
				if (fluxo.getDefinicaoProcesso().getXmlExecucao() != null && !fluxo.getDefinicaoProcesso().getXmlExecucao().isEmpty()) {
					String xmlExecucao = adicionaTimer(fluxo.getDefinicaoProcesso().getXmlExecucao(), taskExpirations);
					if (!fluxo.getDefinicaoProcesso().getXmlExecucao().equals(xmlExecucao)) {
						fluxo.getDefinicaoProcesso().setXmlExecucao(xmlExecucao);
					}
				}
				if (fluxo.getDefinicaoProcesso().getXml() != null && !fluxo.getDefinicaoProcesso().getXml().isEmpty()) {
					String xml = adicionaTimer(fluxo.getDefinicaoProcesso().getXml(), taskExpirations);
					if (!fluxo.getDefinicaoProcesso().getXml().equals(xml)) {
						fluxo.getDefinicaoProcesso().setXml(xml);
					}
				}
			}
			if (fluxo.getDefinicaoProcesso().getXmlExecucao() != null && !fluxo.getDefinicaoProcesso().getXmlExecucao().isEmpty()) {
				fluxo = publishTimerToDefinition(fluxo);
			}
			getEntityManager().merge(fluxo);
		}
	}
	
	private Fluxo publishTimerToDefinition(Fluxo fluxo) {
		String xmlEdicao = fluxo.getDefinicaoProcesso().getXml();
		fluxo.getDefinicaoProcesso().setXml(fluxo.getDefinicaoProcesso().getXmlExecucao());
		fluxo.getDefinicaoProcesso().setXmlExecucao(null);
		getFluxoMergeService().publish(fluxo, null);
		getEntityManager().detach(fluxo.getDefinicaoProcesso());
		getEntityManager().detach(fluxo);
		fluxo = getEntityManager().find(Fluxo.class, fluxo.getIdFluxo());
		fluxo.getDefinicaoProcesso().setXml(xmlEdicao);
		return fluxo;
	}
	
	private String adicionaTimer(String xml, List<Object[]> taskExpirations) throws Exception {
        InputStream inputStream = IOUtils.toInputStream(xml, "UTF-8");
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(inputStream);
        
        IteratorIterable<Element> taskNodes = doc.getDescendants(new ElementFilter("task-node"));
        for (Object[] tskExp : taskExpirations) {
			String taskName = (String) tskExp[0];
			String transitionName = (String) tskExp[1];
			String dueDate = new SimpleDateFormat(CreateTimerAction.DUEDATE_FORMAT).format(tskExp[2]);
			
			for (Element taskNode : taskNodes) {
    			if (taskNode.getAttributeValue("name").equals(taskName)) {
                	Element timer = new Element("timer", taskNode.getNamespace());
                    timer.setAttribute("name", "taskExpiration " + transitionName);
                    timer.setAttribute("duedate", dueDate);
                    timer.setAttribute("transition", transitionName);
                    taskNode.addContent(timer);
                    break;
                }
			}
        }
        return convertNewXml(doc);
    }
	
	private String convertNewXml(Document doc) throws IOException {
        StringWriter stringWriter = new StringWriter();
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
        outputter.output(doc, stringWriter);
        stringWriter.flush();
        return stringWriter.toString();
    }
	
	private List<Fluxo> getFluxos() {
		List<Fluxo> resultList = getEntityManager().createQuery("select f from Fluxo f", Fluxo.class).getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return new ArrayList<Fluxo>();
	}
	
	@SuppressWarnings("unchecked")
	private List<Object[]> getTaskExpirationsByIdFluxo(Integer idFluxo) {
		String sql = "select te.nm_tarefa, te.ds_transition, te.dt_expiration from tb_task_expiration te "
				+ "where te.id_fluxo = ?";
		EntityManager entityManager = getEntityManager();
		Query nativeQuery = entityManager.createNativeQuery(sql);
		List<Object[]> resultList = nativeQuery.setParameter(1, idFluxo).getResultList();
		return resultList;
	}
	
	private void deleteTaskExpirations() {
		String sql = "delete from tb_task_expiration";
		getEntityManager().createNativeQuery(sql).executeUpdate();
		
	}
	
	private FluxoMergeService getFluxoMergeService() {
		return BeanManager.INSTANCE.getReference(FluxoMergeService.class);
	}

}
