package br.com.infox.epp.processo.form.type;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.VariableDominioEnumerationHandler;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaSearch;
import br.com.infox.ibpm.variable.dao.ListaDadosSqlDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.seam.util.ComponentUtil;

public abstract class EnumFormType extends PrimitiveFormType {
    
    public EnumFormType(String name, String path, ValueType valueType) {
        super(name, path, valueType);
    }
    
    @Override
    public boolean isPersistable() {
        return true;
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
    	String codigo = VariableDominioEnumerationHandler.fromJson((String) formField.getProperties().get("configuration")).getCodigoDominio();
        DominioVariavelTarefa dominio = getDominioVariavelTarefaSearch().findByCodigo(codigo);
        List<SelectItem> selectItems = new ArrayList<>();
        if (dominio.isDominioSqlQuery()) {
            ListaDadosSqlDAO listaDadosSqlDAO = ComponentUtil.getComponent(ListaDadosSqlDAO.NAME);
            selectItems.addAll(listaDadosSqlDAO.getListSelectItem(dominio.getDominio()));
        } else {
            String[] itens = dominio.getDominio().split(";");
            for (String item : itens) {
                String[] pair = item.split("=");
                selectItems.add(new SelectItem(pair[1], pair[0]));
            }
        }
        formField.addProperty("selectItems", selectItems);
    }
        
    protected DominioVariavelTarefaSearch getDominioVariavelTarefaSearch() {
    	return BeanManager.INSTANCE.getReference(DominioVariavelTarefaSearch.class);
    }
    
    public static class EnumerationFormType extends EnumFormType {

        public EnumerationFormType() {
            super("enumeration", "/Processo/form/enumeration.xhtml", ValueType.STRING);
        }

        @Override
        public Object convertToFormValue(Object value) {
            return value;
        }
    }
    
    public static class EnumerationMultipleFormType extends EnumFormType {
        
        public EnumerationMultipleFormType() {
            super("enumerationMultiple", "/Processo/form/enumerationMultiple.xhtml", ValueType.STRING_ARRAY);
        }
        
        @Override
        public Object convertToFormValue(Object value) {
            if (value == null) {
                return null;
            } 
            Gson GSON = new GsonBuilder().create();
            if (value instanceof String) {
                String[] array = GSON.fromJson((String) value, String[].class);
                return array;
            }
            if (value instanceof String[]) {
                return value;
            }
            throw new IllegalArgumentException("Cannot convert '" + value + "' to String[]");
        }
        
        @Override
        public boolean isInvalid(FormField formField, FormData formData) {
            String required = formField.getProperty("required", String.class);
            if ("true".equalsIgnoreCase(required) && 
                    (formField.getValue() == null  || ((String[])formField.getValue()).length == 0 )) {
                FacesContext.getCurrentInstance().addMessage(formField.getComponent().getClientId(), new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "", InfoxMessages.getInstance().get("beanValidation.notNull")));
                return true;
            }
            return false;
        }
    }
}
