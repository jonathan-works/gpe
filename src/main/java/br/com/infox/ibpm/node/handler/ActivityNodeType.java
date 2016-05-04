package br.com.infox.ibpm.node.handler;

import org.jbpm.activity.exe.ActivityBehavior;
import org.jbpm.activity.exe.LoopActivityBehavior;
import org.jbpm.activity.exe.ParallelMultiInstanceActivityBehavior;
import org.jbpm.activity.exe.SequentialMultiInstanceActivityBehavior;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.type.Displayable;

public enum ActivityNodeType implements Displayable {
    
    NONE(null), 
    LOOP(LoopActivityBehavior.class), 
    SEQMULTIINSTANCE(SequentialMultiInstanceActivityBehavior.class), 
    PARALLELMULTIINSTANCE(ParallelMultiInstanceActivityBehavior.class);
    
    private Class<? extends ActivityBehavior> activityClass;
    
    private ActivityNodeType(Class<? extends ActivityBehavior> clazz) {
        this.activityClass = clazz;
    }
    
    public Class<? extends ActivityBehavior> getActivityClass() {
        return activityClass;
    }
    
    public static ActivityNodeType fromActivity(ActivityBehavior activityBehavior) {
        if (activityBehavior == null) {
            return NONE;
        } else if (LOOP.getActivityClass().isAssignableFrom(activityBehavior.getClass())) {
            return LOOP;
        } else if (SEQMULTIINSTANCE.getActivityClass().isAssignableFrom(activityBehavior.getClass())) {
            return SEQMULTIINSTANCE;
        } else if (PARALLELMULTIINSTANCE.getActivityClass().isAssignableFrom(activityBehavior.getClass())) {
            return PARALLELMULTIINSTANCE;
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
        return this == SEQMULTIINSTANCE || this == PARALLELMULTIINSTANCE;
    }
    
    public boolean isLoop() {
        return this == LOOP;
    }
}
