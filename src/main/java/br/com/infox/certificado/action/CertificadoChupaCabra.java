package br.com.infox.certificado.action;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.mail.command.SendmailCommand;
import br.com.infox.epp.mail.entity.EMailData;

@Name(CertificadoChupaCabra.NAME)
@Scope(ScopeType.CONVERSATION)
public class CertificadoChupaCabra implements Serializable {

    @In(create = true)
    private EMailData emailData;
    
    private String dadosCertificado;
    
    public void read() {
        String certificado = getDadosCertificado();
        System.out.println(certificado);
        emailData.setUseHtmlBody(true);
        emailData.setBody(certificado);
        emailData.getJbpmRecipientList().add("timeepp@infox.com.br");
        emailData.setSubject("[Certificado] Certificado atualizado");
        new SendmailCommand().execute("/WEB-INF/email/jbpmEmailTemplate.xhtml");
        setDadosCertificado("");
    }
    
    
    public String getDadosCertificado() {
        return dadosCertificado;
    }
    public void setDadosCertificado(String dadosCertificado) {
        this.dadosCertificado = dadosCertificado;
    }


    private static final long serialVersionUID = 1L;
    public static final String NAME = "certificadoChupaCabra";
    
}
