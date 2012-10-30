package br.com.infox.performance;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.itx.component.MeasureTime;
import br.com.itx.component.Util;
import br.com.itx.util.EntityUtil;

public abstract class AbstractCache {

	private Map<String, Cache> map = new HashMap<String, Cache>();
	private static final LogProvider log = Logging.getLogProvider(AbstractCache.class);

	private Object getValue(String expressao, String identificador) {
		MeasureTime mt = new MeasureTime(true);
		String key = identificador;
		Cache cache = map.get(key);
		if (cache == null) {
			cache = new Cache();
			cache.identificador = identificador;
			cache.expressao = expressao;
			cache.resultado = new Util().eval(expressao);
			map.put(key, cache);
		}
		cache.count++;
		cache.tempo += mt.getTime();
		return cache.resultado;
	}

	/**
	 * Se a express�o com os par�metros informados est� cacheada, retorna o valor do cache, 
	 * sen�o, avalia a express�o, armazena o valor no cache e o retorna
	 * @param expressao express�o a ser avaliada
	 * @param objects par�metros da express�o
	 * @return resultado da express�o
	 */
	public Object getValue(String expressao, Object... objects) {
		StringBuilder sb = new StringBuilder(expressao);
		for (Object object : objects) {
			if (object != null) {
				sb.append('_');
				sb.append(getIdentificador(object));
			}
		}
		return getValue(expressao, sb.toString());
	}

	private Object getIdentificador(Object object) {
		if (EntityUtil.isEntity(object)) {
			return EntityUtil.getEntityIdObject(object);
		}
		return object.hashCode();
	}

	/**
	 * Se a express�o est� cacheada, retorna o valor do cache, sen�o, avalia 
	 * a express�o, armazena o valor no cache e o retorna
	 * @param expressao a ser avaliada
	 * @return resultado da express�o
	 */
	public Object getValue(String expressao) {
		return getValue(expressao, expressao);
	}

	/**
	 * Loga os dados estat�sticos sobre a execu��o do cache
	 */
	public void printEstatistica() {
		if (log.isInfoEnabled()) {
			log.info("----------------------------------");
			for (Cache cache : map.values()) {
				log.info(cache);
			}
			log.info("Quantidade de registros" + map.values().size());
			log.info("----------------------------------");
		}
	}

	private class Cache {
		String identificador;
		String expressao;
		Object resultado;
		int count = 0;
		long tempo = 0;

		@Override
		public String toString() {
			return identificador + " - " + count + " = " + resultado + " - " + tempo;
		}
	}

}