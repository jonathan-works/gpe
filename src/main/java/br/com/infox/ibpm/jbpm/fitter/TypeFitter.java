package br.com.infox.ibpm.jbpm.fitter;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.bpm.action.TaskPageAction;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.infox.ibpm.jbpm.handler.VariableAccessHandler;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.FacesUtil;
import br.com.itx.util.FileUtil;

public class TypeFitter implements Serializable, Fitter{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "typeFitter";
	
	private List<String> typeList;
	private Properties types;
	
	private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	
	private void verifyAvaliableTypes(List<String> tList) {
		TaskHandler currentTask = pb.getTaskFitter().getCurrentTask();
		if (currentTask != null) {
			for (VariableAccessHandler vah : currentTask.getVariables()) {
				if (vah.getType().equals(
						TaskPageAction.TASK_PAGE_COMPONENT_NAME)) {
					removeDifferentType(
							TaskPageAction.TASK_PAGE_COMPONENT_NAME, tList);
					break;
				} else if (!vah.getType().equals("null")) {
					tList.remove(TaskPageAction.TASK_PAGE_COMPONENT_NAME);
					break;
				}
			}
		}
	}
	
	private void removeDifferentType(String newName, List<String> tList) {
		for (Iterator<String> iterator = tList.iterator(); iterator.hasNext();) {
			String i = iterator.next();
			if (!i.equals(newName)) {
				iterator.remove();
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getTypeList() {
		if (typeList == null) {
			String path = FacesUtil.getServletContext(null).getRealPath(
					"/WEB-INF/xhtml/components/jbpmComponents.properties");
			types = new Properties();
			FileInputStream input = null;
			try {
				input = new FileInputStream(path);
				types.load(input);
				typeList = new ArrayList(types.keySet());
				verifyAvaliableTypes(typeList);
				Collections.sort(typeList, new Comparator<String>() {

					@Override
					public int compare(String o1, String o2) {
						if (o1.equals("null")) {
							return -1;
						}
						if (o2.equals("null")) {
							return 1;
						}
						return types.getProperty(o1).compareTo(
								types.getProperty(o2));
					}

				});
			} catch (Exception e) {
				FacesMessages.instance().add(Severity.ERROR,
						"Erro ao carregar a lista de componentes: {0}", e);
				e.printStackTrace();
			} finally {
				FileUtil.close(input);
			}
		}
		return typeList;
	}

	public void setTypeList(List<String> tList) {
		this.typeList = tList;
	}
	
	public String getTypeLabel(String type) {
		if (types == null) {
			getTypeList();
		}
		return (String) types.get(type);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
