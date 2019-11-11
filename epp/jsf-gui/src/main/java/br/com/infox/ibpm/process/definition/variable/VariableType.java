package br.com.infox.ibpm.process.definition.variable;

public enum VariableType {
    
    NULL("process.def.var.null", null),
    STRING("process.def.var.string", "/WEB-INF/xhtml/components/form/default.xhtml"),
    TEXT("process.def.var.text", "/WEB-INF/xhtml/components/form/text.xhtml"),
    INTEGER("process.def.var.int", "/WEB-INF/xhtml/components/form/number.xhtml"),
    BOOLEAN("process.def.var.bool", "/WEB-INF/xhtml/components/form/sim_nao.xhtml"),
    DATE("process.def.var.date", "/WEB-INF/xhtml/components/form/date.xhtml"),
    PAGE("process.def.var.page", "/WEB-INF/xhtml/components/form/page.xhtml"),
    FRAME("process.def.var.frame", "/WEB-INF/xhtml/components/form/frame.xhtml"),
    EDITOR("process.def.var.editor", "/WEB-INF/xhtml/components/form/textEditSignature.xhtml"),
    STRUCTURED_TEXT("process.def.var.structuredText", "/WEB-INF/xhtml/components/form/structuredText.xhtml"),
    TASK_PAGE("process.def.var.taskPage", "/WEB-INF/xhtml/components/form/taskPage.xhtml"),
    MONETARY("process.def.var.monetary", "/WEB-INF/xhtml/components/form/numberMoney.xhtml"),
    FILE("process.def.var.file", "/WEB-INF/xhtml/components/form/fileUpload.xhtml"),
    ENUMERATION("process.def.var.enum", "/WEB-INF/xhtml/components/form/enumeracao.xhtml"),
    ENUMERATION_MULTIPLE("process.def.var.enum_multiple", "/WEB-INF/xhtml/components/form/enumeracao_multipla.xhtml"),
    FRAGMENT("process.def.var.fragment", "/WEB-INF/xhtml/components/form/fragment.xhtml"),
    PARAMETER("process.def.var.parameter", null);

    private String label;
    private String path;
    
    private VariableType(String label, String path) {
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
