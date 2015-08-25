package br.com.infox.ibpm.variable;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static java.text.MessageFormat.format;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.seam.Component;
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

import br.com.infox.core.list.EntityList;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;
import br.com.infox.epp.documento.list.associated.AssociatedTipoModeloVariavelList;
import br.com.infox.epp.documento.list.associative.AssociativeModeloDocumentoList;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.TaskHandlerVisitor;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.ibpm.variable.type.ValidacaoDataEnum;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

public class VariableAccessHandler implements Serializable {

    public static final String ACCESS_VARIAVEL_INICIA_VAZIA = "reset";
	public static final String EVENT_JBPM_VARIABLE_NAME_CHANGED = "jbpmVariableNameChanged";
    private static final String COMMA = ",";
    private static final long serialVersionUID = -4113688503786103974L;
    private static final String PREFIX = "#{modeloDocumento.set('";
    private static final LogProvider LOG = Logging.getLogProvider(VariableAccessHandler.class);
    private VariableAccess variableAccess;
    private String name;
    private String label;
    private VariableType type;
    private boolean[] access;
    private List<Integer> modeloList;
    private List<ModeloDocumento> modeloDocumentoList;
    private Task task;
    private boolean mudouModelo;
    private DominioVariavelTarefa dominioVariavelTarefa;
    private boolean possuiDominio = false;
    private ValidacaoDataEnum validacaoDataEnum;
    private boolean isData = false;
    private boolean isFile;
    private boolean fragment;
    private FragmentConfiguration fragmentConfiguration;
    
    public VariableAccessHandler(VariableAccess variableAccess, Task task) {
        this.task = task;
        this.variableAccess = variableAccess;
        String mappedName = variableAccess.getMappedName();
        if (mappedName.indexOf(':') > 0) {
            String[] tokens = mappedName.split(":");
            this.type = VariableType.valueOf(tokens[0]);
            this.name = variableAccess.getVariableName();
            switch (type) {
                case DATE:
                    if (tokens.length < 3) {
                    setValidacaoDataEnum(ValidacaoDataEnum.L);
                    } else {
                    setValidacaoDataEnum(ValidacaoDataEnum.valueOf(tokens[2]));
                    }
                break;
                case ENUMERATION:
                    if (tokens.length >= 3) {
                        DominioVariavelTarefaManager dominioVariavelTarefaManager = (DominioVariavelTarefaManager) Component.getInstance(DominioVariavelTarefaManager.NAME);
                        this.dominioVariavelTarefa = dominioVariavelTarefaManager.find(Integer.valueOf(tokens[2]));
                    }
                break;
            case FRAGMENT:
                if (tokens.length >= 3) {
                    setFragmentConfiguration(ComponentUtil.<FragmentConfigurationCollector> getComponent(
                            FragmentConfigurationCollector.NAME).getByCode(tokens[2]));
                }
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
    }

    private boolean tipoPossuiDominio(VariableType type) {
        return VariableType.ENUMERATION.equals(type);
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
            ReflectionsUtil.setValue(variableAccess, "variableName", auxiliarName);
            
            if (isData || type == VariableType.DATE) {
                setValidacaoDataEnum(validacaoDataEnum);
            } else if (isFragment()){
                setFragmentConfiguration(fragmentConfiguration);
            } else if (isPossuiDominio()) {
                setDominioVariavelTarefa(dominioVariavelTarefa);
            } else {
                setMappedName(auxiliarName,type);
            }
        }
    }

    public DominioVariavelTarefa getDominioVariavelTarefa() {
        return dominioVariavelTarefa;
    }

    public void setDominioVariavelTarefa(DominioVariavelTarefa dominioVariavelTarefa) {
        this.dominioVariavelTarefa = dominioVariavelTarefa;
        if (this.dominioVariavelTarefa != null && name != null) {
            setMappedName(name, type, this.dominioVariavelTarefa.getId());
        } else {
            setMappedName(name, type);
        }
    }

    public VariableAccess update() {
        if (modeloList != null && mudouModelo) {
            updateModelo();
        }
        variableAccess = new VariableAccess(name, getAccess(), format("{0}:{1}", type.name(), name));
        return variableAccess;
    }

    private void updateModelo() {
        StringBuilder newExpression = new StringBuilder(PREFIX).append(name).append("',");
        Action action = getAction(newExpression.toString());
        if (!modeloList.isEmpty()) {
            for (int i = 0; i < modeloList.size(); i++) {
                Integer id = modeloList.get(i);
                newExpression.append(id);
                if (i < modeloList.size() - 1) {
                    newExpression.append(COMMA);
                }
            }
            newExpression.append(")}");
            action.setName(name);
            action.setActionExpression(newExpression.toString());
        } else {
            removeAction(action);
        }
    }
    
    
    @SuppressWarnings("unchecked")
	public void removeTaskAction(String actionName){
    	GraphElement parent = task.getParent();
    	Map<String, Event> events = parent.getEvents();
    	if (events != null) {
            for (Object entry : events.entrySet()) {
    			Event event = ((Map.Entry<String,Event>)entry).getValue();
    			for (Object o : event.getActions()) {
                    Action a = (Action) o;
                    String name = a.getName();
                    String exp = a.getActionExpression();
                    if ( (name != null && name.equalsIgnoreCase(actionName) ) || ( exp != null  && exp.contains("'"+ actionName +"'") ) ) {
                    	event.removeAction(a);
                    	 if (event.getActions().isEmpty()) {
                             event.getGraphElement().removeEvent(event);
                         }
                        return ;
                    }
                }
    		}
    	}
    }

    private void removeAction(Action action) {
        action.setActionExpression(null);
        Event event = action.getEvent();
        event.removeAction(action);
        if (event.getActions().isEmpty()) {
            event.getGraphElement().removeEvent(event);
        }
    }

    private Action getAction(String newExpression) {
        GraphElement parent = task.getParent();
        Event e = parent.getEvent(Event.EVENTTYPE_NODE_ENTER);
        if (e == null) {
            e = new Event(parent, Event.EVENTTYPE_NODE_ENTER);
            parent.addEvent(e);
        }
        if (e.getActions() != null) {
            for (Object o : e.getActions()) {
                Action a = (Action) o;
                String exp = a.getActionExpression();
                if (exp != null && exp.startsWith(newExpression)) {
                    return a;
                }
            }
        }
        Action action = new Action();
        e.addAction(action);
        return action;
    }
    
    
    private Action getActionNodeEnterByName(String name) {
    	GraphElement parent = task.getParent();
    	Event e = parent.getEvent(Event.EVENTTYPE_NODE_ENTER);
        if (e == null) {
            e = new Event(parent, Event.EVENTTYPE_NODE_ENTER);
            parent.addEvent(e);
        }
    	if (e.getActions() != null) {
    		for (Object o : e.getActions()) {
    			Action a = (Action) o;
    			String expName = a.getName();
    			if (expName != null && expName.equalsIgnoreCase(name)) {
    				return a;
    			}
    		}
    	}
    	Action action = new Action();
    	action.setName(name);
    	e.addAction(action);
    	return action;
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
            ReflectionsUtil.setValue(variableAccess, "access", new Access(getAccess()));
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
            ReflectionsUtil.setValue(variableAccess, "access", new Access(getAccess()));
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
            ReflectionsUtil.setValue(variableAccess, "access", new Access(getAccess()));
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
            ReflectionsUtil.setValue(variableAccess, "access", new Access(getAccess()));
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

    public List<Integer> getModeloList() {
        return modeloList;
    }

    public void setModeloList(List<Integer> list) {
        modeloList = list;
    }

    public List<ModeloDocumento> getModeloDocumentoList() {
        if (modeloDocumentoList == null && modeloList != null) {
            modeloDocumentoList = new ArrayList<ModeloDocumento>();
            ModeloDocumentoManager modeloDocumentoManager = ComponentUtil.getComponent(ModeloDocumentoManager.NAME);
            for (Integer id : modeloList) {
                modeloDocumentoList.add(modeloDocumentoManager.find(id));
            }
        }
        return modeloDocumentoList;
    }

    // TODO: Esse entityList está bizarro, é a causa dos 2 warnings abaixo
    @SuppressWarnings({ UNCHECKED, RAWTYPES })
    public void addModelo(ModeloDocumento modelo) {
        if (modeloDocumentoList == null) {
            modeloDocumentoList = new ArrayList<>();
            modeloList = new ArrayList<>();
        }
        modeloDocumentoList.add(modelo);
        modeloList.add(modelo.getIdModeloDocumento());
        EntityList modeloDocumentoList = ComponentUtil.getComponent(AssociatedTipoModeloVariavelList.NAME);
        modeloDocumentoList.getResultList().add(modelo);
        refreshModelosAssociados();
        mudouModelo = true;
        updateModelo();
    }

    private void refreshModelosAssociados() {
        AssociativeModeloDocumentoList associativeModeloDocumentoList = ComponentUtil.getComponent(AssociativeModeloDocumentoList.NAME);
        associativeModeloDocumentoList.refreshModelosAssociados();
    }

    public void removeModelo(ModeloDocumento modelo) {
        modeloDocumentoList.remove(modelo);
        modeloList.remove(Integer.valueOf(modelo.getIdModeloDocumento()));
        EntityList<VariavelTipoModelo> modeloDocumentoList = ComponentUtil.getComponent(AssociatedTipoModeloVariavelList.NAME);
        modeloDocumentoList.getResultList().remove(modelo);
        refreshModelosAssociados();
        mudouModelo = true;
        updateModelo();
    }

    public void copyVariable() {
        TaskHandlerVisitor visitor = new TaskHandlerVisitor(true);
        visitor.visit(task);
        for (String v : visitor.getVariables()) {
            String[] tokens = v.split(":");
            if (tokens.length > 1 && tokens[1].equals(name)) {
                this.label = getLabel();
                setType(VariableType.valueOf(tokens[0]));
                setWritable(false);
                switch (type) {
                case FRAGMENT:
                    if (tokens.length >= 3) {
                        setFragmentConfiguration(ComponentUtil.<FragmentConfigurationCollector> getComponent(
                                FragmentConfigurationCollector.NAME).getByCode(tokens[2]));
                    }
                    break;
                    case DATE:
                        if (tokens.length < 3) {
                            setValidacaoDataEnum(ValidacaoDataEnum.L);
                        } else {
                            setValidacaoDataEnum(ValidacaoDataEnum.valueOf(tokens[2]));
                        }
                    break;
                    case ENUMERATION:
                        if (tokens.length >= 3) {
                            DominioVariavelTarefaManager dominioVariavelTarefaManager = (DominioVariavelTarefaManager) Component.getInstance(DominioVariavelTarefaManager.NAME);
                            setDominioVariavelTarefa(dominioVariavelTarefaManager.find(Integer.valueOf(tokens[2])));
                        }
                    break;
                    default:
                    break;
                }
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    public static List<VariableAccessHandler> getList(Task task) {
        List<VariableAccessHandler> ret = new ArrayList<>();
        if (task.getTaskController() == null) {
            return ret;
        }
        Map<String, List<Integer>> modeloMap = new HashMap<String, List<Integer>>();
        GraphElement parent = task.getParent();
        Event e = parent.getEvent(Event.EVENTTYPE_NODE_ENTER);

        if (e != null && e.getActions() != null) {
            Action action = (Action) e.getActions().get(0);
            modeloMap = getModeloMap(action.getActionExpression());
        }

        List<VariableAccess> list = task.getTaskController().getVariableAccesses();
        for (VariableAccess v : list) {
            VariableAccessHandler vh = new VariableAccessHandler(v, task);
            String name = v.getVariableName();
            if (modeloMap.containsKey(name)) {
                vh.setModeloList(modeloMap.get(name));
            }
            ret.add(vh);
        }
        return ret;
    }

    private static Map<String, List<Integer>> getModeloMap(String texto) {
        Map<String, List<Integer>> ret = new LinkedHashMap<String, List<Integer>>();
        if (texto != null && texto.startsWith(PREFIX)) {
            String textoAuxiliar = texto.substring(PREFIX.length());
            StringTokenizer st = new StringTokenizer(textoAuxiliar, ",')}");
            List<Integer> values = new ArrayList<Integer>();
            ret.put(st.nextToken(), values);
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                values.add(Integer.parseInt(s.trim()));
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setLabel(String label) {
        String labelAuxiliar = label.trim();
        if (!labelAuxiliar.equals(this.label) && !"".equals(labelAuxiliar)) {
            this.label = labelAuxiliar;
            storeLabel(name, labelAuxiliar);
        }
    }

    // TODO verificar por que tem registro duplicado na base
    private void storeLabel(String name, String label) {
        Map<String, String> map = ComponentUtil.getComponent("jbpmMessages");
        final String mappedVariableName = format("{0}:{1}", task.getProcessDefinition().getName(), name);
        final String old = map.get(mappedVariableName);
        if (!label.equals(old)) {
            map.put(mappedVariableName, label);
            JbpmVariavelLabel j = new JbpmVariavelLabel();
            j.setNomeVariavel(mappedVariableName);
            j.setLabelVariavel(label);
            try {
                genericManager().persist(j);
            } catch (DAOException e) {
                LOG.error(format("Não foi possível gravar a JbpmVariavelLabel: {0}", j), e);
            }
        }
    }

    public String getLabel() {
        if (!"".equals(name)) {
            setLabel(VariableHandler.getLabel(format("{0}:{1}", task.getProcessDefinition().getName(), name)));
        }
        return this.label;
    }

    public GenericManager genericManager() {
        return ComponentUtil.getComponent(GenericManager.NAME);
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

    public ValidacaoDataEnum[] getTypeDateValues() {
        return ValidacaoDataEnum.values();
    }

    public ValidacaoDataEnum getValidacaoDataEnum() {
        return validacaoDataEnum;
    }

    public void setValidacaoDataEnum(ValidacaoDataEnum validacaoDataEnum) {
        if ((this.validacaoDataEnum = validacaoDataEnum) != null && name != null) {
            setMappedName(name, type, this.validacaoDataEnum.toString());
        } else {
            setMappedName(name, type);
        }
    }

    private void setMappedName(String name, VariableType type, Object... extra) {
        if (name == null) {
            throw new IllegalStateException("Existe uma variável sem nome na tarefa ");
        }
        StringBuilder sb = new StringBuilder().append(type.name()).append(":").append(name);
        for (Object value : extra) {
            sb.append(":").append(value);
        }
        variableAccess.setMappedName(sb.toString());
    }

    public void limparModelos() {
        List<ModeloDocumento> modelos = getModeloDocumentoList();
        if (modelos != null) {
            modelos = new ArrayList<>(modelos);
            for (ModeloDocumento modelo : modelos) {
                removeModelo(modelo);
            }
        }
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
    
}
