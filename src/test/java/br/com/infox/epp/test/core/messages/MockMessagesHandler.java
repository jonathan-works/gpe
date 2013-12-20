package br.com.infox.epp.test.core.messages;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

public class MockMessagesHandler extends StatusMessages {
    private static final long serialVersionUID = 0L;
    private static final MockMessagesHandler mockHandlerInstance = init();
    
    private static final synchronized MockMessagesHandler init() {
        return new MockMessagesHandler();
    }
    
    public static final MockMessagesHandler instance() {
        return mockHandlerInstance;
    }
    
    private HashMap<String, ArrayList<String>> keyedMessages;
    private ArrayList<String> messages;
    
    private MockMessagesHandler() {
        keyedMessages = new HashMap<>();
        messages = new ArrayList<>();
    }
    
    @Override
    public void clearGlobalMessages() {
        dumpGlobalMessages();
        messages.clear();
    }
    
    @Override
    public void clearKeyedMessages(final String id) {
        dumpKeyedMessages(id);
        final ArrayList<String> arrayList = keyedMessages.get(id);
        if (arrayList != null) {
            arrayList.clear();
        }
    }
    
    private void clearKeyedMessages() {
        dumpKeyedMessages();
        getKeyedMessages().clear();
    }
    
    private void addToList(Severity severity, String value, ArrayList<String> list, Object... params) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(MessageFormat.format(value.replaceAll("#{", "#\\{"), params));
        } catch (Exception e) {
            builder.append(value);
        }
        list.add(MessageFormat.format("[{0}] {1}",severity, builder.toString()));
    }
    
    @Override
    public void add(Severity severity, String key, String detailKey, String messageTemplate, String messageDetailTemplate, final Object... params) {
        addToList(severity, messageTemplate, messages, params);
    }
    
    @Override
    public void addToControl(String id, Severity severity, String key, String messageTemplate, final Object... params) {
        ArrayList<String> list;
        if (keyedMessages.containsKey(id)) {
            list = keyedMessages.get(id);
        } else {
            list = new ArrayList<>();
            keyedMessages.put(id, list);
        }
        addToList(severity, messageTemplate, list, params);
    }
    
    @Override
    public void clear() {
        clearGlobalMessages();
        clearKeyedMessages();
    }
    
    private void dumpGlobalMessages() {
//        for (String statusMessage : messages) {
//            System.out.println(statusMessage);
//        }
    }
    
    private void dumpKeyedMessages(String key) {
//        dumpKeyedMessages(this.keyedMessages, key);
    }
    
    private void dumpKeyedMessages() {
        final Set<String> messageKeys = this.keyedMessages.keySet();
        for (String key : messageKeys) {
            dumpKeyedMessages(this.keyedMessages, key);
        }
    }

    private void dumpKeyedMessages(
            final HashMap<String, ArrayList<String>> keyedMessages, String key) {
//        System.out.println("["+key+"]");
//        final ArrayList<String> list = keyedMessages.get(key);
//        for (String statusMessage : list) {
//            System.out.println(statusMessage);
//        }
    }
    
    public boolean keyedMessagesContains(String id, String message) {
        boolean result = false;
        final ArrayList<String> arrayList = keyedMessages.get(id);
        if (arrayList != null) {
            result = arrayList.contains(message);
        }
        return result;
    }
    
    public boolean globalMessagesContains(String message) {
        return messages.contains(message);
    }
    
}