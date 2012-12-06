/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.command;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.access.entity.UsuarioLogin;


@Name(EMailData.NAME)
@BypassInterceptors
public class EMailData {
	
	public final static String NAME = "emailData";
	
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