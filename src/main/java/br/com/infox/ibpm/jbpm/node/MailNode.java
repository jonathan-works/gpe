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
package br.com.infox.ibpm.jbpm.node;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Query;
import javax.transaction.SystemException;

import org.dom4j.Element;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.transaction.Transaction;
import org.jbpm.graph.action.ActionTypes;
import org.jbpm.graph.def.Action;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.xml.JpdlXmlReader;

import br.com.infox.ibpm.entity.ListaEmail;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.home.ListaEmailHome;
import br.com.infox.ibpm.jbpm.MailResolver;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.itx.util.EntityUtil;


public class MailNode extends org.jbpm.graph.node.MailNode {

	private static final long serialVersionUID = 1L;
	private String subject;
	private String to;
	private String actors;
	private String template;
	private ModeloDocumento modeloDocumento;
	private int idGrupo;
	private List<ListaEmail> listaEmail;
	private ListaEmail currentListaEmail = new ListaEmail();

	@Override
	public void read(Element element, JpdlXmlReader jpdlReader) {
		template = element.attributeValue("template");
		actors = element.attributeValue("actors");
		to = element.attributeValue("to");
		subject = jpdlReader.getProperty("subject", element);
		String text = jpdlReader.getProperty("text", element);
		try {
			modeloDocumento = ModeloDocumentoAction.instance().getModeloDocumento(Integer.parseInt(text));	
		}catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}
		
		super.read(element, jpdlReader);
	}

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

	private void createAction() {
		JpdlXmlReader jpdlReader = new JpdlXmlReader(new StringReader(""));
		Delegation delegation = jpdlReader.createMailDelegation(template,
				actors, to, subject, String.valueOf(modeloDocumento.getIdModeloDocumento()));
		delegation.setProcessDefinition(this.getProcessDefinition());
		this.action = new Action(delegation);
	}

	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
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
		if (listaEmail == null) {
			if (to != null && to.startsWith("#{" + MailResolver.NAME)) {
				StringTokenizer st = new StringTokenizer(to, "()");
				st.nextToken();
				String id = st.nextToken();
				idGrupo = Integer.parseInt(id);
				listaEmail = EntityUtil.createQuery("select o from ListaEmail o " +
						"where o.idGrupoEmail = :idGrupo")
					.setParameter("idGrupo", idGrupo)
					.getResultList();
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
		if (idGrupo == 0) {
			String q = "select max(o.idGrupoEmail) from ListaEmail o";
			Query query = EntityUtil.getEntityManager().createQuery(q);
			Object singleResult = EntityUtil.getSingleResult(query);
			if(singleResult != null) {
				idGrupo = (Integer) singleResult;
			}
			idGrupo++;
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
		if (to == null || "".equals(to)) {
			to = MessageFormat.format("#'{'{0}.resolve({1})}", MailResolver.NAME,
					idGrupo);
		}
		Expressions.instance().createMethodExpression("#{estruturaTree.clearTree}").invoke(new Object[0]);
		Expressions.instance().createMethodExpression("#{localizacaoTree.clearTree}").invoke(new Object[0]);
		Expressions.instance().createMethodExpression("#{papelTree.clearTree}").invoke(new Object[0]);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MailNode)) {
			return false;
		}
		MailNode other = (MailNode) obj;
		return getId() == other.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int)getId();
		return result;
	}
	
}