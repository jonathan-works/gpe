package br.com.infox.epp.fluxo.definicao;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.Problem;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.definicao.modeler.BpmnJpdlService;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class BpmnView implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(BpmnView.class);
	
	@Inject
	private BpmnJpdlService bpmnJpdlService;
	@Inject
	private DefinicaoProcessoController definicaoProcessoController;
	
	private String bpmnInformation;
	private String elementKey;

	public DefinicaoProcesso getDefinicaoProcesso() {
	    return definicaoProcessoController.getDefinicaoProcesso();
	}
	
	@ExceptionHandled
	public void update() {
		DefinicaoProcesso definicaoProcesso = getDefinicaoProcesso();
		JsonObject bpmnInfo = new Gson().fromJson(bpmnInformation, JsonObject.class);
		
		String newProcessDefinitionXml = JpdlXmlWriter.toString(ProcessBuilder.instance().getInstance());
		String newBpmnXml = bpmnInfo.get("bpmn").getAsString();
		newBpmnXml = Bpmn.convertToString(Bpmn.readModelFromStream(new ByteArrayInputStream(newBpmnXml.getBytes(StandardCharsets.UTF_8))));
		
		if (!newProcessDefinitionXml.equals(definicaoProcesso.getXml()) || !newBpmnXml.equals(definicaoProcesso.getBpmn())) {
			try {
				definicaoProcessoController.setDefinicaoProcesso(
			        bpmnJpdlService.atualizarDefinicao(definicaoProcesso, newProcessDefinitionXml, newBpmnXml, bpmnInfo.get("svg").getAsString())
		        );
				
				definicaoProcesso = getDefinicaoProcesso();
			} catch (JpdlException e) {
				logJpdlException(e);
				return;
			}
			String currentNodeKey = ProcessBuilder.instance().getNodeFitter().getCurrentNode() != null ? ProcessBuilder.instance().getNodeFitter().getCurrentNode().getKey() : null;
			ProcessBuilder.instance().load(definicaoProcesso);
			ProcessBuilder.instance().getNodeFitter().setCurrentNodeByKey(currentNodeKey);
			bpmnInformation = null;
			FacesMessages.instance().add("Fluxo salvo com sucesso!");
		}
	}
	
	public void configureElement() {
		ProcessBuilder.instance().setTab("nodesTab");
		ProcessBuilder.instance().getNodeFitter().setCurrentNodeByKey(elementKey);
		elementKey = null;
	}
	
	public String getBpmnInformation() {
		return bpmnInformation;
	}
	
	public void setBpmnInformation(String bpmnInformation) {
		this.bpmnInformation = bpmnInformation;
	}
	
	public String getElementKey() {
		return elementKey;
	}
	
	public void setElementKey(String elementKey) {
		this.elementKey = elementKey;
	}
	
	@SuppressWarnings("unchecked")
	private void logJpdlException(JpdlException e) {
		for (Problem problem : (List<Problem>) e.getProblems()) {
			int problemLevel = problem.getLevel();
			if (problemLevel == Problem.LEVEL_FATAL || problemLevel == Problem.LEVEL_ERROR){
				FacesMessages.instance().add(Severity.ERROR, problem.getDescription());
				LOG.error(problem);
			} else if (problemLevel == Problem.LEVEL_WARNING){
				LOG.warn(problem);
			} else {
				LOG.info(problem);
			}
		}
	}
}