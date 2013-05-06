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
package br.com.infox.filter;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlForm;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Classe para medi��o de tempo das fases do ciclos de vida JSF
 * 
 * Para habilitar remova o coment�rio dos observer
 * @author luiz
 *
 */
@Name("meeterPhaseListener")
@BypassInterceptors
public class MeeterPhaseListener {
	
	private long time;
	
	@Observer("org.jboss.seam.beforePhase")
	public void beforePhase(PhaseEvent event) {
		time = new Date().getTime();
		System.out.println("Entrou: " + event.getPhaseId());
	}

	@Observer("org.jboss.seam.afterPhase")
	public void afterPhase(PhaseEvent event) {
		System.out.println("Saiu: " + event.getPhaseId() + " - " + (new Date().getTime() - time));
		time = 0; 
//		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
//			UIViewRoot root = event.getFacesContext().getViewRoot();
//			showComponentsWithoutForm(root);
//		}
	}

//	private void showComponentsWithoutForm(UIComponent root) {
//		if (hasParentForm(root)) {
//			System.out.println(root.getClass() + " -> " + root.getId());
//		}
//		for (UIComponent child : root.getChildren()) {
//			showComponentsWithoutForm(child);
//		}
//	}
//	
//	private boolean hasParentForm(UIComponent component) {
//		UIComponent parent = component.getParent();
//		while (parent != null) {
//			if (parent instanceof UIForm || parent instanceof HtmlForm) {
//				return true;
//			}
//			parent = parent.getParent();
//		}
//		return false;
//	}
}