package br.com.infox.epp.processo.form.variable.value;

public interface ValueType {
    
    public static ValueType STRING = new PrimitiveValueType.StringValueType();
    public static ValueType INTEGER = new PrimitiveValueType.IntegerValueType();
    public static ValueType DOUBLE = new PrimitiveValueType.DoubleValueType();
    public static ValueType BOOLEAN = new PrimitiveValueType.BooleanValueType();
    public static ValueType DATE = new PrimitiveValueType.DateValueType();
    public static ValueType EDITOR = new FileValueType.EditorValueType();
    public static ValueType UPLOAD = new FileValueType.UploadValueType();
    
    public static ValueType[] TYPES = {STRING, INTEGER, DOUBLE, BOOLEAN, DATE, EDITOR};
    
    String getName();
    
    Object convertToModelValue(TypedValue propertyValue);
    
    String convertToStringValue(TypedValue propertyValue);

}
