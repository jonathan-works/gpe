/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.jbpm.handler;

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
import org.jbpm.context.def.Access;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.list.associated.AssociatedTipoModeloVariavelList;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.VariavelTipoModelo;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.ReflectionsUtil;


public class VariableAccessHandler implements Serializable {

	private static final long serialVersionUID = -4113688503786103974L;
	private static final String PREFIX = "#{modeloDocumento.set('";
	private VariableAccess variableAccess;
	private String name;
	private String label;
	private String type;
	private boolean[] access;
	private List<Integer> modeloList;
	private List<ModeloDocumento> modeloDocumentoList;
	private Task task;
	private boolean mudouModelo;

	public VariableAccessHandler(VariableAccess variableAccess, Task task) {
		this.task = task;
		this.variableAccess = variableAccess;
		String mappedName = variableAccess.getMappedName();
		if (mappedName.indexOf(':') > 0) {
			this.type = mappedName.split(":")[0];
		} else {
			this.type = "default";
		}
		this.name = variableAccess.getVariableName();
		access = new boolean[3];
		access[0] = variableAccess.isReadable();
		access[1] = variableAccess.isWritable();
		access[2] = variableAccess.isRequired();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
	    String auxiliarName = name.replace(' ', '_').replace('/', '_');
		if (!auxiliarName.equals(this.name)){
			this.name = auxiliarName;
			if ("page".equals(type) && !pageExists()) {
				return;
			}
			ReflectionsUtil.setValue(variableAccess, "variableName", auxiliarName);
			ReflectionsUtil.setValue(variableAccess, "mappedName", type + ":" + auxiliarName);
		}
	}
	
	public VariableAccess update() {
		if (modeloList != null && mudouModelo) {
			updateModelo();
		}
		variableAccess = new VariableAccess(name, getAccess(), type + ":" + name );
		return variableAccess;
	}

	private void updateModelo() {
		StringBuilder newExpression = new StringBuilder(PREFIX)
			.append(name)
			.append("',");
		Action action = getAction(newExpression.toString());
		if (modeloList.size() > 0) {
			for (int i = 0; i < modeloList.size(); i++) {
				Integer id = modeloList.get(i);
				newExpression.append(id);
				if (i < modeloList.size()-1) {
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
			FacesMessages.instance().add(StatusMessage.Severity.INFO,
					"A página '" + page + "' não foi encontrada.");
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
				FacesMessages.instance().add(StatusMessage.Severity.INFO,
						"O form '" + nameForm + "' não foi encontrado.");
				return;
			}
		}
		if ("page".equals(type) && !pageExists()) {
			setWritable(true);
			return;
		}
		ReflectionsUtil.setValue(variableAccess, "mappedName", type + ":" + name);
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
			if(writable) {
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
			if(required) {
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
			for (Integer id : modeloList) {
				modeloDocumentoList.add(EntityUtil.getEntityManager().find(ModeloDocumento.class, id));
			}
		}
		return modeloDocumentoList;
	}
	
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
			if (v.endsWith(":" + name)) {
				this.label = getLabel();
				setType( v.split(":")[0]);
				setWritable(false);
			}
		}
	}

	@SuppressWarnings("unchecked")
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
			while(st.hasMoreTokens()) {
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
		if (! labelAuxiliar.equals(this.label) && !"".equals(labelAuxiliar)) {
			this.label = labelAuxiliar;
			JbpmUtil.instance().storeLabel(name, labelAuxiliar);
		}
	}

	public String getLabel() {
		if (!"".equals(name)) {
			this.label = VariableHandler.getLabel(name);
		}
		return this.label;
	}
}