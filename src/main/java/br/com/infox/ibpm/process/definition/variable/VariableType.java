package br.com.infox.ibpm.process.definition.variable;

import static br.com.infox.ibpm.process.definition.variable.constants.VariableConstants.DEFAULT_LOCALE;
import static br.com.infox.ibpm.process.definition.variable.constants.VariableConstants.DEFAULT_PATH;
import static java.text.MessageFormat.format;

public enum VariableType {
    NULL(format(DEFAULT_LOCALE, "null"), null),
    STRING(format(DEFAULT_LOCALE,"string"), format(DEFAULT_PATH,"default.xhtml")),
    TEXT(format(DEFAULT_LOCALE,"text"), format(DEFAULT_PATH,"text.xhtml")),
    INTEGER(format(DEFAULT_LOCALE,"int"), format(DEFAULT_PATH,"number.xhtml")),
    BOOLEAN(format(DEFAULT_LOCALE,"bool"), format(DEFAULT_PATH,"sim_nao.xhtml")),
    DATE(format(DEFAULT_LOCALE,"date"), format(DEFAULT_PATH,"date.xhtml")),
    FORM(format(DEFAULT_LOCALE,"form"), format(DEFAULT_PATH,"form.xhtml")),
    PAGE(format(DEFAULT_LOCALE,"page"), format(DEFAULT_PATH,"page.xhtml")),
    FRAME(format(DEFAULT_LOCALE,"frame"), format(DEFAULT_PATH,"frame.xhtml")),
    EDITOR(format(DEFAULT_LOCALE,"editor"), format(DEFAULT_PATH,"textEditSignature.xhtml")),
    TASK_PAGE(format(DEFAULT_LOCALE,"taskPage"), format(DEFAULT_PATH,"taskPage.xhtml")),
    MONETARY(format(DEFAULT_LOCALE,"monetary"), format(DEFAULT_PATH,"numberMoney.xhtml")),
    ENUMERATION(format(DEFAULT_LOCALE,"enum"), format(DEFAULT_PATH,"enumeracao.xhtml"));
    
    private String label;
    private String path;
    
    private VariableType(final String label, final String path) {
        this.label = label;
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
}
