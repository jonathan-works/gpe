package br.com.infox.core.messages;

import org.jboss.seam.international.StatusMessage.Severity;

public interface MessagesInterface {
    public void addMessage(String message);
    public void addMessage(Severity severity, String message);
    public void addMessage(Severity severity,
            String messageTemplate, Throwable cause);
    public void clearMessages();
}
