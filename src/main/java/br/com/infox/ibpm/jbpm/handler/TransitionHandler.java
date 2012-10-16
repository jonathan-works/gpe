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
package br.com.infox.ibpm.jbpm.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.graph.def.Transition;

import br.com.infox.ibpm.jbpm.converter.NodeConverter;

public class TransitionHandler implements Serializable {

	private static final long serialVersionUID = 4373236937521654740L;

	private Transition transition;
	
	/**
	 * Usado para definir se a transi��o ser� vis�vel na sa�da do n� para o 
	 * usu�rio. 
	 */
	private boolean showTransitionButton;

	public TransitionHandler(Transition transition) {
		this.transition = transition;
		this.showTransitionButton = "#{true}".equals(transition.getCondition());
	}

	public String getName() {
		return transition.getName();
	}

	public void setName(String name) {
		if (name != null && !name.equals(transition.getName())) {
			transition.setName(name);
		}
	}
	
	public Transition getTransition() {
		return transition;
	}
	
	public void setFrom(String from) {
		transition.setFrom(NodeConverter.getAsObject(from));
	}

	public String getFrom() {
		return transition.getFrom() == null ? null : transition.getFrom().toString();
	}

	public String getFromName() {
		return transition.getFrom() == null ? null : transition.getFrom().getName();
	}
	
	public void setTo(String to) {
		transition.setTo(NodeConverter.getAsObject(to));
	}

	public String getTo() {
		return transition.getTo() == null ? null : transition.getTo().toString();
	}

	public String getToName() {
		return transition.getTo() == null ? null : transition.getTo().getName();
	}

	public static List<TransitionHandler> getList(Collection<Transition> transitions) {
		List<TransitionHandler> list = new ArrayList<TransitionHandler>();
		for (Transition t : transitions) {
			list.add(new TransitionHandler(t));
		}
		return list;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append(" [");
		if (getFrom() != null) {
			sb.append(getFromName());
		}
		sb.append(" -> ");
		if (getTo() != null) {
			sb.append(getToName());
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static String asString(TransitionHandler th) {
		if (th == null) {
			return null;
		}
		return th.getFromName() + " -> " + th.getToName();	
	}
	
	public static TransitionHandler asObject(String name, List<TransitionHandler> transitions ) {
		if (name == null) {
			return null;
		} 
		for (TransitionHandler t : transitions) {
			if (t.toString().equals(name)) {
				return t;
			}
		}
		return null;
	}

	public void setShowTransitionButton(boolean showTransitionButton) {
		this.showTransitionButton = showTransitionButton;
	}

	public boolean getShowTransitionButton() {
		return showTransitionButton;
	}
	
}