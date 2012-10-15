package br.com.infox.annotations.manager;

import java.text.MessageFormat;
import java.util.List;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.persistence.Id;

import org.hibernate.AnnotationException;

import br.com.infox.annotations.ChildList;
import br.com.infox.annotations.HierarchicalPath;
import br.com.infox.annotations.Parent;
import br.com.infox.annotations.PathDescriptor;
import br.com.infox.annotations.Recursive;
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
	private static String getFullPath(Object object, String dadField,
										String pathDescriptorField, StringBuilder sb) {
		if(object != null) {
			getFullPath(ComponentUtil.getValue(object, dadField),
						dadField, pathDescriptorField, sb);
			sb.append(ComponentUtil.getValue(object, pathDescriptorField));
			sb.append("|");
		}
		return sb.toString();
	}
	
	private static String getFullPath(Object object, String dadField, String pathDescriptorField) {
		return getFullPath(object, dadField, pathDescriptorField, new StringBuilder());
	}

	public static void refactor(Object object) throws RecursiveException {
		try {
			if(!isRecursive(object)) {
				throw new AnnotationException("Missing annotation @Recursive: " +
											  object.getClass().getName());
			} else if(verifyParent(object)) {
				throw new RecursiveException(MSG_PARENT_EXCEPTION);
			}
			refactorFieldPath(object);
		} catch (AnnotationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método que atualiza o fullPath do registro informado no argumento e 
	 * modifica todos os fullPaths de seus dependentes na árvore.
	 * @param object Registro que se deseja atualizar
	 */
	private static void refactorFieldPath(Object object) {
		try {
			setFullPath(object);
			
			List<Object> fieldList = (List<Object>) AnnotationUtil.getValue(object, ChildList.class);
			if (fieldList != null) {
				for (Object o : fieldList) {
					refactorFieldPath(o);
				}
			}
		} catch (InvalidTargetObjectTypeException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Obtem através das anotações os nomes dos campos que serão necessários 
	 * para efetuar as atualizações.
	 * @param object Registro que se deseja atualizar
	 * @throws InvalidTargetObjectTypeException 
	 * @throws AnnotationException 
	 */
	public static void setFullPath(Object object) throws AnnotationException, 
												InvalidTargetObjectTypeException {
		String dad = AnnotationUtil.getAnnotationField(object, Parent.class);
		String pathDescriptor = AnnotationUtil.getAnnotationField(object, PathDescriptor.class);
		String hierarchicalPath = AnnotationUtil.getAnnotationField(object, HierarchicalPath.class);
		ComponentUtil.setValue(object, hierarchicalPath, getFullPath(object, dad, pathDescriptor));
	}
	
	/**
	 * Verifica se a classe do argumento informado possui a anotação de 
	 * recursividade.
	 * @param object Registro que se deseja saber se está mapeado como recursive.
	 * @return True se possuir
	 */
	public static boolean isRecursive(Object object) {
		return object.getClass().isAnnotationPresent(Recursive.class);
	}	
	
	/**
	 * Verifica se o registro não está apontando para seu pai como ele mesmo.
	 * @return True se possuir ele mesmo na hierarquia
	 * @throws InvalidTargetObjectTypeException 
	 * @throws AnnotationException 
	 */
	private static boolean verifyParent(Object object) {
		try {
			Integer id = (Integer) AnnotationUtil.getValue(object, Id.class);
			return hasParentDuplicity(object, id);
		} catch (AnnotationException e) {
			e.printStackTrace();
		} catch (InvalidTargetObjectTypeException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static boolean hasParentDuplicity(Object o, Integer checkId) throws 
						AnnotationException, InvalidTargetObjectTypeException {
		Object dad = AnnotationUtil.getValue(o, Parent.class);
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
	public static void populateAllHierarchicalPaths(Class<? extends Object> clazz) {
		List<? extends Object> entityList = getEntityListNullHierarchicalPath(clazz);
		for(Object o : entityList){
			try {
				if (isFullPathEnpty(o)) {
					refactorFieldPath(o);
				}
			} catch (AnnotationException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static boolean isFullPathEnpty(Object object) {
		String currentFullPath = (String) AnnotationUtil.getValue(object, HierarchicalPath.class);
		return currentFullPath == null || "".equals(currentFullPath);
	}
	
	private static <E> List<E> getEntityListNullHierarchicalPath(Class<E> clazz) {
		String annotationField = getFieldHierarchicalPath(clazz);
		String template = "select o from {0} o where o.{1} is null or o.{1} = ''''";
		String sql = MessageFormat.format(template, clazz.getName(), annotationField);
		return EntityUtil.createQuery(sql).getResultList();
	}

	private static <E> String getFieldHierarchicalPath(Class<E> clazz) {
		return AnnotationUtil.getAnnotationField(clazz, HierarchicalPath.class);
	}
	
	/**
	 * Método para inativar recursivamente todos os filhos do objeto passado
	 * @param obj raiz da sub-árvore que será inativada
	 */
	public static void inactiveRecursive(Object obj) {
		if (isRecursive(obj)) {
			ComponentUtil.setValue(obj, "ativo", Boolean.FALSE);
			
			List<Object> childList = (List<Object>) AnnotationUtil.getValue(obj, ChildList.class);
			for (Object child: childList) {
				inactiveRecursive(child);
			}
		}
	}
}