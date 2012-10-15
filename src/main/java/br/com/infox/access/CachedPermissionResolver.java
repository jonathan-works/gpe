package br.com.infox.access;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.permission.PermissionResolver;


/**
 * Classe abstrata para ser implementada de acordo com cada regra de valida��o
 * necess�ria.
 * 
 * @author luizruiz
 *
 */

//TODO Verificar como invalidar o cache

public abstract class CachedPermissionResolver<T> implements PermissionResolver {

	private static final String	UNCHECKED	= "unchecked";

	/**
	 * Verifica se a permiss�o � v�lida para o usu�rio.
	 * Primeiramente busca no contexto escolhido para cache,
	 * n�o encontrando, chama o m�todo com a l�gica espec�fica e 
	 * guarda o resultado no contexto de cache definido pelo m�todo
	 * getContext.
	 */
	@Override
	@SuppressWarnings(UNCHECKED)
	public boolean hasPermission(Object target, String action) {
		if (! checkType(target)) {
			return false;
		}
		if (getContext() == null) {
			return getPermission((T) target, action);
		}
		String hash = getHash(this.getClass(), target, action);
		Object permission = getContext().get(hash);
		if (permission != null) {
			return (Boolean) permission;
		}
		boolean b = getPermission((T) target, action);
		getContext().set(hash, b);
		return b;
	}
	
	private boolean checkType(Object target) {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType p = (ParameterizedType) type;
			if (p.getActualTypeArguments().length > 0) {
				Class<?> t = (Class<?>)p.getActualTypeArguments()[0];
				return t.isAssignableFrom(target.getClass());
			}
		}
		return false;
	}
	
	/**
	 * Filtra uma cole��o de objetos, removendo aqueles que a permiss�o � v�lida,
	 * deixando assim apenas aqueles que n�o tem permiss�o segundo o crit�rio 
	 * definido no m�todo getPermission.
	 * 
	 */
	@Override
	@SuppressWarnings({ "rawtypes", UNCHECKED })
	public void filterSetByAction(Set<Object> targets, String action) {
		Set remove = new HashSet();
		for (Object target : targets) {
			if (checkType(target) && getPermission((T) target, action)) {
				remove.add(target);
			}
		}
		targets.removeAll(remove);
	}
	
	/**
	 * Cria uma chave para guardar a permiss�o no contexto definido.
	 * 
	 * @param resolver � a classe usada para validar a permiss�o
	 * @param target � o alvo da permiss�o
	 * @param action � a ac�o a ser executada no alvo
	 * @return
	 */
	public static String getHash(Class<?> resolver, Object target, String action) {
		StringBuilder sb = new StringBuilder();
		sb.append(resolver.getName())
		  .append(":")
		  .append(target.getClass().getName())
		  .append(":")
		  .append(target.hashCode())
		  .append(":")
		  .append(action);
		return sb.toString();
	}

	/**
	 * Contexto usado para o cache
	 * @return valor default � o PageContext, retornando null n�o � feito cache
	 */
	protected Context getContext() {
		return Contexts.getPageContext();
	}
	
	/**
	 * Verifica a permiss�o de acordo com a regra de neg�cio
	 * @param target � o alvo da permiss�o
	 * @param action � a ac�o a ser executada no alvo
	 * @return
	 */
	protected abstract boolean getPermission(T target, String action);

	
}