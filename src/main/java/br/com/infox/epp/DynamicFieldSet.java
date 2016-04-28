package br.com.infox.epp;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.outputlabel.OutputLabel;

@FacesComponent(value = "DynamicFieldSet")
@ResourceDependencies({ @ResourceDependency(library = "primefaces", name = "primefaces.css"),
		@ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
		@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
		@ResourceDependency(library = "primefaces", name = "primefaces.js") })
public class DynamicFieldSet extends UIComponentBase {

	private static final String STYLE_CLASS = "dyn-field";
	private static final String GROUP_STYLE_CLASS = "dyn-field dyn-field-grp";
	private static final String LABEL_STYLE_CLASS = "dyn-field dyn-field-lbl";
	private static final String INPUT_STYLE_CLASS = "dyn-field dyn-field-ipt";

	@Override
	public String getFamily() {
		return "fieldset";
	}

	public DynamicFieldSet() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("fieldset", this);
		writer.writeAttribute("class", STYLE_CLASS, "styleClass");
		getChildren().clear();
		Map<String, DynamicField> value = (Map<String, DynamicField>) getAttributes().get("value");
		if (value != null) {
			for (Entry<String, DynamicField> field : value.entrySet()) {
				DynamicField formField = field.getValue();
				if (findComponent(toId(formField.getId())) == null) {
					HtmlPanelGroup parent = new HtmlPanelGroup();
					parent.setStyleClass(GROUP_STYLE_CLASS);
					getChildren().add(parent);
					createInput(formField, parent);
					createLabel(formField, parent);
				}
			}
		}
	}

	private UIOutput createLabel(DynamicField formField, HtmlPanelGroup parent) {
		OutputLabel label = new OutputLabel();
		label.setStyleClass(LABEL_STYLE_CLASS);
		label.setFor(toId(formField.getId()));
		label.setValue(formField.getLabel());
		parent.getChildren().add(label);
		return label;
	}

	private String toId(String string) {
		return DynamicFieldSetUtil.toJsfId(string);
	}

	private UIInput createInput(DynamicField formField, HtmlPanelGroup parent) {
		UIInput input = createInput(formField);
		input.setId(toId(formField.getId()));
		String expression = format("#'{'{0}[''{1}'']'.value}'", formField.getPath(), formField.getId());
		input.setValueExpression("value", DynamicFieldSetUtil.createValueExpression(expression, String.class));
		parent.getChildren().add(input);
		return input;
	}

	private UIInput createInput(DynamicField formField) {
		switch (formField.getType()) {
		case BOOLEAN:
			return createBooleanInput(formField);
		case DATE:
			return createDateInput(formField);
		case SELECT_ONE:
			return createSelectOneInput(formField);
		case STRING:
			return createStringInput(formField);
		default:
			{
				InputText input = new InputText();
				input.setStyleClass(INPUT_STYLE_CLASS);
				input.setTitle(formField.getTooltip());
				input.setDisabled(true);
				return input;
			}
		}
	}

	private UIInput createSelectOneInput(DynamicField formField) {
		HtmlSelectOneMenu menu = new HtmlSelectOneMenu();
		menu.setTitle(formField.getTooltip());
		menu.setStyleClass(INPUT_STYLE_CLASS);
		UISelectItem emptyItem = new UISelectItem();
		emptyItem.setValueExpression("itemLabel", DynamicFieldSetUtil.createValueExpression("#{messages['crud.select.select']}", Object.class));
		emptyItem.setItemValue(null);
		menu.getChildren().add(emptyItem);
		UISelectItems selectItems = new UISelectItems();
		String expression = format("#'{'{0}[''{1}'']'.options.items}'", formField.getPath(), formField.getId());
		selectItems.setValueExpression("value", DynamicFieldSetUtil.createValueExpression(expression, Object.class));
		menu.getChildren().add(selectItems);
		return menu;
	}

	private UIInput createStringInput(DynamicField formField) {
		HtmlInputText input = new HtmlInputText();
		input.setStyleClass(INPUT_STYLE_CLASS);
		input.setTitle(formField.getTooltip());
		return input;
	}

	private UIInput createBooleanInput(DynamicField formField) {
		HtmlSelectBooleanCheckbox input = new HtmlSelectBooleanCheckbox();
		input.setStyleClass(INPUT_STYLE_CLASS);
		input.setTitle(formField.getTooltip());
		return input;
	}

	private UIInput createDateInput(DynamicField formField) {
		Calendar input = new Calendar();
		input.setStyleClass(INPUT_STYLE_CLASS);
		input.setTitle(formField.getTooltip());
		return input;
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		context.getResponseWriter().endElement("fieldset");
		super.encodeEnd(context);
	}
}