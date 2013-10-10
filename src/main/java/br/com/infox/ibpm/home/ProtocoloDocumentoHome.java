package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.ProtocoloDocumento;
import br.com.itx.component.AbstractHome;

@Name(ProtocoloDocumentoHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class ProtocoloDocumentoHome extends AbstractHome<ProtocoloDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "protocoloDocumentoHome";

}
