package br.com.infox.component.tabs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.el.MethodExpression;
import javax.faces.component.ActionSource2;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;

import org.apache.commons.lang.RandomStringUtils;

import com.sun.faces.application.MethodBindingMethodExpressionAdapter;

@SuppressWarnings("deprecation")
@FacesComponent(Tab.COMPONENT_ID)
public class Tab extends UIPanel implements ActionSource2, ClientBehaviorHolder {
	public static final String COMPONENT_ID = "br.com.infox.component.tabs.Tab";
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";

	private static enum PropertyKeys {
		name, title, actionExpression, immediate, disabled, style, styleClass;
	}
	
	private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(
	        "action"
	));

	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getName() {
		String name = (String) getStateHelper().get(PropertyKeys.name);
		if (name == null) {
			name = RandomStringUtils.randomAlphabetic(5);
			setName(name);
		}
		return name;
	}

	public void setName(String name) {
		getStateHelper().put(PropertyKeys.name, name);
	}

	public String getTitle() {
		String title = (String) getStateHelper().eval(PropertyKeys.title);
		if (title == null) {
			title = getName();
			setTitle(title);
		}
		return title;
	}

	public void setTitle(String title) {
		getStateHelper().put(PropertyKeys.title, title);
	}

	public boolean isDisabled() {
		return (boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	
	public void setDisabled(boolean disabled) {
		getStateHelper().put(PropertyKeys.disabled, disabled);
	}
	
	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style);
	}
	
	public void setStyle(String style) {
		getStateHelper().put(PropertyKeys.style, style);
	}
	
	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass);
	}
	
	public void setStyleClass(String styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, styleClass);
	}
	
	public boolean shouldProcess() {
		return isActiveTab() && !isDisabled();
	}
	
	public TabPanel getTabPanel() {
		UIComponent parent = getParent();
		while (parent != null && !(parent instanceof TabPanel)) {
			parent = parent.getParent();
		}
		return (TabPanel) parent;
	}
	
	public boolean isActiveTab() {
		return getTabPanel().getActiveTab().equals(getName());
	}
	
	//-------------------------- Início ActionSource2 ---------------------------------------//
	@Override
	public MethodBinding getAction() {
		MethodExpression expr = getActionExpression();
		if (expr != null) {
			return new MethodBindingMethodExpressionAdapter(expr);
		}
		return null;
	}

	@Override
	public void setAction(MethodBinding action) {
	}

	@Override
	public MethodBinding getActionListener() {
		return null;
	}

	@Override
	public void setActionListener(MethodBinding actionListener) {
	}

	@Override
	public boolean isImmediate() {
		return (boolean) getStateHelper().eval(PropertyKeys.immediate, false);
	}

	@Override
	public void setImmediate(boolean immediate) {
		getStateHelper().put(PropertyKeys.immediate, immediate);
	}

	@Override
	public void addActionListener(ActionListener listener) {
		addFacesListener(listener);
	}

	@Override
	public ActionListener[] getActionListeners() {
		ActionListener al[] = (ActionListener[]) getFacesListeners(ActionListener.class);
		return (al);
	}

	@Override
	public void removeActionListener(ActionListener listener) {
		removeFacesListener(listener);
	}

	@Override
	public MethodExpression getActionExpression() {
		return (MethodExpression) getStateHelper().get(PropertyKeys.actionExpression);
	}

	@Override
	public void setActionExpression(MethodExpression action) {
		getStateHelper().put(PropertyKeys.actionExpression, action);
	}

	@Override
	public void broadcast(FacesEvent event) throws AbortProcessingException {
		super.broadcast(event);
		if (event instanceof ActionEvent) {
			FacesContext context = FacesContext.getCurrentInstance();
			
			MethodBinding defaultActionListener = getActionListener();
			if (defaultActionListener != null) {
				defaultActionListener.invoke(context, new Object[] {event});
			}
			
			ActionListener listener = context.getApplication().getActionListener();
			if (listener != null) {
				listener.processAction((ActionEvent) event);
			}
		}
	}
	//-------------------------- Fim ActionSource2 ---------------------------------------//
	
	//-------------------------- Início ClientBehaviorHolder ---------------------------------------//
	
	@Override
	public Collection<String> getEventNames() {
		return EVENT_NAMES;
	}
	
	//-------------------------- Fim ClientBehaviorHolder ---------------------------------------//
}
