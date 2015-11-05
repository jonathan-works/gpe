package br.com.infox.bpm.cdi.qualifier;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import org.jbpm.graph.def.Event;

import br.com.infox.epp.cdi.config.BeanManager;

public final class Events {

    private static final Map<String, Annotation> REGISTERED_EVENTS = new HashMap<>();

    static {
        // Registered Events for JBPM
        REGISTERED_EVENTS.put(Event.EVENTTYPE_NODE_ENTER, new AnnotationLiteral<NodeEnter>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_SUPERSTATE_LEAVE, new AnnotationLiteral<SuperStateLeave>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_SUBPROCESS_END, new AnnotationLiteral<SubProcessEnd>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_NODE_LEAVE, new AnnotationLiteral<NodeLeave>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_BEFORE_SIGNAL, new AnnotationLiteral<BeforeSignal>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_SUPERSTATE_ENTER, new AnnotationLiteral<SuperStateEnter>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_PROCESS_START, new AnnotationLiteral<ProcessStart>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_TRANSITION, new AnnotationLiteral<Transition>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_PROCESS_END, new AnnotationLiteral<ProcessEnd>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_TASK_END, new AnnotationLiteral<TaskEnd>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_TASK_START, new AnnotationLiteral<TaskStart>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_SUBPROCESS_CREATED, new AnnotationLiteral<SubProcessCreated>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_AFTER_SIGNAL, new AnnotationLiteral<AfterSignal>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_TASK_ASSIGN, new AnnotationLiteral<TaskAssign>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_TASK_CREATE, new AnnotationLiteral<TaskCreate>() {
            private static final long serialVersionUID = 1L;
        });
        REGISTERED_EVENTS.put(Event.EVENTTYPE_TIMER, new AnnotationLiteral<Timer>() {
            private static final long serialVersionUID = 1L;
        });
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface NodeEnter {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface SuperStateLeave {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface SubProcessEnd {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface NodeLeave {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface BeforeSignal {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface SuperStateEnter {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface ProcessStart {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface Transition {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface ProcessEnd {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface TaskEnd {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface TaskStart {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface SubProcessCreated {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface AfterSignal {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface TaskAssign {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface TaskCreate {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    public @interface Timer {
    }

    public static boolean isRegisteredEvent(String eventType) {
        return REGISTERED_EVENTS.containsKey(eventType);
    }

    public static Annotation fromEventType(String eventType) {
        return REGISTERED_EVENTS.get(eventType);
    }
    
    public static void fireEvent(Object object, String eventType) {
        BeanManager.INSTANCE.fireEvent(object, fromEventType(eventType));
    }

}
