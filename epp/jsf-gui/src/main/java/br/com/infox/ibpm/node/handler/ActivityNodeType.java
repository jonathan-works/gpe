package br.com.infox.ibpm.node.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jbpm.activity.exe.ActivityBehavior;
import org.jbpm.activity.exe.LoopActivityBehavior;
import org.jbpm.activity.exe.MultiInstanceActivityBehavior;
import org.jbpm.activity.exe.ParallelMultiInstanceActivityBehavior;
import org.jbpm.activity.exe.SequentialMultiInstanceActivityBehavior;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.type.Displayable;

public enum ActivityNodeType implements Displayable {
    
    NONE(null), 
    LOOP(LoopActivityBehavior.class), 
    SEQUENTIAL_MULTIINSTANCE(SequentialMultiInstanceActivityBehavior.class), 
    PARALLEL_MULTIINSTANCE(ParallelMultiInstanceActivityBehavior.class);
    
    private Class<? extends ActivityBehavior> activityClass;
    private List<Pair<String, String>> variables;
    
    private ActivityNodeType(Class<? extends ActivityBehavior> clazz) {
        this.activityClass = clazz;
        if (activityClass != null) {
            this.variables = loadVariables(activityClass);
        }
    }
    
    private static List<Pair<String, String>> loadVariables(Class<? extends ActivityBehavior> clazz) {
        List<Pair<String, String>> variables = new ArrayList<>();
        if (LoopActivityBehavior.class.equals(clazz)) {
            variables.add(Pair.of(LoopActivityBehavior.LOOP_COUNTER, "Variável que indica a contagem atual. Inicia com 0."));
        } else if (MultiInstanceActivityBehavior.class.isAssignableFrom(clazz)) {
            variables.add(Pair.of(MultiInstanceActivityBehavior.LOOP_COUNTER, "Indica a contagem atual. Inicia com 0."));
            variables.add(Pair.of(MultiInstanceActivityBehavior.NUMBER_OF_INSTANCES, "Indica o total de instâncias criadas ou que serão criadas."));
            variables.add(Pair.of(MultiInstanceActivityBehavior.NUMBER_OF_ACTIVE_INSTANCES, "Indica o número de instâncias ativas."));
            variables.add(Pair.of(MultiInstanceActivityBehavior.NUMBER_OF_COMPLETED_INSTANCES, "Indica o número de instâncias que finalizaram pelo usuário."));
            variables.add(Pair.of(MultiInstanceActivityBehavior.NUMBER_OF_TERMINATED_INSTANCES, "Indica o número de instâncias que foram terminadas, foram cancelada."));
        }
        return variables;
    }

    public Class<? extends ActivityBehavior> getActivityClass() {
        return activityClass;
    }
    
    public List<Pair<String, String>> getVariables() {
        return variables;
    }

    public static ActivityNodeType fromActivity(ActivityBehavior activityBehavior) {
        if (activityBehavior == null) {
            return NONE;
        } else if (LOOP.getActivityClass().isAssignableFrom(activityBehavior.getClass())) {
            return LOOP;
        } else if (SEQUENTIAL_MULTIINSTANCE.getActivityClass().isAssignableFrom(activityBehavior.getClass())) {
            return SEQUENTIAL_MULTIINSTANCE;
        } else if (PARALLEL_MULTIINSTANCE.getActivityClass().isAssignableFrom(activityBehavior.getClass())) {
            return PARALLEL_MULTIINSTANCE;
        } else {
            return null;
        }
    }

    @Override
    public String getLabel() {
        return InfoxMessages.getInstance().get("process.def.activity.activityType."+name());
    }
    
    public ActivityBehavior createActivity() {
        if (activityClass == null) return null;
        try {
            return activityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }
    
    public boolean isNone() {
        return this == NONE;
    }
    
    public boolean isMultiInstance() {
        return this == SEQUENTIAL_MULTIINSTANCE || this == PARALLEL_MULTIINSTANCE;
    }
    
    public boolean isLoop() {
        return this == LOOP;
    }
}
