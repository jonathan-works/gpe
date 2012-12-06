package br.com.infox.ibpm.util;

import br.com.infox.command.EMailData;
import br.com.infox.command.SendmailCommand;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.itx.util.ComponentUtil;

public class JbpmMail extends org.jbpm.mail.Mail {
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getText() {
		String conteudo = "";
		try {
			conteudo = ModeloDocumentoAction.instance().getConteudo(Integer.parseInt(super.getText()));
		} catch (NumberFormatException e) {
			
		}
		return conteudo;
	}
	
	@Override
	public void send() {
		EMailData data = ComponentUtil.getComponent(EMailData.NAME);
		data.setUseHtmlBody(true);
		data.setBody(getText());
		
		data.setJbpmRecipientList(getRecipients());
		data.setSubject(getSubject());
		new SendmailCommand().execute("/WEB-INF/email/jbpmEmailTemplate.xhtml");
	}

}
