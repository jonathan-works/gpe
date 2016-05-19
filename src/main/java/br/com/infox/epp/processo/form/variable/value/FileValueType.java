package br.com.infox.epp.processo.form.variable.value;

import br.com.infox.epp.processo.documento.entity.Documento;

public abstract class FileValueType implements ValueType {
    
    protected String name;
    
    public FileValueType(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public TypedValue convertToModelValue(TypedValue propertyValue) {
        Object value = propertyValue.getValue();
        if (value instanceof Documento) {
            return new PrimitiveTypedValueImpl.IntegerValue(((Documento) value).getId());
        }
        throw new IllegalArgumentException("Impossible convert " + propertyValue);
    }
    
    @Override
    public String convertToStringValue(TypedValue propertyValue) {
        TypedValue typedValue = convertToModelValue(propertyValue);
        return typedValue.getValue().toString();
    }
    
    public static class EditorValueType extends FileValueType {

        public EditorValueType() {
            super("editor");
        }
        
    }
    
    public static class UploadValueType extends FileValueType {

        public UploadValueType() {
            super("upload");
        }
        
    }
}
