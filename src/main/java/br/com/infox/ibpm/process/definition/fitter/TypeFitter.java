package br.com.infox.ibpm.process.definition.fitter;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.task.action.TaskPageAction;
import br.com.infox.ibpm.task.handler.TaskHandler;
import br.com.infox.ibpm.variable.VariableAccessHandler;
import br.com.infox.jboss.context.ContextFacade;
import br.com.itx.util.FileUtil;

@Name(TypeFitter.NAME)
@AutoCreate
public class TypeFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "typeFitter";
    private static final LogProvider LOG = Logging.getLogProvider(TypeFitter.class);

    private List<String> typeList;
    private Properties types;

    @SuppressWarnings({ RAWTYPES, UNCHECKED })
    public List getTypeList() {
        if (typeList == null) {
            String path = ContextFacade.getServletContext(null).getRealPath("/WEB-INF/xhtml/components/jbpmComponents.properties");
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
                        if ("null".equals(o1)) {
                            return -1;
                        }
                        if ("null".equals(o2)) {
                            return 1;
                        }
                        return types.getProperty(o1).compareTo(types.getProperty(o2));
                    }

                });
            } catch (Exception e) {
                FacesMessages.instance().add(Severity.ERROR, "Erro ao carregar a lista de componentes: {0}", e);
                LOG.error(".getTypeList()", e);
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

    private void verifyAvaliableTypes(List<String> tList) {
        TaskHandler currentTask = getProcessBuilder().getTaskFitter().getCurrentTask();
        if (currentTask != null) {
            for (VariableAccessHandler vah : currentTask.getVariables()) {
                if (vah.getType().equals(TaskPageAction.TASK_PAGE_COMPONENT_NAME)) {
                    removeDifferentType(TaskPageAction.TASK_PAGE_COMPONENT_NAME, tList);
                    break;
                } else if (!"null".equals(vah.getType())) {
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

    @Override
    public void clear() {

    }
}
