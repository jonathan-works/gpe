package br.com.infox.ibpm.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.jbpm.ProcessBuilder;

import br.com.infox.ibpm.xpdl.ImportarXPDLService;
import br.com.infox.ibpm.xpdl.ImportarXPDLServiceException;
import br.com.infox.ibpm.xpdl.activities.ActivityNotAllowedXPDLException;
import br.com.infox.ibpm.xpdl.activities.IllegalActivityXPDLException;
import br.com.infox.ibpm.xpdl.lane.IllegalNumberPoolsXPDLException;
import br.com.infox.ibpm.xpdl.transition.IllegalTransitionXPDLException;

@Name(ImportarXPDLAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ImportarXPDLAction {

	public static final String NAME = "importarXPDLAction";
	private static final Log LOG = Logging.getLog(ImportarXPDLAction.class);

	@In
	private ImportarXPDLService importarXPDLService;

	public void importarXPDL(byte[] bytes, String cdFluxo) throws IllegalNumberPoolsXPDLException, ActivityNotAllowedXPDLException, IllegalActivityXPDLException,	 IllegalTransitionXPDLException {
		try {
			String xml = importarXPDLService.importarXPDLToJPDL(bytes, cdFluxo);
			redirectToProcessDefinition(cdFluxo, xml);
		} catch (ImportarXPDLServiceException e) {
			LOG.error("Erro ao importar arquivo XPDL." + e.getMessage());
			FacesMessages.instance().add(Severity.INFO, e.getMessage());
		}
	}

	private void redirectToProcessDefinition(String cdFluxo, String xml) {
		Redirect redirect = Redirect.instance();
		redirect.setParameter("id", cdFluxo);
		redirect.setParameter("tab", "Propriedades");
		redirect.setParameter("nodeIndex", "0");
		redirect.setViewId("/Fluxo/definicao/processDefinition.xhtml");

		ProcessBuilder process = ProcessBuilder.instance();
		process.setXml(xml);
		process.updateFluxo();
		redirect.execute();
	}
}
