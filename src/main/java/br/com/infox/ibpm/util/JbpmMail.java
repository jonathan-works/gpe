package br.com.infox.ibpm.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.TwitterException;
import br.com.infox.command.EMailData;
import br.com.infox.command.SendmailCommand;
import br.com.infox.ibpm.entity.TwitterTemplate;
import br.com.infox.ibpm.home.ProcessoHome;
import br.com.infox.ibpm.jbpm.MailResolver;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.infox.util.TwitterUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

public class JbpmMail extends org.jbpm.mail.Mail {
	private static final long serialVersionUID = 1L;
	private Map<String, String> parameters = new HashMap<String, String>();
	
	
	/**
	 * Método separa conteúdo de saída de um Map e interpreta seus atributos com base
	 * em suas chaves String e atribui a valores referentes ao envio de mensagens
	 * pre-definidas no sistema.
	 */
	private void initMailContent() {
		parameters.putAll(getStringToMap(getText()));

	}
	
	private Map<String,String> getStringToMap(String string) {
		String result = string.substring(1, string.length()-1);
		HashMap<String, String> map = new HashMap<String, String>();
		
		for (String s : result.split(", ")) {
			String[] att = s.split("=");
			if (att.length == 2) {
				map.put(att[0], att[1]);
			}
		}
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private void initRemetentes() {
		List recip = new ArrayList(getRecipients());
				
		if (recip.size()==1) {
			String value = recip.get(0).toString();
			Map<String, String> map = getStringToMap(value);
			
			if (map.size() == 0 && value.contains("@")) {
				parameters.put("mailList", value);
			} else {
				parameters.putAll(map);
			}
		}
	}
	
	private void sendMail() {
		EMailData data = ComponentUtil.getComponent(EMailData.NAME);
		data.setUseHtmlBody(true);
		data.setBody(ModeloDocumentoAction.instance().getConteudo(Integer.parseInt(parameters.get("idModeloDocumento"))));
		String idGrupo = parameters.get("idGrupo");
		List<String> recipList = null;
		if (idGrupo != null) {
			 recipList = MailResolver.instance().resolve(Integer.parseInt(parameters.get("idGrupo")));
		}
		if (parameters.containsKey("mailList")) {
			if (recipList == null) {
				recipList = new ArrayList<String>();
			}
			recipList.add( parameters.get("mailList"));
		}
		data.setJbpmRecipientList(recipList);
		data.setSubject(getSubject());
		new SendmailCommand().execute("/WEB-INF/email/jbpmEmailTemplate.xhtml");
	}
	
	private void sendTwitter() {
		if (parameters.containsKey("idTwitterTemplate")) {
			int idTemplate = Integer.parseInt(parameters.get("idTwitterTemplate"));
			int idGrupo = Integer.parseInt(parameters.get("idGrupo"));
			String mensagem = MessageFormat.format("[{1}] {0}", EntityUtil.find(TwitterTemplate.class, idTemplate).getMensagem(),
					ProcessoHome.instance().getInstance().getNumeroProcesso());
			try {
				TwitterUtil.getInstance().sendMessage(MailResolver.instance().listaContasTwitter(idGrupo), mensagem);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void send() {
		initMailContent();
		initRemetentes();
		sendMail();
		sendTwitter();
	}

}
