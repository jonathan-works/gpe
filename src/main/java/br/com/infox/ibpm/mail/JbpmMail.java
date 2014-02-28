package br.com.infox.ibpm.mail;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import twitter4j.TwitterException;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.command.SendmailCommand;
import br.com.infox.epp.mail.entity.EMailData;
import br.com.infox.epp.mail.manager.ListaEmailManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.epp.twitter.manager.ContaTwitterManager;
import br.com.infox.epp.twitter.manager.TwitterTemplateManager;
import br.com.infox.epp.twitter.util.TwitterUtil;
import br.com.infox.seam.util.ComponentUtil;

public class JbpmMail extends org.jbpm.mail.Mail {
	private static final long serialVersionUID = 1L;
	private Map<String, String> parameters = new HashMap<String, String>();
	private static final LogProvider LOG = Logging.getLogProvider(JbpmMail.class);
	
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
	
	@SuppressWarnings({ UNCHECKED, RAWTYPES })
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
		ModeloDocumentoManager modeloDocumentoManager = ComponentUtil.getComponent(ModeloDocumentoManager.NAME);
		data.setBody(modeloDocumentoManager.getConteudo(Integer.parseInt(parameters.get("idModeloDocumento"))));
		String idGrupo = parameters.get("idGrupo");
		List<String> recipList = null;
		if (idGrupo != null) {
		    ListaEmailManager listaEmailManager = ComponentUtil.getComponent(ListaEmailManager.NAME);
			 recipList = listaEmailManager.resolve(Integer.parseInt(parameters.get("idGrupo")));
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
			TwitterTemplateManager twitterTemplateManager = ComponentUtil.getComponent(TwitterTemplateManager.NAME);
			String mensagem = MessageFormat.format("[{1}] {0}", twitterTemplateManager.find(idTemplate).getMensagem(),
					ProcessoHome.instance().getInstance().getNumeroProcesso());
			try {
			    ContaTwitterManager contaTwitterManager = ComponentUtil.getComponent(ContaTwitterManager.NAME);
				TwitterUtil.getInstance().sendMessage(contaTwitterManager.listaContasTwitter(idGrupo), mensagem);
			} catch (TwitterException e) {
			    LOG.error(".sendTwitter()", e);
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
