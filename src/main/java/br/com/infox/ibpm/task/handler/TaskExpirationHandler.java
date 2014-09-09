package br.com.infox.ibpm.task.handler;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.epp.processo.timer.TaskExpirationInfo;
import br.com.infox.epp.processo.timer.TaskExpirationProcessor;
import br.com.infox.seam.util.ComponentUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TaskExpirationHandler implements ActionHandler, CustomAction {

    private static final long serialVersionUID = 1L;
    private static final Pattern CDATA_REGEX = Pattern.compile("<!\\[CDATA\\[(.+?)\\]\\]>");

    private TaskExpirationInfo expirationInfo;
    
    public static TaskExpirationInfo parseTaskExpirationInfo(String json) throws ParseException {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        Matcher matcher = CDATA_REGEX.matcher(json);
        if (matcher.find()) {
            json = matcher.group(1);
        }
        TaskExpirationInfo info = gson.fromJson(json, TaskExpirationInfo.class);
        return info;
    }
    
    public TaskExpirationHandler(String json) throws ParseException {
        expirationInfo = parseTaskExpirationInfo(json);
    }
    
    public TaskExpirationHandler() {
    }

    @Override
    public void execute(ExecutionContext executionContext) throws Exception {
        TaskExpirationProcessor taskExpirationProcessor = ComponentUtil.getComponent(TaskExpirationProcessor.NAME);
        expirationInfo.setTaskId(executionContext.getTaskInstance().getId());
        taskExpirationProcessor.endTask(expirationInfo.getExpiration(), expirationInfo);
    }

    @Override
    public String parseJbpmConfiguration(String configuration) {
        Matcher matcher = CDATA_REGEX.matcher(configuration);
        if (matcher.find()) {
            configuration = matcher.group(1);
        }
        return configuration;
    }
}
