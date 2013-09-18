/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.util;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.entity.Parametro;
import br.com.itx.util.EntityUtil;


@Name(CarregarParametrosAplicacao.NAME)
@Scope(ScopeType.APPLICATION)
@Install()
@Startup()
@BypassInterceptors
public class CarregarParametrosAplicacao {
	
	public static final String NAME = "carregarParametrosAplicacao";
	private static final LogProvider LOG = Logging.getLogProvider(CarregarParametrosAplicacao.class);
	
	@Create
	public void init() {
		StopWatch sw = new StopWatch();
		sw.start();
		for (Parametro parametro : getParametroAtivoList()) {
			Contexts.getApplicationContext().set(parametro.getNomeVariavel().trim(), 
					parametro.getValorVariavel());
		}
		LOG.info(".init(): " + sw.getTime());
	}
	
	@SuppressWarnings("unchecked")
	private List<Parametro> getParametroAtivoList() {
		String hql = "select o from Parametro o where o.ativo = true";
		return EntityUtil.createQuery(hql).getResultList();
	}
	
}