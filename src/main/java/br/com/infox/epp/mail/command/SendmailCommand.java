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
package br.com.infox.epp.mail.command;

import static java.text.MessageFormat.format;

import java.security.Security;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;


public class SendmailCommand  {
	
	private static final String SENDMAIL_LOG_PATTERN = ".execute(sendmail): {0}";
    private static final LogProvider LOG = Logging.getLogProvider(SendmailCommand.class);

	public void execute(final String templateFile) {
		final StopWatch sw = new StopWatch();
		sw.start();
		final FacesMessages messages = FacesMessages.instance();
		try {
			setProperty(SSLSocketFactory.class.getName());
			Renderer.instance().render(templateFile);
			messages.add("Email enviado com sucesso.");
			LOG.info(format(SENDMAIL_LOG_PATTERN, sw.getTime()));
		} catch (Exception e) {
			messages.add("Erro ao enviar eMail", e);
			LOG.error(format(SENDMAIL_LOG_PATTERN, sw.getTime()), e);
		} finally {
			setProperty("");
		}
	}

    private void setProperty(final String value) {
        Security.setProperty("ssl.SocketFactory.provider", value);
    }
	
}