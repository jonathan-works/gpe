/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.command;

import java.security.Security;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;


public class SendmailCommand  {
	
	private static final LogProvider LOG = Logging.getLogProvider(SendmailCommand.class);

	public void execute(String templateFile) {
		StopWatch sw = new StopWatch();
		sw.start();
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