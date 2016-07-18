package br.com.infox.ibpm.variable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Named;

import org.reflections.Reflections;

@Singleton
@Startup
@Named
public class TaskpageMapLoader implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, TaskpageVO> taskpageMap = new HashMap<>();

    @PostConstruct
    private void init() {
        Reflections r = new Reflections("br.com.infox");
        Set<Class<?>> types = r.getTypesAnnotatedWith(Taskpage.class);
        for (Class<?> type : types) {
            Taskpage taskpageAnnotation = type.getAnnotation(Taskpage.class);
            TaskpageVO taskpage;
            if (taskpageMap.containsKey(taskpageAnnotation.name())) {
                taskpage = taskpageMap.get(taskpageAnnotation.name());
                if (!taskpage.getDescription().equals(taskpageAnnotation.description())) {
                    taskpage.setDescription(taskpage.getDescription() + " " + taskpageAnnotation.description());
                    populateTaskpageVO(type, taskpage);
                }
            } else {
                taskpage = new TaskpageVO(taskpageAnnotation.name(), taskpageAnnotation.description());
                populateTaskpageVO(type, taskpage);
                taskpageMap.put(taskpageAnnotation.name(), taskpage);
            }
        }
    }

    private void populateTaskpageVO(Class<?> type, TaskpageVO taskpage) {
        List<TaskpageParameterVO> parameters = taskpage.getParameters();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TaskpageParameter.class)) {
                TaskpageParameter parameter = field.getAnnotation(TaskpageParameter.class);
                TaskpageParameterVO parameterVO = new TaskpageParameterVO(parameter);
                if (!parameters.contains(parameterVO)) {
                    parameters.add(parameterVO);
                }
            }
        }
        // Considera também a herança da classe anotada
        Class<?> superclass = type.getSuperclass();
        if (!Object.class.equals(superclass)) populateTaskpageVO(superclass, taskpage);
    }

    public TaskpageVO getTaskpage(String taskpageName) {
        return taskpageMap.get(taskpageName);
    }
}
