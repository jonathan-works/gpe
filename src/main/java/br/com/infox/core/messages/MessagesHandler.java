package br.com.infox.core.messages;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

public class MessagesHandler implements MessagesInterface{
    private static final MessagesHandler instance=init();
    
    private static final synchronized MessagesHandler init() {
        return new MessagesHandler();
    }
    
    public static final MessagesHandler getInstance() {
        return instance;
    }
    
    public static final void add(Severity severity, String message) {
        instance.addMessage(severity, message);
    }

    public static final void add(Severity severity,
            String messageTemplate, Throwable cause) {
        instance.addMessage(severity, messageTemplate, cause);
    }

    public static final void add(String message) {
        instance.addMessage(message);
    }
    
    public static final void clear() {
        instance.clearMessages();
    }
    
    private MessagesHandler() {
    }
    
    public void addMessage(Severity severity, String message) {
        FacesMessages.instance().add(severity, message);
    }

    public void addMessage(Severity severity,
            String messageTemplate, Throwable cause) {
        FacesMessages.instance().add(severity, messageTemplate, cause);
    }

    public void addMessage(String message) {
        FacesMessages.instance().add(message);
    }
    
    public void clearMessages() {
        FacesMessages.instance().clear();
    }
    
}
