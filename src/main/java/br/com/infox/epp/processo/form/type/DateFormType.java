package br.com.infox.epp.processo.form.type;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import br.com.infox.epp.processo.form.variable.value.TypedValue;

public class DateFormType implements FormType {

    public final static String TYPE_NAME = "date";

    protected String datePattern;
    protected DateFormat dateFormat;

    public DateFormType(String datePattern) {
        this.datePattern = datePattern;
        this.dateFormat = new SimpleDateFormat(datePattern);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public TypedValue convertToFormValue(Object object) {
        return null;
    }

    @Override
    public TypedValue convertToModelValue(TypedValue propertyValue) {
        return propertyValue;
    }

}
