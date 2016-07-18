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
    PAGE(format(DEFAULT_LOCALE,"page"), format(DEFAULT_PATH,"page")),
    FRAME(format(DEFAULT_LOCALE,"frame"), format(DEFAULT_PATH,"frame")),
    EDITOR(format(DEFAULT_LOCALE,"editor"), format(DEFAULT_PATH,"textEditSignature")),
    STRUCTURED_TEXT(format(DEFAULT_LOCALE,"structuredText"), format(DEFAULT_PATH,"structuredText")),
    TASK_PAGE(format(DEFAULT_LOCALE,"taskPage"), format(DEFAULT_PATH,"taskPage")),
    MONETARY(format(DEFAULT_LOCALE,"monetary"), format(DEFAULT_PATH,"numberMoney")),
    FILE(format(DEFAULT_LOCALE, "file"), format(DEFAULT_PATH, "fileUpload")),
    ENUMERATION(format(DEFAULT_LOCALE,"enum"), format(DEFAULT_PATH,"enumeracao")),
    ENUMERATION_MULTIPLE(format(DEFAULT_LOCALE,"enum_multiple"), format(DEFAULT_PATH,"enumeracao_multipla")),
    FRAGMENT(format(DEFAULT_LOCALE, "fragment"), format(DEFAULT_PATH, "fragment")),
    PARAMETER(format(DEFAULT_LOCALE, "parameter"), null);

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
}
