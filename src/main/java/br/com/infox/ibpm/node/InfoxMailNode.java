/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.node;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.JbpmContext;
import org.jbpm.graph.action.ActionTypes;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.MailNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.persistence.db.DbPersistenceService;

import br.com.infox.core.tree.TreeHandler;
import br.com.infox.epp.access.component.tree.EstruturaTreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.mail.entity.ListaEmail;
import br.com.infox.epp.mail.home.ListaEmailHome;
import br.com.infox.epp.mail.manager.ListaEmailManager;
import br.com.infox.epp.twitter.entity.TwitterTemplate;
import br.com.infox.epp.twitter.manager.TwitterTemplateManager;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

public class InfoxMailNode extends MailNode {

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(InfoxMailNode.class);
	
	private String subject;
	private String to;
	private String actors;
	private String template;
	private ModeloDocumento modeloDocumento;
	private TwitterTemplate modeloTwitter;
	private Boolean usaTwitter;
	private int idGrupo;
	private List<ListaEmail> listaEmail;
	private ListaEmail currentListaEmail = new ListaEmail();

	@Override
	public void raiseException(Throwable exception, ExecutionContext executionContext) {
		JbpmContext jbpmContext = executionContext.getJbpmContext();
		DbPersistenceService persistenceService = (DbPersistenceService) jbpmContext.getServices().getPersistenceService();
		persistenceService.endTransaction();
		persistenceService.beginTransaction();
		super.raiseException(exception, executionContext);
		persistenceService.endTransaction();
		LOG.warn(MessageFormat.format("Erro ao enviar email (nó {0})", getName()), exception);
	}
	
	@Override
	public void read(Element element, JpdlXmlReader jpdlReader) {
		template = element.attributeValue("template");
		actors = element.attributeValue("actors");
		to = element.attributeValue("to");
		subject = jpdlReader.getProperty("subject", element);
		initMailContent(jpdlReader.getProperty("text", element));
		
		super.read(element, jpdlReader);
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public void write(Element nodeElement) {
		if (action != null) {
			String actionName = ActionTypes.getActionName(action.getClass());
			Element actionElement = nodeElement.addElement(actionName);
			actionElement.detach();
			action.write(actionElement);
			List<Element> content = actionElement.elements();
			for (Element e : content) {
				String name = e.getName();
				if ("to".equals(name) || "template".equals(name) || "actors".equals(name)) {
					nodeElement.addAttribute(name, e.getTextTrim());
				} else {
					Element element = nodeElement.addElement(e.getName());
					element.setAttributes(e.attributes());
					element.addCDATA(e.getText());
				}
			}
		}
	}

	private void initMailContent(String parametros) {
		if (parametros == null || parametros.length() < 3) {
			return;
		}
		String result = parametros.substring(1, parametros.length()-1);
		
		for (String string : result.split(", ")) {
			String[] att = string.split("=");
			if (att.length < 2) {
				continue;
			} else if ("idModeloDocumento".equals(att[0])) {
				modeloDocumento = modeloDocumentoManager().find(Integer.parseInt(att[1]));
			} else if ("idTwitterTemplate".equals(att[0]))	{
				modeloTwitter = twitterTemplateManager().find(Integer.parseInt(att[1]));
				usaTwitter = true;
			}
		}
	}
	
	private ModeloDocumentoManager modeloDocumentoManager(){
	    return ComponentUtil.getComponent(ModeloDocumentoManager.NAME);
	}
	
	private TwitterTemplateManager twitterTemplateManager(){
	    return ComponentUtil.getComponent(TwitterTemplateManager.NAME);
	}
	
	/**
	 * Método que lista atributos em um Map<K,V> e devolve uma saída
	 * no formato "{Chave1=Valor1, Chave2=Valor2, ..., ChaveN=ValorN}"
	 * @return String formatada com dados referentes às mensagens InfoxMailNode.
	 */
	private String getParametros() {
		Map<String, String> parametros = new HashMap<String, String>();
		if (modeloDocumento != null) {
			parametros.put("idModeloDocumento", String.valueOf(modeloDocumento.getIdModeloDocumento()));
		}
		if (modeloTwitter != null && usaTwitter) {
			parametros.put("idTwitterTemplate", String.valueOf(modeloTwitter.getIdTwitterTemplate()));
		}
		return parametros.toString();
	}
	
	private void createAction() {
		JpdlXmlReader jpdlReader = new JpdlXmlReader(new StringReader(""));
		Delegation delegation = jpdlReader.readMailDelegation(createMailElement());
		delegation.setProcessDefinition(this.getProcessDefinition());
		this.action = new Action(delegation);
	}
	
	private Element createMailElement(){
		Element element = DocumentHelper.createElement("mailElement");
		element.addAttribute("template", template);
		element.addAttribute("actors", actors);
		element.addAttribute("to", to);
		element.addAttribute("subject", subject);
		element.addAttribute("text", getParametros());
		return element;
	}

	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}
	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
		createAction();
	}

	public Boolean getUsaTwitter() {
		if (usaTwitter == null) {
			usaTwitter = false;
		}
			
		return this.usaTwitter;
	}

	public void setUsaTwitter(Boolean usaTwitter) {
		this.usaTwitter = usaTwitter;
	}
	
	public TwitterTemplate getModeloTwitter() {
		return modeloTwitter;
	}
	public void setModeloTwitter(TwitterTemplate modeloTwitter) {
		this.modeloTwitter = modeloTwitter;
		createAction();
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
		createAction();
	}

	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
		createAction();
	}

	public String getActors() {
		return actors;
	}
	public void setActors(String actors) {
		this.actors = actors;
		createAction();
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
		createAction();
	}
	
	public List<ListaEmail> getListaEmail() {
		if (listaEmail == null && to != null) {
			String result = to.substring(1, to.length()-1);
			for (String string : result.split(", ")) {
				String[] att = string.split("=");
				if (att.length < 2) {
					continue;
				} else if ("idGrupo".equals(att[0])) {
				    listaEmail = listaEmailManager().getListaEmailByIdGrupoEmail(Integer.parseInt(att[1]));
				}
			}
		}
		return listaEmail;
	}
	
	public ListaEmail getCurrentListaEmail() {
		return currentListaEmail;
	}

	public void removeListaEmail(ListaEmail listaEmail) {
		EntityUtil.getEntityManager().remove(listaEmail);
		EntityUtil.getEntityManager().flush();
		this.listaEmail.remove(listaEmail);
		if (this.listaEmail.isEmpty()) {
			to = "";
		}
	}

	public void addNewEmail() {
        if (currentListaEmail == null || (currentListaEmail.getEstrutura() == null && currentListaEmail.getLocalizacao() == null && currentListaEmail.getPapel() == null)) {
            FacesMessages.instance().clearGlobalMessages();
            FacesMessages.instance().add("Pelo menos um dos campos de destinatário é obrigatório");
            return;
        }
        
        if (idGrupo == 0) {
            getListaEmail();
            if (this.listaEmail != null && !this.listaEmail.isEmpty()) {
                idGrupo = this.listaEmail.get(0).getIdGrupoEmail();
            } else {
                Integer singleResult = listaEmailManager().getMaxIdGrupoEmailInListaEmail();
                if(singleResult != null) {
                    idGrupo = singleResult;
                }
                idGrupo++;
            }
        }
        currentListaEmail.setIdGrupoEmail(idGrupo);
        if(listaEmail == null) {
            listaEmail = new ArrayList<ListaEmail>();
        }
        this.listaEmail.add(currentListaEmail);
        ListaEmailHome home = ListaEmailHome.instance();
        home.setInstance(currentListaEmail);
        home.persist();
        
        currentListaEmail = new ListaEmail();
        to = MessageFormat.format("'{'idGrupo={0}'}'", idGrupo);
        
        TreeHandler<?> treeHandler = (TreeHandler<?>) Component.getInstance(EstruturaTreeHandler.NAME);
        treeHandler.clearTree();
        treeHandler = (TreeHandler<?>) Component.getInstance(LocalizacaoTreeHandler.NAME);
        treeHandler.clearTree();
        treeHandler = (TreeHandler<?>) Component.getInstance(PapelTreeHandler.class);
        treeHandler.clearTree();
        createAction();
    }

    private ListaEmailManager listaEmailManager() {
        return ComponentUtil.getComponent(ListaEmailManager.NAME);
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof InfoxMailNode)) {
			return false;
		}
		InfoxMailNode other = (InfoxMailNode) obj;
		return getName().equals(other.getName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getName().hashCode();
		return result;
	}
	
}