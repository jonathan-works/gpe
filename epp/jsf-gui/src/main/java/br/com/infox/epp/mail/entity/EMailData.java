package br.com.infox.epp.mail.entity;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(EMailData.NAME)
@BypassInterceptors
public class EMailData {

    public static final String NAME = "emailData";

    private String fromName;
    private String fromAdress;
    private String recipientName;
    private String recipientAdress;
    private String subject;
    private String body;
    private boolean useHtmlBody = false;
    private List<UsuarioLogin> recipientList = new ArrayList<UsuarioLogin>(0);
    private List<String> jbpmRecipientList = new ArrayList<String>();

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromAdress() {
        return fromAdress;
    }

    public void setFromAdress(String fromAdress) {
        this.fromAdress = fromAdress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAdress() {
        return recipientAdress;
    }

    public void setRecipientAdress(String recipientAdress) {
        this.recipientAdress = recipientAdress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isUseHtmlBody() {
        return useHtmlBody;
    }

    public void setUseHtmlBody(boolean useHtmlBody) {
        this.useHtmlBody = useHtmlBody;
    }

    public List<String> getJbpmRecipientList() {
        return jbpmRecipientList;
    }

    public void setJbpmRecipientList(List<String> jbpmRecipientList) {
        this.jbpmRecipientList = jbpmRecipientList;
    }

    public List<UsuarioLogin> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<UsuarioLogin> recipientsList) {
        this.recipientList = recipientsList;
    }

}
