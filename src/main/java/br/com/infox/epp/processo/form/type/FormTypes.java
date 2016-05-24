package br.com.infox.epp.processo.form.type;

import br.com.infox.epp.processo.form.type.PrimitiveFormType.BooleanFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.DateFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.EnumerationFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.FrameFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.IntegerFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.MonetaryFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.PageFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.StringFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.StructuredTextFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.TaskPageFormType;
import br.com.infox.epp.processo.form.type.PrimitiveFormType.TextFormType;

public enum FormTypes {
    
    STRING(StringFormType.class),
    TEXT(TextFormType.class),
    INTEGER(IntegerFormType.class),
    BOOLEAN(BooleanFormType.class),
    DATE(DateFormType.class),
    MONETARY(MonetaryFormType.class),
    STRUCTURED_TEXT(StructuredTextFormType.class),
    ENUMERATION(EnumerationFormType.class),
    FRAME(FrameFormType.class),
    PAGE(PageFormType.class),
    EDITOR(EditorFormType.class),
    FILE(UploadFormType.class),
    TASKPAGE(TaskPageFormType.class);
    
    private Class<? extends FormType> formTypeClass;
    
    private FormTypes(Class<? extends FormType> formTypeClass) {
        this.formTypeClass = formTypeClass;
    }
    
    public Class<? extends FormType> getFormTypeClass() {
        return formTypeClass;
    }
    
    public boolean isFileType() {
        return formTypeClass.isAssignableFrom(FileFormType.class);
    }
    
    public boolean isPrimitiveType() {
        return formTypeClass.isAssignableFrom(PrimitiveFormType.class);
    }
    
    public FormType create() {
        try {
            return getFormTypeClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Impossible construct instance of class " + getFormTypeClass().getName());
        }
    }

}
