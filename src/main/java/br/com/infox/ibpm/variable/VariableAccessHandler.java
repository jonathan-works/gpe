package br.com.infox.ibpm.variable;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.context.def.Access;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.fluxo.crud.VariavelClassificacaoDocumentoAction;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.TaskHandlerVisitor;

public class VariableAccessHandler implements Serializable {

    public static final String ACCESS_VARIAVEL_INICIA_VAZIA = "reset";
	public static final String EVENT_JBPM_VARIABLE_NAME_CHANGED = "jbpmVariableNameChanged";
    private static final String COMMA = ",";
    private static final long serialVersionUID = -4113688503786103974L;
    private VariableAccess variableAccess;
    private String name;
    private VariableType type;
    private String value;
    private boolean[] access;
    private Task task;
    private boolean possuiDominio = false;
    private boolean isData = false;
    private boolean isFile;
    private boolean fragment;
    private boolean isNumericOrMonetary;
    private FragmentConfiguration fragmentConfiguration;
    
    private VariableEditorModeloHandler modeloEditorHandler = new VariableEditorModeloHandler();
    private VariableDataHandler dataHandler = new VariableDataHandler();
    private VariableDominioEnumerationHandler dominioHandler = new VariableDominioEnumerationHandler();
    private VariableMaxMinHandler maxMinHandler = new VariableMaxMinHandler();
    private VariableStringHandler stringHandler = new VariableStringHandler();
    
    public VariableAccessHandler(VariableAccess variableAccess, Task task) {
        this.task = task;
        this.variableAccess = variableAccess;
        String mappedName = variableAccess.getMappedName();
        if (mappedName.indexOf(':') > 0) {
            String[] tokens = mappedName.split(":");
            this.type = VariableType.valueOf(tokens[0]);
            this.name = variableAccess.getVariableName();
            this.value = variableAccess.getValue();
            switch (type) {
                case DATE:
                    getDataHandler().init(this.variableAccess);
                    break;
                case MONETARY:
                case INTEGER:	
                	getMaxMinHandler().init(this.variableAccess);
                	break;
                case STRING:
                	getStringHandler().init(this.variableAccess);
                case ENUMERATION:
                case ENUMERATION_MULTIPLE:
                	getDominioHandler().init(getVariableAccess());
                    break;
                case FRAGMENT:
                    if (tokens.length >= 3) {
                        setFragmentConfiguration(BeanManager.INSTANCE.getReference(FragmentConfigurationCollector.class).getByCode(tokens[2]));
                    }
                    break;
                case FILE:
                case EDITOR:
                	getModeloEditorHandler().init(this.variableAccess);
                    break;
                default:
                    break;
                }
        } else {
            this.type = VariableType.STRING;
            this.name = variableAccess.getVariableName();
        }
        access = new boolean[5];
        access[0] = variableAccess.isReadable();
        access[1] = variableAccess.isWritable();
        access[2] = variableAccess.isRequired();
        access[3] = !variableAccess.isReadable() && variableAccess.isWritable();
        access[4] = variableAccess.getAccess().hasAccess(ACCESS_VARIAVEL_INICIA_VAZIA);
        this.possuiDominio = tipoPossuiDominio(this.type);
        this.isData = isTipoData(this.type);
        this.isFile = isTipoFile(this.type);
        this.isNumericOrMonetary = isNumericoOuMonetario(this.type);
    }

    private boolean tipoPossuiDominio(VariableType type) {
        return VariableType.ENUMERATION.equals(type) || VariableType.ENUMERATION_MULTIPLE == type;
    }

    private boolean isTipoData(VariableType type) {
        return VariableType.DATE.equals(type);
    }
    
    private boolean isTipoFile(VariableType type) {
        return VariableType.FILE.equals(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String auxiliarName = name.replace(' ', '_').replace('/', '_');
        if (!auxiliarName.equals(this.name)) {
            if (VariableType.PAGE.equals(type) && !pageExists(auxiliarName)) {
                return;
            }
            Events.instance().raiseEvent(EVENT_JBPM_VARIABLE_NAME_CHANGED, ProcessBuilder.instance().getFluxo().getIdFluxo(), this.name, auxiliarName);
            this.name = auxiliarName;
            variableAccess.setVariableName(auxiliarName);
            
            if (isFragment()){
                setFragmentConfiguration(fragmentConfiguration);
            } else {
                setMappedName(auxiliarName, type);
            }
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        variableAccess.setValue(value);
    }

    public VariableAccess update() {
        variableAccess = new VariableAccess(name, getAccess(), format("{0}:{1}", type.name(), name));
    	limparConfiguracoes();
        return variableAccess;
    }
    
    @SuppressWarnings("unchecked")
    public void removeTaskAction(String actionName) {
        GraphElement parent = task.getParent();
        if (parent.getEvents() == null)
            return;
        for (Object entry : parent.getEvents().entrySet()) {
            Event event = ((Map.Entry<String, Event>) entry).getValue();
            if (event.hasActions()) {
                for (Object o : event.getActions()) {
                    Action a = (Action) o;
                    String name = a.getName();
                    String exp = a.getActionExpression();
                    if ((name != null && name.equalsIgnoreCase(actionName))
                            || (exp != null && exp.contains("'" + actionName + "'"))) {
                        event.removeAction(a);
                        if (a.getProcessDefinition() != null) {
                            a.getProcessDefinition().removeAction(a);
                        }
                        if (event.getActions().isEmpty()) {
                            event.getGraphElement().removeEvent(event);
                        }
                        return;
                    }
                }
            }
        }
    }

    public VariableAccess getVariableAccess() {
        return variableAccess;
    }

    public void setVariableAccess(VariableAccess variableAccess) {
        this.variableAccess = variableAccess;
    }

    public VariableType getType() {
        return type;
    }

    private boolean pageExists(String name) {
        String page = format("/{0}.xhtml", name.replaceAll("_", "/"));
        String realPath = ServletLifecycle.getServletContext().getRealPath(page);
        final boolean fileExists = new File(realPath).exists();
        if (!fileExists) {
            FacesMessages.instance().add(Severity.INFO, format("A página ''{0}'' não foi encontrada.", page));
        }
        return fileExists;
    }

    public void setType(VariableType type) {
        this.type = type;
        this.variableAccess.setType(type.name());
        switch (type) {
            case PAGE:
                if (!pageExists(name)) {
                    setWritable(true);
                    this.name = "";
                    return;
                }
                break;
            case FILE:
                setWritable(true);
            default:
                break;
        }
        setMappedName(name, type);
        this.possuiDominio = tipoPossuiDominio(type);
        this.isData = isTipoData(type);
        this.isFile = isTipoFile(type);
        this.fragment = isTipoFragment(type);
        this.isNumericOrMonetary = isNumericoOuMonetario(type);
    }

    private boolean isNumericoOuMonetario(VariableType type) {
        return VariableType.INTEGER.equals(type) || VariableType.MONETARY.equals(type);
    }
     
    
    private boolean isTipoFragment(VariableType type) {
        return VariableType.FRAGMENT.equals(type);
    }

    public boolean isReadable() {
        return variableAccess.isReadable();
    }

    public void setReadable(boolean readable) {
        if (readable != variableAccess.isReadable()) {
            access[0] = readable;
            access[3] = !access[0];
            variableAccess.setAccess(new Access(getAccess()));
        }
    }

    public boolean isWritable() {
        return variableAccess.isWritable();
    }

    public void setWritable(boolean writable) {
        if (writable != variableAccess.isWritable()) {
            access[1] = writable;
            if (access[1]) {
                access[0] = !access[3];
            }
            variableAccess.setAccess(new Access(getAccess()));
            if (!writable) {
            	setIniciaVazia(false);
            }
        }
    }

    public boolean isHidden() {
        return !variableAccess.isReadable() && variableAccess.isWritable();
    }

    public void setHidden(boolean hidden) {
        if (hidden != (!variableAccess.isReadable() && variableAccess.isWritable())) {
            access[3] = hidden;
            if (access[3]) {
                access[0] = false;
                access[1] = true;
                access[2] = false;
            }
            variableAccess.setAccess(new Access(getAccess()));
        }
    }

    public boolean isRequired() {
        return variableAccess.getAccess().isRequired();
    }

    public void setRequired(boolean required) {
        if (required != variableAccess.isRequired()) {
            access[2] = required;
            if (access[2]) {
                access[0] = true;
                access[1] = true;
                access[3] = false;
            }
            variableAccess.setAccess(new Access(getAccess()));
        }
    }

    private String getAccess() {
        StringBuilder sb = new StringBuilder();
        if (access[2]) {
            access[3] = false;
            access[1] = true;
            access[0] = true;
        } else if (access[3]) {
            access[2] = false;
            access[1] = true;
            access[0] = false;
        } else if (access[1]) {
            access[0] = true;
        }

        if (access[0]) {
            appendPermission(sb, "read");
        }
        if (access[1]) {
            appendPermission(sb, "write");
        }
        if (access[2]) {
            appendPermission(sb, "required");
        }
        if (access[4]) {
        	appendPermission(sb, ACCESS_VARIAVEL_INICIA_VAZIA);
        }
        return sb.toString();
    }

    private void appendPermission(StringBuilder sb, String permission) {
        if (sb.length() > 0) {
            sb.append(COMMA);
        }
        sb.append(permission);
    }

    public void copyVariable() {
        TaskHandlerVisitor visitor = new TaskHandlerVisitor(true);
        visitor.visit(task);
        for (String v : visitor.getVariables()) {
            String[] tokens = v.split(":");
            if (tokens.length > 1 && tokens[1].equals(name)) {
                setType(VariableType.valueOf(tokens[0]));
                setWritable(false);
                switch (type) {
                case FRAGMENT:
                    if (tokens.length >= 3) {
                        setFragmentConfiguration(BeanManager.INSTANCE.getReference(FragmentConfigurationCollector.class).getByCode(tokens[2]));
                    }
                    break;
                case DATE:
                    getDataHandler().init(getVariableAccess());
                    break;
                case MONETARY:
                case INTEGER:	
                	getMaxMinHandler().init(getVariableAccess());
                	break;
                case STRING:
                	getStringHandler().init(getVariableAccess());
                	break;
                case ENUMERATION:
                case ENUMERATION_MULTIPLE:
                    getDominioHandler().init(getVariableAccess());
                    break;
                    case FILE:
                    case EDITOR:
                    	getModeloEditorHandler().init(getVariableAccess());
                    	break;
                default:
                	break;
                }
            }
        }
    }

    public static List<VariableAccessHandler> getList(Task task) {
        List<VariableAccessHandler> ret = new ArrayList<>();
        if (task.getTaskController() == null) {
            return ret;
        }
        List<VariableAccess> list = task.getTaskController().getVariableAccesses();
        for (VariableAccess v : list) {
            VariableAccessHandler vh = new VariableAccessHandler(v, task);
            ret.add(vh);
        }
        return ret;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setLabel(String label) {
        variableAccess.setLabel(label.trim());
    }

    public String getLabel() {
        return variableAccess.getLabel();
    }

    public boolean isFragment() {
        return isTipoFragment(type);
    }

    public boolean isPossuiDominio() {
        return possuiDominio;
    }

    public boolean isData() {
        return isData;
    }
    
    public boolean isFile() {
        return isFile;
    }

    private void setMappedName(String name, VariableType type, Object... extra) {
        if (name == null) {
            throw new IllegalStateException(InfoxMessages.getInstance().get("processBuilder.validationError.varSemNome"));
        }
        StringBuilder sb = new StringBuilder().append(type.name()).append(":").append(name);
        for (Object value : extra) {
            sb.append(":").append(value);
        }
        this.variableAccess.setMappedName(sb.toString());
    }

    public void limparConfiguracoes() {
    	getVariableAccess().setConfiguration(null);
        getModeloEditorHandler().init(getVariableAccess());
        VariavelClassificacaoDocumentoAction v = BeanManager.INSTANCE.getReference(VariavelClassificacaoDocumentoAction.class);
        v.setCurrentVariable(getVariableAccess());
        getDataHandler().init(getVariableAccess());
        getMaxMinHandler().init(getVariableAccess());
        getStringHandler().init(getVariableAccess());
        getDominioHandler().init(getVariableAccess());
    }

    public FragmentConfiguration getFragmentConfiguration() {
        return fragmentConfiguration;
    }

    public void setFragmentConfiguration(FragmentConfiguration fragmentConfiguration) {
        if ((this.fragmentConfiguration = fragmentConfiguration) != null) {
            setMappedName(name, type, fragmentConfiguration.getCode());
        } else {
            setMappedName(name, type);
        }
    }

    public boolean isIniciaVazia() {
		return access[4];
	}
    
    public void setIniciaVazia(boolean iniciaVazia) {
		access[4] = iniciaVazia;
		variableAccess.setAccess(new Access(getAccess()));
	}
    
    public boolean podeIniciarVazia() {
    	return isWritable() && type != VariableType.FRAGMENT && type != VariableType.FRAME && type != VariableType.PAGE && type != VariableType.TASK_PAGE;
    }

	public VariableEditorModeloHandler getModeloEditorHandler() {
		return modeloEditorHandler;
	}

	public VariableDataHandler getDataHandler() {
		return dataHandler;
	}

	public void setDataHandler(VariableDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	public VariableDominioEnumerationHandler getDominioHandler() {
		return dominioHandler;
	}

	public void setDominioHandler(VariableDominioEnumerationHandler dominioHandler) {
		this.dominioHandler = dominioHandler;
	}

	public boolean isNumericOrMonetary() {
		return isNumericOrMonetary;
	}

	public void setNumericOrMonetary(boolean isNumericOrMonetary) {
		this.isNumericOrMonetary = isNumericOrMonetary;
	}

	public VariableMaxMinHandler getMaxMinHandler() {
		return maxMinHandler;
	}

	public void setMaxMinHandler(VariableMaxMinHandler maxMinHandler) {
		this.maxMinHandler = maxMinHandler;
	}

	public VariableStringHandler getStringHandler() {
		return stringHandler;
	}

	public void setStringHandler(VariableStringHandler stringHandler) {
		this.stringHandler = stringHandler;
	}
}
