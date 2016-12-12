package br.com.infox.epp.mail.command;

import static java.text.MessageFormat.format;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

public class SendmailCommand {

    private static final String SENDMAIL_LOG_PATTERN = ".execute(sendmail): {0}";
    private static final LogProvider LOG = Logging.getLogProvider(SendmailCommand.class);

    public void execute(final String templateFile) {
        final StopWatch sw = new StopWatch();
        sw.start();
        final FacesMessages messages = FacesMessages.instance();
        try {
            Renderer.instance().render(templateFile);
            messages.add("Email enviado com sucesso.");
            LOG.info(format(SENDMAIL_LOG_PATTERN, sw.getTime()));
        } catch (Exception e) {
            LOG.error(format(SENDMAIL_LOG_PATTERN, sw.getTime()), e);
            throw new BusinessException("Erro ao enviar eMail", e);
        }
    }
}
