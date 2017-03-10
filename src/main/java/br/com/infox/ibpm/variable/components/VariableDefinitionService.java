package br.com.infox.ibpm.variable.components;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.reflections.Reflections;

@Singleton
@Startup
public class VariableDefinitionService {

	private Map<String, FrameDefinition> framesMap;
	private Map<String, TaskpageDefinition> taskPagesMap;
	
	private static interface AnnotationConverter<A extends Annotation, T extends ComponentDefinition> {
		public T getComponentDefinition(A annotation);
		public String getKey(T value);
	}
	
	private static class TaskpageConverter implements AnnotationConverter<Taskpage, TaskpageDefinition> {
		@Override
		public String getKey(TaskpageDefinition taskpageDefinition) {
			return taskpageDefinition.getId();
		}

		@Override
		public TaskpageDefinition getComponentDefinition(Taskpage annotation) {
			return new AnnotatedTaskpage(annotation);
		}
	}
	
	private static class FrameConverter implements AnnotationConverter<Frame, FrameDefinition> {
		@Override
		public String getKey(FrameDefinition frameDefinition) {
			return frameDefinition.getId();
		}

		@Override
		public FrameDefinition getComponentDefinition(Frame annotation) {
			return new AnnotatedFrame(annotation);
		}
	}
	
	private static class SamePriorityException extends Exception {
		private static final long serialVersionUID = 1L;
		
		private Collection<Class<?>>  classes;
		
		public SamePriorityException(Collection<Class<?>> classes) {
			super();
			this.classes = classes;
		}

		public Collection<Class<?>> getClasses() {
			return classes;
		}
	}
	
	private static interface ComponentSelector {
		Class<?> select(Collection<Class<?>> classes) throws SamePriorityException;
	}
	
	private static class PrioritySelector implements ComponentSelector {

		private int getPrioridade(Class<?> classe) {
			Priority anotacao = classe.getAnnotation(Priority.class);
			return anotacao == null ? 0 : anotacao.value();			
		}
		
		@Override
		public Class<?> select(Collection<Class<?>> classes) throws SamePriorityException {
			int maiorPrioridade = 0;
			for(Class<?> classe : classes) {
				int prioridade = getPrioridade(classe);
				if(prioridade > maiorPrioridade) {
					maiorPrioridade = prioridade;
				}
			}
			
			Collection<Class<?>> classesMaiorPrioridade = new HashSet<>(); 
			for(Class<?> classe : classes) {
				int prioridade = getPrioridade(classe);
				if(prioridade == maiorPrioridade) {
					classesMaiorPrioridade.add(classe);
				}
			}
			
			if(classesMaiorPrioridade.size() > 1) {
				throw new SamePriorityException(classesMaiorPrioridade);
			}
			
			return classesMaiorPrioridade.iterator().next();
		}
		
	}
	
    @SuppressWarnings("unchecked")
	private <K extends Annotation> K getDeclaredAnnotation(Class<?> classe, Class<K> annotationType) {
    	for(Annotation annotation : classe.getDeclaredAnnotations()) {
    		if(annotation.annotationType().equals(annotationType)) {
    			return (K)annotation;
    		}
    	}
    	return null;
    }
    
	private <A extends Annotation, T extends ComponentDefinition> Map<String, T> getMapaAnotacoes(Class<A> classeAnotacao, AnnotationConverter<A, T> converter) {
        Reflections r = new Reflections("br.com.infox");
        
        Map<String, T> retorno = new HashMap<>();

        //Mapeia IDs que definem esse tipo de componente para classes que contenham a anotação do tipo com esse id
        Map<String, Collection<Class<?>>> mapaComponetes = new HashMap<>();
        
        Set<Class<?>> types = r.getTypesAnnotatedWith(classeAnotacao);
        for (Class<?> type : types) {
        	A anotacao = getDeclaredAnnotation(type, classeAnotacao);
        	if(anotacao == null) {
        		continue;
        	}
        	T componentDefinition = converter.getComponentDefinition(anotacao);
        	String key = converter.getKey(componentDefinition);
        	Collection<Class<?>> classesComponente = mapaComponetes.get(key);
        	if(classesComponente == null) {
        		classesComponente = new HashSet<>();
        		mapaComponetes.put(key, classesComponente);
        	}
        	classesComponente.add(type);
        }
        
        ComponentSelector selector = new PrioritySelector();
        
        for(String id : mapaComponetes.keySet()) {
        	Collection<Class<?>> classesComponente = mapaComponetes.get(id);
        	if(classesComponente != null) {
        		try {
        			Class<?> classeSelecionada = selector.select(classesComponente);
                	A anotacao = classeSelecionada.getAnnotation(classeAnotacao);
                	T componentDefinition = converter.getComponentDefinition(anotacao);
                	if(!componentDefinition.isDisabled()) {
                		retorno.put(id, componentDefinition);
                	}
        		}
        		catch(SamePriorityException e) {
        			throw new RuntimeException(String.format("Classes com mesma prioridade definidas para o componente %s com id '%s': %s", classeAnotacao.getSimpleName(), id, e.getClasses()));
        		}
        	}
        }
        
        return retorno;
	}
	
	@PostConstruct
	private void init() {
		framesMap = getMapaAnotacoes(Frame.class, new FrameConverter());
		taskPagesMap = getMapaAnotacoes(Taskpage.class, new TaskpageConverter());
	}
	
	public FrameDefinition getFrame(String id) {
		return framesMap.get(id);
	}
	
	public TaskpageDefinition getTaskPage(String id) {
		return taskPagesMap.get(id);
	}
	
	public Collection<TaskpageDefinition> getTaskpages() {
		return new ArrayList<>(taskPagesMap.values());
	}
	
	public Collection<FrameDefinition> getFrames() {
		return new ArrayList<>(framesMap.values());
	}
}
