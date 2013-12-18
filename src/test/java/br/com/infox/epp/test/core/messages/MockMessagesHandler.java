package br.com.infox.epp.test.core.messages;

import java.text.MessageFormat;

import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.messages.MessagesInterface;

public class MockMessagesHandler implements MessagesInterface {
    private static final MockMessagesHandler instance=init();
    
    private static final synchronized MockMessagesHandler init() {
        return new MockMessagesHandler();
    }
    
    public static final MockMessagesHandler getInstance() {
        return instance;
    }

    public void addMessage(String message) {
        System.out.println(message);
    }
    
    public void addMessage(Severity severity, String message) {
        System.out.println(MessageFormat.format("[{0}] {1}", severity, message));
    }

    public void addMessage(Severity severity,
            String messageTemplate, Throwable cause) {
        System.out.println(MessageFormat.format("[{0}] {1}", severity, messageTemplate, cause));
    }
    
    public void clearMessages() {
    }
    
}
