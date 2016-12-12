package br.com.infox.epp.processo.form.variable.value;

import br.com.infox.epp.processo.form.variable.value.ObjectValueType.JPAValueType;
import br.com.infox.epp.processo.form.variable.value.ObjectValueType.JSONValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.BooleanValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.DateValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.DoubleValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.IntegerValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.LongValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.NullValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.ParameterValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.StringArrayValueType;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType.StringValueType;

public interface ValueType {
    
    public static PrimitiveValueType NULL = new NullValueType();
    public static PrimitiveValueType STRING = new StringValueType();
    public static PrimitiveValueType PARAMETER = new ParameterValueType();
    public static PrimitiveValueType INTEGER = new IntegerValueType();
    public static PrimitiveValueType LONG = new LongValueType();
    public static PrimitiveValueType DOUBLE = new DoubleValueType();
    public static PrimitiveValueType BOOLEAN = new BooleanValueType();
    public static PrimitiveValueType DATE = new DateValueType();
    public static PrimitiveValueType STRING_ARRAY = new StringArrayValueType();
    
    public static FileValueType FILE = new FileValueType();
    
    public static ObjectValueType JPA = new JPAValueType();
    public static ObjectValueType JSON = new JSONValueType();
    
    public static ValueType[] TYPES = {NULL, STRING, PARAMETER, INTEGER, LONG, DOUBLE, BOOLEAN, DATE, FILE, STRING_ARRAY, JPA, JSON};
    
    String getName();
    
    Object convertToModelValue(Object propertyValue);
    
    String convertToStringValue(Object propertyValue);

}
