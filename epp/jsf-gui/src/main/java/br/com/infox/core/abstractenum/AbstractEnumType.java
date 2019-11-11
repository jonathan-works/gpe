package br.com.infox.core.abstractenum;

import java.util.Properties;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.usertype.ParameterizedType;

public class AbstractEnumType extends AbstractSingleColumnStandardBasicType<AbstractEnum> implements ParameterizedType {
    
    private static final long serialVersionUID = 1L;

    public static final String ATTR_NAME = "classType";
    public static final String TYPE = "br.com.infox.core.abstractenum.AbstractEnumType";

    public AbstractEnumType () {
        this(org.hibernate.type.descriptor.sql.VarcharTypeDescriptor.INSTANCE, null);
    }
    
    public AbstractEnumType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<AbstractEnum> javaTypeDescriptor) {
        super(sqlTypeDescriptor, javaTypeDescriptor);
    }

    @Override
    public String getName() {
        return "abstractEnumType";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[] {getName(), AbstractEnum.class.getName()};
    }
    
    public AbstractEnum stringToObject(String string) {
        return fromString(string);
    }
    
    public String objectToSQLString(AbstractEnum value, Dialect dialect) {
        return value.name();
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void setParameterValues(Properties parameters) {
        String className = parameters.getProperty(ATTR_NAME);
        try {
            Class clazz = Class.forName(className);
            setJavaTypeDescriptor(new AbstractEnumDescriptor(clazz));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class '" + className + "' not found.");
        }
    }

}
