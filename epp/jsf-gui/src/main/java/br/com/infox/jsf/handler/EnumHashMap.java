package br.com.infox.jsf.handler;


import java.util.HashMap;

import javax.faces.FacesException;

/**
 * Custom {@link HashMap} which throws an {@link FacesException} if the key/constant does not exist.
 * 
 * @author Thomas Andraschko / last modified by $Author: $
 * @version $Revision: $
 * @since 0.7
 *
 * @param <TK> The key type.
 * @param <TV> The value type.
 */
@SuppressWarnings("serial")
public class EnumHashMap<TK, TV> extends HashMap<TK, TV> {

	private Class<?> clazz;

	/**
	 * Default constructor.
	 *
	 * @param clazz The class.
	 */
	public EnumHashMap(final Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TV get(final Object key) {
		if (!containsKey(key)) {
			throw new FacesException("Class " + clazz.getName() + " does not contain the enum constant " + key);
		}

		return super.get(key);
	}
}