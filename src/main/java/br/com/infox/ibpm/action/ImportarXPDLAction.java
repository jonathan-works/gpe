package br.com.infox.ibpm.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.xpdl.IllegalXPDLException;

@Name(ImportarXPDLAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ImportarXPDLAction {

	public static final String NAME = "importarXPDLAction";
	private static final Log LOG = Logging.getLog(ImportarXPDLAction.class);

	public void importarXPDL(byte[] bytes, String cdFluxo) {
		try {
			FluxoXPDL fluxoXPDL = FluxoXPDL.createInstance(bytes);
			String xml = fluxoXPDL.toJPDL(cdFluxo);
			redirectToProcessDefinition(cdFluxo, xml);
		} catch (IllegalXPDLException e) {
			LOG.error("Erro ao importar arquivo XPDL. " + e.getMessage());
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
