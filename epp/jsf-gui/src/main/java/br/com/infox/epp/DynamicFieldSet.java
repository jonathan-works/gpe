package br.com.infox.epp;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlSelectManyCheckbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.outputlabel.OutputLabel;

import br.com.infox.core.type.Displayable;

@FacesComponent(value = "DynamicFieldSet")
@ResourceDependencies({ @ResourceDependency(library = "primefaces", name = "primefaces.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "primefaces.js") })
public class DynamicFieldSet extends UIComponentBase {

    private static final String STYLE_CLASS = "dyn-field";
    private static final String GROUP_STYLE_CLASS = "dyn-field-grp";
    private static final String LABEL_STYLE_CLASS = "dyn-field-lbl";
    private static final String INPUT_STYLE_CLASS = "dyn-field-ipt";

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
                    parent.setStyleClass(resolveGroupStyleClass());
                    getChildren().add(parent);
                    createInput(formField, parent);
                    createLabel(formField, parent);
                    createActions(formField, parent);
                }
            }
        }
    }

    private String resolveGroupStyleClass() {
        return STYLE_CLASS + " " + GROUP_STYLE_CLASS;
    }

    private void createActions(DynamicField formField, HtmlPanelGroup parent) {
        HtmlPanelGroup actionsGroup = new HtmlPanelGroup();

        for (DynamicFieldAction action : formField.getActions()) {
            actionsGroup.getChildren().add(createAction(action, parent));
        }
        parent.getChildren().add(actionsGroup);
    }

    private UICommand createAction(DynamicFieldAction action, HtmlPanelGroup parent) {
        HtmlCommandLink command = new HtmlCommandLink();
        if (action.getIcon() == null)
            command.setValue(action.getLabel());
        command.setActionExpression(DynamicFieldSetUtil.createMethodExpression(action.getAction(), Void.class));
        parent.getChildren().add(command);
        return command;
    }

    private UIOutput createLabel(DynamicField formField, HtmlPanelGroup parent) {
        OutputLabel label = new OutputLabel();
        label.setStyleClass(resolveLabelStyleClass());
        label.setFor(toId(formField.getId()));
        label.setValue(formField.getLabel());
        parent.getChildren().add(label);
        return label;
    }

    private String resolveLabelStyleClass() {
        return STYLE_CLASS + " " + LABEL_STYLE_CLASS;
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
        case TEXT:
            return createTextInput(formField);
        case STRING:
            return createStringInput(formField);
        case CHECKBOX_ENUM:
            return createCheckboxEnum(formField);
        default: {
            HtmlInputText input = new HtmlInputText();
            input.setStyleClass(resolveInputStyleClass());
            input.setTitle(formField.getTooltip());
            input.setDisabled(true);
            return input;
        }
        }
    }

    private String resolveInputStyleClass() {
        return STYLE_CLASS + " " + INPUT_STYLE_CLASS;
    }

    private UIInput createCheckboxEnum(DynamicField formField) {
        HtmlSelectManyCheckbox menu = new HtmlSelectManyCheckbox();
        menu.setTitle(formField.getTooltip());
        menu.setStyleClass(resolveInputStyleClass());
        for (Enum<? extends Displayable> enumConstant : formField.getEnumValues()) {
            UISelectItem item = new UISelectItem();
            item.setItemLabel(((Displayable) enumConstant).getLabel());
            item.setItemValue(enumConstant);
            menu.getChildren().add(item);
        }
        menu.setValue(formField.getValue());
        return menu;
    }

    private UIInput createSelectOneInput(DynamicField formField) {
        HtmlSelectOneMenu menu = new HtmlSelectOneMenu();
        menu.setTitle(formField.getTooltip());
        menu.setStyleClass(resolveInputStyleClass());
        UISelectItem emptyItem = new UISelectItem();
        emptyItem.setValueExpression("itemLabel",
                DynamicFieldSetUtil.createValueExpression("#{messages['crud.select.select']}", Object.class));
        emptyItem.setItemValue(null);
        emptyItem.setNoSelectionOption(true);
        menu.getChildren().add(emptyItem);
        UISelectItems selectItems = new UISelectItems();
        String expression = format("#'{'{0}[''{1}'']'.options.items}'", formField.getPath(), formField.getId());
        selectItems.setValueExpression("value", DynamicFieldSetUtil.createValueExpression(expression, Object.class));
        menu.getChildren().add(selectItems);
        return menu;
    }

    private UIInput createTextInput(DynamicField formField) {
        HtmlInputTextarea input = new HtmlInputTextarea();
        input.setStyleClass(resolveInputStyleClass());
        input.setTitle(formField.getTooltip());
        input.setReadonly(Boolean.TRUE.equals(formField.get("readonly")));
        return input;
    }
    
    private UIInput createStringInput(DynamicField formField) {
        HtmlInputText input = new HtmlInputText();
        input.setStyleClass(resolveInputStyleClass());
        input.setTitle(formField.getTooltip());
        input.setReadonly(Boolean.TRUE.equals(formField.get("readonly")));
        return input;
    }

    private UIInput createBooleanInput(DynamicField formField) {
        HtmlSelectBooleanCheckbox input = new HtmlSelectBooleanCheckbox();
        input.setStyleClass(resolveInputStyleClass());
        input.setTitle(formField.getTooltip());
        return input;
    }

    private UIInput createDateInput(DynamicField formField) {
        Calendar input = new Calendar();
        input.setStyleClass(resolveInputStyleClass());
        input.setTitle(formField.getTooltip());
        return input;
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("fieldset");
        encodeScripts(writer);
        super.encodeEnd(context);
    }

    private void encodeScripts(ResponseWriter writer) throws IOException {
        writer.startElement("script", null);
        encodeSortFieldsScript(writer);
        writer.endElement("script");
    }

    private void encodeSortFieldsScript(ResponseWriter writer) throws IOException {
        final String labelCssSelector = String.format(".%s",LABEL_STYLE_CLASS);
        final String fieldsetCssSelector = String.format("fieldset.%s", STYLE_CLASS);
        final String groupsCssSelector = fieldsetCssSelector + " " + String.format(".%s", GROUP_STYLE_CLASS);
        
        writer.append("(function(){");
        writer.append("\"use strict\";");
        writer.append("var addToParentInOrder = function(fieldsArray, parentNode){");
        writer.append("var item;");
        writer.append("for(var i=0,l=fieldsArray.length; i<l; i++){");
        writer.append("item = fieldsArray[i];");
        writer.append("parentNode.removeChild(item);");
        writer.append("parentNode.appendChild(item);");
        writer.append("}");
        writer.append("};");
        writer.append("var sortFunction = function(a,b){");
        writer.append("if(a.sortableValue < b.sortableValue) return -1;");
        writer.append("if(a.sortableValue > b.sortableValue) return 1;");
        writer.append("return 0;");
        writer.append("};");
        writer.append("var sortFields = function(fields){");
        writer.append("return fields.sort(sortFunction);");
        writer.append("};");
        writer.append("var mapField = function(field){");
        writer.append("return {");
        writer.append("sortableValue : field.querySelector(\""+ labelCssSelector+ "\").textContent.trim().toLowerCase(),");
        writer.append("field : field");
        writer.append("};");
        writer.append("};");
        writer.append("var mapFields = function(fields){");
        writer.append("var map = [];");
        writer.append("for(var i=0,l=fields.length; i<l; i++){");
        writer.append("map.push( mapField( fields.item(i) ) );");
        writer.append("}");
        writer.append("return map;");
        writer.append("};");
        writer.append("var unmapFields = function( fields ){");
        writer.append("var map = [];");
        writer.append("for(var i=0,l=fields.length; i<l; i++){");
        writer.append("map.push( fields[i].field );");
        writer.append("}");
        writer.append("return map;");
        writer.append("};");
        writer.append("var fields = sortFields( mapFields( document.querySelectorAll(\""
                + groupsCssSelector
                + "\") ) );");
        writer.append("addToParentInOrder(unmapFields(fields), document.querySelector(\""
                + fieldsetCssSelector + "\"));");
        writer.append("})();");
    }
}