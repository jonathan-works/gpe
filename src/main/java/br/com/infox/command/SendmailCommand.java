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
package br.com.infox.command;

import java.security.Security;

import javax.net.ssl.SSLSocketFactory;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.util.StopWatch;


public class SendmailCommand  {
	
	private static final LogProvider LOG = Logging.getLogProvider(SendmailCommand.class);

	public void execute(String templateFile) {
		StopWatch sw = new StopWatch(true);
		Renderer renderer = Renderer.instance();
		FacesMessages fm = FacesMessages.instance();
		try {
			String name = SSLSocketFactory.class.getName();
			Security.setProperty( "ssl.SocketFactory.provider", name);
			renderer.render(templateFile);
			String msg = "Email enviado com sucesso.";
			fm.add(msg);
			LOG.info(".execute(sendmail): " + sw.getTime());
		} catch (Exception e) {
			fm.add("Erro ao enviar eMail", e);
			LOG.error(".execute(sendmail): " + sw.getTime(), e);
		} finally {
			Security.setProperty("ssl.SocketFactory.provider", "");
		}
	}
	
}