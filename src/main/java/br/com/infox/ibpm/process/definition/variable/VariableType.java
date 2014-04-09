package br.com.infox.ibpm.process.definition.variable;

import static br.com.infox.ibpm.process.definition.variable.constants.VariableConstants.DEFAULT_LOCALE;
import static br.com.infox.ibpm.process.definition.variable.constants.VariableConstants.DEFAULT_PATH;
import static java.text.MessageFormat.format;

public enum VariableType {
    NULL(format(DEFAULT_LOCALE, "null"), null),
    STRING(format(DEFAULT_LOCALE,"string"), format(DEFAULT_PATH,"default")),
    TEXT(format(DEFAULT_LOCALE,"text"), format(DEFAULT_PATH,"text")),
    INTEGER(format(DEFAULT_LOCALE,"int"), format(DEFAULT_PATH,"number")),
    BOOLEAN(format(DEFAULT_LOCALE,"bool"), format(DEFAULT_PATH,"sim_nao")),
    DATE(format(DEFAULT_LOCALE,"date"), format(DEFAULT_PATH,"date")),
    FORM(format(DEFAULT_LOCALE,"form"), format(DEFAULT_PATH,"form")),
    PAGE(format(DEFAULT_LOCALE,"page"), format(DEFAULT_PATH,"page")),
    FRAME(format(DEFAULT_LOCALE,"frame"), format(DEFAULT_PATH,"frame")),
    EDITOR(format(DEFAULT_LOCALE,"editor"), format(DEFAULT_PATH,"textEditSignature")),
    TASK_PAGE(format(DEFAULT_LOCALE,"taskPage"), format(DEFAULT_PATH,"taskPage")),
    MONETARY(format(DEFAULT_LOCALE,"monetary"), format(DEFAULT_PATH,"numberMoney")),
    ENUMERATION(format(DEFAULT_LOCALE,"enum"), format(DEFAULT_PATH,"enumeracao"));
    
    private String label;
    private String path;
    
    private VariableType(final String label, final String path) {
        this.label = label;
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public String getPath() {
        return path;
    }
    
    // TODO: quando em produção remover este método e utilizar valueOf
    public static VariableType convertValueOf(String value) {
        if ("sim_nao".equals(value) || BOOLEAN.name().equals(value)) {
            return BOOLEAN;
        } else if ("default".equals(value) || STRING.name().equals(value)) {
            return STRING;
        } else if ("number".equals(value) || INTEGER.name().equals(value)) {
            return INTEGER;
        } else if ("text".equals(value) || TEXT.name().equals(value)) {
            return TEXT;
        } else if ("date".equals(value) || DATE.name().equals(value)) {
            return DATE;
        } else if ("form".equals(value) || FORM.name().equals(value)) {
            return FORM;
        } else if ("page".equals(value) || PAGE.name().equals(value)) {
            return PAGE;
        } else if ("frame".equals(value) || FRAME.name().equals(value)) {
            return FRAME;
        } else if ("textEditSignature".equals(value) || EDITOR.name().equals(value)) {
            return EDITOR;
        } else if ("taskPage".equals(value) || TASK_PAGE.name().equals(value)) {
            return TASK_PAGE;
        } else if ("numberMoney".equals(value) || MONETARY.name().equals(value)) {
            return MONETARY;
        } else if ("enumeracao".equals(value) || ENUMERATION.name().equals(value)) {
            return ENUMERATION;
        }
        return NULL;
    }
    
}
