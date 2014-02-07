package br.com.infox.ibpm.variable;

import static br.com.infox.core.constants.WarningConstants.RAWTYPES;
import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.Access;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;
import br.com.infox.epp.documento.list.associated.AssociatedTipoModeloVariavelList;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.ibpm.task.handler.TaskHandlerVisitor;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.ReflectionsUtil;

public class VariableAccessHandler implements Serializable {

    private static final long serialVersionUID = -4113688503786103974L;
    private static final String PREFIX = "#{modeloDocumento.set('";
    private static final LogProvider LOG = Logging.getLogProvider(VariableAccessHandler.class);
    private VariableAccess variableAccess;
    private String name;
    private String label;
    private String type;
    private boolean[] access;
    private List<Integer> modeloList;
    private List<ModeloDocumento> modeloDocumentoList;
    private Task task;
    private boolean mudouModelo;
    private DominioVariavelTarefa dominioVariavelTarefa;
    private boolean possuiDominio = false;

    public VariableAccessHandler(VariableAccess variableAccess, Task task) {
        this.task = task;
        this.variableAccess = variableAccess;
        String mappedName = variableAccess.getMappedName();
        if (mappedName.indexOf(':') > 0) {
            String[] tokens = mappedName.split(":");
            this.type = tokens[0];
            if (tokens.length >= 3) {
                DominioVariavelTarefaManager dominioVariavelTarefaManager = (DominioVariavelTarefaManager) Component.getInstance(DominioVariavelTarefaManager.NAME);
                this.dominioVariavelTarefa = dominioVariavelTarefaManager.find(Integer.valueOf(tokens[2]));
            }
        } else {
            this.type = "default";
        }
        this.name = variableAccess.getVariableName();
        access = new boolean[3];
        access[0] = variableAccess.isReadable();
        access[1] = variableAccess.isWritable();
        access[2] = variableAccess.isRequired();
        this.possuiDominio = tipoPossuiDominio(this.type);
    }

    private boolean tipoPossuiDominio(String type) {
        return "enumeracao".equals(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String auxiliarName = name.replace(' ', '_').replace('/', '_');
        if (!auxiliarName.equals(this.name)) {
            this.name = auxiliarName;
            if ("page".equals(type) && !pageExists()) {
                return;
            }
            ReflectionsUtil.setValue(variableAccess, "variableName", auxiliarName);
            ReflectionsUtil.setValue(variableAccess, "mappedName", type + ":"
                    + auxiliarName);
        }
    }

    public DominioVariavelTarefa getDominioVariavelTarefa() {
        return dominioVariavelTarefa;
    }

    public void setDominioVariavelTarefa(
            DominioVariavelTarefa dominioVariavelTarefa) {
        this.dominioVariavelTarefa = dominioVariavelTarefa;
        if (this.dominioVariavelTarefa != null) {
            ReflectionsUtil.setValue(variableAccess, "mappedName", type + ":"
                    + name + ":" + this.dominioVariavelTarefa.getId());
        } else {
            ReflectionsUtil.setValue(variableAccess, "mappedName", type + ":"
                    + name);
        }
    }

    public VariableAccess update() {
        if (modeloList != null && mudouModelo) {
            updateModelo();
        }
        variableAccess = new VariableAccess(name, getAccess(), type + ":"
                + name);
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
                    newExpression.append(",");
                }
            }
            newExpression.append(")}");
            action.setActionExpression(newExpression.toString());
        } else {
            removeAction(action);
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

    public VariableAccess getVariableAccess() {
        return variableAccess;
    }

    public void setVariableAccess(VariableAccess variableAccess) {
        this.variableAccess = variableAccess;
    }

    public String getType() {
        return type;
    }

    private boolean pageExists() {
        String page = "/" + name.replaceAll("_", "/") + ".xhtml";
        String realPath = ServletLifecycle.getServletContext().getRealPath(page);
        if (!new File(realPath).exists()) {
            FacesMessages.instance().add(StatusMessage.Severity.INFO, "A página '"
                    + page + "' não foi encontrada.");
            return false;
        }
        return true;
    }

    public void setType(String type) {
        this.type = type;
        if ("form".equals(type)) {
            String nameForm = name + "Form";
            boolean existeForm = Component.getInstance(nameForm) != null;
            if (!existeForm) {
                FacesMessages.instance().add(StatusMessage.Severity.INFO, "O form '"
                        + nameForm + "' não foi encontrado.");
                return;
            }
        }
        if ("page".equals(type) && !pageExists()) {
            setWritable(true);
            return;
        }
        ReflectionsUtil.setValue(variableAccess, "mappedName", type + ":"
                + name);
        this.possuiDominio = tipoPossuiDominio(type);
    }

    public boolean isReadable() {
        return variableAccess.getAccess().isReadable();
    }

    public void setReadable(boolean readable) {
        if (readable != variableAccess.isReadable()) {
            access[0] = readable;
            ReflectionsUtil.setValue(variableAccess, "access", new Access(getAccess()));
        }
    }

    public boolean isWritable() {
        return variableAccess.getAccess().isWritable();
    }

    public void setWritable(boolean writable) {
        if (writable != variableAccess.isWritable()) {
            access[1] = writable;
            if (writable) {
                access[0] = true;
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
            if (required) {
                access[0] = true;
                access[1] = true;
            }
            ReflectionsUtil.setValue(variableAccess, "access", new Access(getAccess()));
        }
    }

    private String getAccess() {
        StringBuilder sb = new StringBuilder();
        if (access[2]) {
            access[1] = true;
        }
        if (access[1]) {
            access[0] = true;
        }
        if (access[0]) {
            sb.append("read,");
        }
        if (access[1]) {
            sb.append("write,");
        }
        if (access[2]) {
            sb.append("required");
        }
        String s = sb.toString();
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
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
            modeloDocumentoList = new ArrayList<ModeloDocumento>();
            modeloList = new ArrayList<Integer>();
        }
        modeloDocumentoList.add(modelo);
        modeloList.add(modelo.getIdModeloDocumento());
        EntityList modeloDocumentoList = ComponentUtil.getComponent(AssociatedTipoModeloVariavelList.NAME);
        modeloDocumentoList.getResultList().add(modelo);
        mudouModelo = true;
        updateModelo();
    }

    public void removeModelo(ModeloDocumento modelo) {
        modeloDocumentoList.remove(modelo);
        modeloList.remove(Integer.valueOf(modelo.getIdModeloDocumento()));
        EntityList<VariavelTipoModelo> modeloDocumentoList = ComponentUtil.getComponent(AssociatedTipoModeloVariavelList.NAME);
        modeloDocumentoList.getResultList().remove(modelo);
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
                setType(tokens[0]);
                setWritable(false);
                if (tokens.length >= 3) {
                    DominioVariavelTarefaManager dominioVariavelTarefaManager = (DominioVariavelTarefaManager) Component.getInstance(DominioVariavelTarefaManager.NAME);
                    setDominioVariavelTarefa(dominioVariavelTarefaManager.find(Integer.valueOf(tokens[2])));
                }
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    public static List<VariableAccessHandler> getList(Task task) {
        List<VariableAccessHandler> ret = new ArrayList<VariableAccessHandler>();
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
        String old = map.get(name);
        if (!label.equals(old)) {
            map.put(name, label);
            JbpmVariavelLabel j = new JbpmVariavelLabel();
            j.setNomeVariavel(name);
            j.setLabelVariavel(label);
            try {
                genericManager().persist(j);
            } catch (DAOException e) {
                LOG.error("Não foi possível gravar a JbpmVariavelLabel: " + j, e);
            }
        }
    }

    public String getLabel() {
        if (!"".equals(name)) {
            setLabel(VariableHandler.getLabel(name));
        }
        return this.label;
    }

    public GenericManager genericManager() {
        return ComponentUtil.getComponent(GenericManager.NAME);
    }

    public boolean isPossuiDominio() {
        return possuiDominio;
    }
}
