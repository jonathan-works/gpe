package br.com.infox.annotations.manager;

import java.text.MessageFormat;
import java.util.List;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.persistence.Id;

import org.hibernate.AnnotationException;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.Recursive;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.exception.RecursiveException;
import br.com.itx.util.AnnotationUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

/**
 * Classe que gerencia a consistência dos valores no campo que possui o caminho
 * completo da recursividade na entidade.
 * @author Infox
 *
 */
public final class RecursiveManager {

	private RecursiveManager() { }
	
	public static final String MSG_PARENT_EXCEPTION = "Este registro já está nesta hierarquia";
	private static final LogProvider LOG = Logging.getLogProvider(RecursiveManager.class);
	
	/**
	 * Retorna uma string com o valor do campo com a anotação PathDescriptor
	 * concatenado por '/' de todos os elementos superiores na árvore do registro informado.
	 * @param object Registro que se deseja obter o fullPath
	 * @param sb informe um new StringBuilder()
	 * @param dadField Nome do campo da entidade que representa o pai do field 
	 * informado
	 * @param pathDescriptorField Nome do campo da entidade que representa o id do field 
	 * informado
	 * @return
	 */
	private static String getFullPath(Recursive<?> object, StringBuilder sb) {
		if(object != null) {
		    
			getFullPath((Recursive<?>)object.getParent(), sb);
			sb.append(object.getPathDescriptor());
			sb.append("|");
		}
		return sb.toString();
	}
	
	private static String getFullPath(Recursive<?> object) {
		return getFullPath(object, new StringBuilder());
	}

	public static void refactor(Recursive<?> object) {
		try {
			if(verifyParent(object)) {
				throw new RecursiveException(MSG_PARENT_EXCEPTION);
			}
			refactorFieldPath(object);
		} catch (AnnotationException e) {
		    LOG.error(".refactor()", e);
		}
	}
	
	/**
	 * Método que atualiza o fullPath do registro informado no argumento e 
	 * modifica todos os fullPaths de seus dependentes na árvore.
	 * @param object Registro que se deseja atualizar
	 */
	@SuppressWarnings(WarningConstants.UNCHECKED)
	private static void refactorFieldPath(Recursive<?> object) {
		try {
			setFullPath(object);
			
			List<Recursive<?>> fieldList = (List<Recursive<?>>) object.getChildList();
			if (fieldList != null) {
				for (Recursive<?> o : fieldList) {
					refactorFieldPath(o);
				}
			}
		} catch (InvalidTargetObjectTypeException ex) {
		    LOG.error(".refactorFieldPath()", ex);
		}
	}
	
	/**
	 * Obtem através das anotações os nomes dos campos que serão necessários 
	 * para efetuar as atualizações.
	 * @param object Registro que se deseja atualizar
	 * @throws InvalidTargetObjectTypeException 
	 * @throws AnnotationException 
	 */
	public static void setFullPath(Recursive<?> object) throws InvalidTargetObjectTypeException {
		object.setHierarchicalPath(getFullPath(object));
	}
	
	/**
	 * Verifica se o registro não está apontando para seu pai como ele mesmo.
	 * @return True se possuir ele mesmo na hierarquia
	 * @throws InvalidTargetObjectTypeException 
	 * @throws AnnotationException 
	 */
	private static boolean verifyParent(Recursive<?> object) {
		try {
			Integer id = (Integer) AnnotationUtil.getValue(object, Id.class);
			return hasParentDuplicity(object, id);
		} catch (AnnotationException | InvalidTargetObjectTypeException e) {
		    LOG.error(".verifyParent()", e);
		}
		return false;
	}
	
	private static boolean hasParentDuplicity(Recursive<?> o, Integer checkId) throws InvalidTargetObjectTypeException {
	    Recursive<?> dad = (Recursive<?>) o.getParent();
		if(dad != null) {
			Integer id = (Integer) AnnotationUtil.getValue(dad, Id.class);
			if(id.equals(checkId)) {
				return true;
			}
			
			return hasParentDuplicity(dad, checkId);
		}
		return false;
	}
	
	/**
	 * Método util para popular todos os registro da entidade no banco com seus 
	 * fullPaths.
	 * @param clazz Entidade que se deseja atualizar todos os registros.
	 */
	public static void populateAllHierarchicalPaths(Class<? extends Recursive<?>> clazz) {
		List<? extends Recursive<?>> entityList = getEntityListNullHierarchicalPath(clazz);
		for(Recursive<?> o : entityList){
			try {
				if (isFullPathEnpty(o)) {
					refactorFieldPath(o);
				}
			} catch (AnnotationException e) {
			    LOG.error(".populateAllHierarchicalPaths()", e);
			}
		}
	}
	
	
	public static boolean isFullPathEnpty(Recursive<?> object) {
		String currentFullPath = object.getHierarchicalPath();
		return currentFullPath == null || "".equals(currentFullPath);
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	private static <E> List<E> getEntityListNullHierarchicalPath(Class<E> clazz) {
		String annotationField = "hierarchicalPath";
		String template = "select o from {0} o where o.{1} is null or o.{1} = ''''";
		String sql = MessageFormat.format(template, clazz.getName(), annotationField);
		return EntityUtil.createQuery(sql).getResultList();
	}

	/**
	 * Método para inativar recursivamente todos os filhos do objeto passado
	 * @param obj raiz da sub-árvore que será inativada
	 */
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public static void inactiveRecursive(Recursive<?> obj) {
		ComponentUtil.setValue(obj, "ativo", Boolean.FALSE);
		
		List<Recursive<?>> childList = (List<Recursive<?>>) obj.getChildList();
		for (Recursive<?> child: childList) {
			inactiveRecursive(child);
		}
	}
}