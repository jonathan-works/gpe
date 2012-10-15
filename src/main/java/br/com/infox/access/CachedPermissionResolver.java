package br.com.infox.access;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.permission.PermissionResolver;


/**
 * Classe abstrata para ser implementada de acordo com cada regra de validação
 * necessária.
 * 
 * @author luizruiz
 *
 */

//TODO Verificar como invalidar o cache

public abstract class CachedPermissionResolver<T> implements PermissionResolver {

	private static final String	UNCHECKED	= "unchecked";

	/**
	 * Verifica se a permissão é válida para o usuário.
	 * Primeiramente busca no contexto escolhido para cache,
	 * não encontrando, chama o método com a lógica específica e 
	 * guarda o resultado no contexto de cache definido pelo método
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
	 * Filtra uma coleção de objetos, removendo aqueles que a permissão é válida,
	 * deixando assim apenas aqueles que não tem permissão segundo o critério 
	 * definido no método getPermission.
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
	 * Cria uma chave para guardar a permissão no contexto definido.
	 * 
	 * @param resolver é a classe usada para validar a permissão
	 * @param target é o alvo da permissão
	 * @param action é a acão a ser executada no alvo
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
	 * @return valor default é o PageContext, retornando null não é feito cache
	 */
	protected Context getContext() {
		return Contexts.getPageContext();
	}
	
	/**
	 * Verifica a permissão de acordo com a regra de negócio
	 * @param target é o alvo da permissão
	 * @param action é a acão a ser executada no alvo
	 * @return
	 */
	protected abstract boolean getPermission(T target, String action);

	
}