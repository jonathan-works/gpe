package br.com.infox.epp.processo.form.type;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.dao.ListaDadosSqlDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

public abstract class EnumFormType extends PrimitiveFormType {
    
    public EnumFormType(String name, String path, ValueType valueType) {
        super(name, path, valueType);
    }
    
    @Override
    public void validate(FormField formField, FormData formData) throws BusinessException {
        // do nothing
    }

    @Override
    public boolean isPersistable() {
        return true;
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
        Integer idDominio = Integer.valueOf((String)formField.getProperties().get("extendedProperties"));
        DominioVariavelTarefaManager dominioVariavelTarefaManager = getDominioVariavelTarefaManager();
        List<SelectItem> selectItems = new ArrayList<>();
        DominioVariavelTarefa dominio = dominioVariavelTarefaManager.find(idDominio);
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
        
    protected DominioVariavelTarefaManager getDominioVariavelTarefaManager() {
        return  (DominioVariavelTarefaManager) Component.getInstance(DominioVariavelTarefaManager.NAME);
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
    }
}
