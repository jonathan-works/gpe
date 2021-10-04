package br.com.infox.ibpm.converter;

import java.math.BigDecimal;

import org.jbpm.context.exe.Converter;

public class BigDecimalToStringConverter implements Converter {

    private static final long serialVersionUID = 1L;

    public boolean supports(Object value) {
        return value instanceof BigDecimal || value == null;
    }

    public Object convert(Object o) {
        return o == null ? null : ((BigDecimal) o).toString();
    }

    public Object revert(Object o) {
        return o == null ? null : new BigDecimal(o.toString());
    }

}
