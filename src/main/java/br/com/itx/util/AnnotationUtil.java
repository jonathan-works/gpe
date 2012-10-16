/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/

package br.com.itx.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import javax.persistence.Id;

import org.hibernate.AnnotationException;

public final class AnnotationUtil {
	
	private AnnotationUtil() { }
	
	/**
	 * Verifica se o objeto passado possui a anota��o informada.
	 * @param obj Objeto que ser� verificado
	 * @param clazz Anota��o que pretende se verificar
	 * @return true se o objeto possui a anota��o, sen�o false.
	 */
	public static boolean isAnnotationPresent(Object obj, Class<? extends Annotation> clazz) {
		return EntityUtil.getEntityClass(obj).isAnnotationPresent(clazz);
	}	

	/**
	 * Retorna o nome do atributo que possui a anota��o informada.
	 * @param object Objeto em que ser� pesquisada o m�todo que possui a anota��o
	 * @param annotationClass @interface da anota��o a ser pesquisada.
	 * @return Nome do atributo
	 */
	public static String getAnnotationField(Object object, Class<? extends Annotation> 
											 annotationClass) {
		return getAnnotationField(object.getClass(), annotationClass);
	}
	
	/**
	 * Retorna o nome do atributo que possui a anota��o informada.
	 * @param classObj Classe em que ser� pesquisada o m�todo que possui a anota��o
	 * @param annotationClass @interface da anota��o a ser pesquisada.
	 * @return Nome do atributo
	 */
	public static String getAnnotationField(Class<? extends Object> classObj, Class<? extends Annotation> 
											 annotationClass) {
		for (Method m: classObj.getMethods()) { 
			if(!m.isAnnotationPresent(annotationClass)) {
				continue;
			}
			
			String fieldName = m.getName();
			fieldName = fieldName.startsWith("is") ? fieldName.substring(2) : fieldName.substring(3);
			return Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
		}
		
		for (Field f: classObj.getDeclaredFields()) {
			if (f.isAnnotationPresent(annotationClass)) {
				return f.getName();
			}
		}

		String msg = MessageFormat.format("Missing annotation @{0}", annotationClass.getSimpleName());
		throw new AnnotationException(msg);
	}	
	
	/**
	 * Retorna o valor do atributo que possui a anota��o informada.
	 * @param object Objeto em que ser� pesquisada o m�todo que possui a anota��o
	 * @param annotationClass anota��o a ser pesquisada nos m�todos do objeto
	 * @return Valor do atributo
	 */
	public static Object getValue(Object object, Class<? extends Annotation> 
											annotationClass) {
		String fieldName = getAnnotationField(object, annotationClass);
		return ComponentUtil.getValue(object, fieldName);
	}
	
	/**
	 * Retorna o valor do Id da entidade.
	 * @param object Objeto em que ser� pesquisada o m�todo que possui a anota��o
	 * @return Valor do Id
	 */
	public static Object getIdValue(Object object) {
		return getValue(object, Id.class);
	}
}
