package br.com.infox.epp.processo.form.type;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValue;
import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValue.EnumerationMultipleValue;
import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValue.EnumerationValue;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.dao.ListaDadosSqlDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

public abstract class EnumFormType extends PrimitiveFormType {
    
    public EnumFormType(String name, ValueType valueType) {
        super(name, valueType);
    }
    
    @Override
    public void validate(FormField formField, FormData formData) throws BusinessException {
        // do nothing
    }

    @Override
    public boolean isPersistable() {
        return true;
    }
    
    public abstract void setSelectItems(List<SelectItem> selectItens, TypedValue typedValue);

    @Override
    public void performValue(FormField formField, FormData formData) {
        Integer idDominio = Integer.valueOf(formField.getProperties().get("extendedProperties"));
        DominioVariavelTarefaManager dominioVariavelTarefaManager = getDominioVariavelTarefaManager();
        List<SelectItem> selectItens = new ArrayList<>();
        DominioVariavelTarefa dominio = dominioVariavelTarefaManager.find(idDominio);
        if (dominio.isDominioSqlQuery()) {
            ListaDadosSqlDAO listaDadosSqlDAO = ComponentUtil.getComponent(ListaDadosSqlDAO.NAME);
            selectItens.addAll(listaDadosSqlDAO.getListSelectItem(dominio.getDominio()));
        } else {
            String[] itens = dominio.getDominio().split(";");
            for (String item : itens) {
                String[] pair = item.split("=");
                selectItens.add(new SelectItem(pair[1], pair[0]));
            }
        }
        setSelectItems(selectItens, formField.getTypedValue());
    }
        
    protected DominioVariavelTarefaManager getDominioVariavelTarefaManager() {
        return  (DominioVariavelTarefaManager) Component.getInstance(DominioVariavelTarefaManager.NAME);
    }
    
    public static class EnumerationFormType extends EnumFormType {

        public EnumerationFormType() {
            super("enumeration", ValueType.STRING);
        }

        @Override
        public TypedValue convertToFormValue(Object value) {
            if (value == null) {
                return new PrimitiveTypedValue.EnumerationValue(null);
            } else if (value instanceof String) {
                return new PrimitiveTypedValue.EnumerationValue((String) value);
            }
            throw new IllegalArgumentException("Cannot convert '" + value + "' to String");
        }

        @Override
        public void setSelectItems(List<SelectItem> selectItens, TypedValue typedValue) {
            EnumerationValue enumValue = (EnumerationValue) typedValue;
            enumValue.setSelectItems(selectItens);
        }
    }
    
    public static class EnumerationMultipleFormType extends EnumFormType {
        
        public EnumerationMultipleFormType() {
            super("enumerationMultiple", ValueType.STRING_ARRAY);
        }
        
        @Override
        public TypedValue convertToFormValue(Object value) {
            if (value == null) {
                return new PrimitiveTypedValue.EnumerationMultipleValue(null);
            } 
            Gson GSON = new GsonBuilder().create();
            if (value instanceof String) {
                String[] array = GSON.fromJson((String) value, String[].class);
                return new PrimitiveTypedValue.EnumerationMultipleValue(array);
            }
            if (value instanceof String[]) {
                return new PrimitiveTypedValue.EnumerationMultipleValue((String[]) value);
            }
            throw new IllegalArgumentException("Cannot convert '" + value + "' to String[]");
        }

        @Override
        public void setSelectItems(List<SelectItem> selectItens, TypedValue typedValue) {
            EnumerationMultipleValue enumValue = (EnumerationMultipleValue) typedValue;
            enumValue.setSelectItems(selectItens);
        }
    }

}
