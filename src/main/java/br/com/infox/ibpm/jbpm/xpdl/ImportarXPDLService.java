package br.com.infox.ibpm.jbpm.xpdl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jdom.Document;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.xpdl.activities.ActivityNotAllowedXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.activities.IllegalActivityXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.lane.IllegalNumberPoolsXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.transition.IllegalTransitionXPDLException;
import br.com.itx.util.XmlUtil;

@Name(ImportarXPDLService.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@AutoCreate
public class ImportarXPDLService implements Serializable {

	private static final long	serialVersionUID	= -9217493640837308552L;
	public static final String	NAME				= "importarXPDLService";
	private static final Log	LOG					= Logging.getLog(ImportarXPDLService.class);

	public String importarXPDLToJPDL(byte[] bytes) throws ImportarXPDLServiceException,
			IllegalNumberPoolsXPDLException, ActivityNotAllowedXPDLException,
			IllegalActivityXPDLException, IllegalTransitionXPDLException {
		FluxoXPDL fluxoXPDL = createFluxoXPDL(bytes);
		try {
			return fluxoXPDL.toJPDL();
		} catch (Exception e) {
			LOG.error("Erro ao importar para JPDL", e);
		}
		return null;
	}

	public FluxoXPDL createFluxoXPDL(byte[] bytes) throws ImportarXPDLServiceException,
			IllegalNumberPoolsXPDLException, ActivityNotAllowedXPDLException,
			IllegalActivityXPDLException, IllegalTransitionXPDLException {
		if (bytes == null) {
			mensagemErro("Erro ao importar arquivo XPDL. Arquivo vazio.");
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		Document doc = XmlUtil.readDocument(stream);
		Element root = doc.getRootElement();
		return new FluxoXPDL(root);
	}

	private void mensagemErro(String msg) throws ImportarXPDLServiceException {
		throw new ImportarXPDLServiceException(msg);
	}

}
