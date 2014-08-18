package br.com.infox.ibpm.task.handler;

import java.text.ParseException;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.epp.processo.timer.TaskExpirationInfo;
import br.com.infox.epp.processo.timer.TaskExpirationProcessor;
import br.com.infox.seam.util.ComponentUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TaskExpirationHandler implements ActionHandler {

    private static final long serialVersionUID = 1L;

    private TaskExpirationInfo expirationInfo;
    
    public static TaskExpirationInfo parseTaskExpirationInfo(String json) throws ParseException {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        if (json.startsWith("<![CDATA[")) {
            json = json.substring(9, json.length() - 3);
        }
        TaskExpirationInfo info = gson.fromJson(json, TaskExpirationInfo.class);
        return info;
    }
    
    public TaskExpirationHandler(String json) throws ParseException {
        expirationInfo = parseTaskExpirationInfo(json);
    }

    @Override
    public void execute(ExecutionContext executionContext) throws Exception {
        TaskExpirationProcessor taskExpirationProcessor = ComponentUtil.getComponent(TaskExpirationProcessor.NAME);
        expirationInfo.setTaskId(executionContext.getTaskInstance().getId());
        taskExpirationProcessor.endTask(expirationInfo.getExpiration(), expirationInfo);
    }
}
