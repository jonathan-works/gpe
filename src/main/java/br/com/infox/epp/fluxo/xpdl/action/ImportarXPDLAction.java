package br.com.infox.epp.fluxo.xpdl.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.fluxo.xpdl.FluxoXPDL;
import br.com.infox.epp.fluxo.xpdl.IllegalXPDLException;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Name(ImportarXPDLAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ImportarXPDLAction {

	public static final String NAME = "importarXPDLAction";
	private static final Log LOG = Logging.getLog(ImportarXPDLAction.class);

	public void importarXPDL(byte[] bytes, String cdFluxo) {
		try {
			FluxoXPDL fluxoXPDL = FluxoXPDL.createInstance(bytes);
			String xml = fluxoXPDL.toJPDL(cdFluxo);
			ProcessBuilder process = ProcessBuilder.instance();
			process.setXml(xml); 
			process.updateFluxo(cdFluxo);
		} catch (IllegalXPDLException e) {
			LOG.error("Erro ao importar arquivo XPDL. " + e.getMessage());
		}
	}

}
