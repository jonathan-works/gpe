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
	private static final LogProvider LOG = Logging.getLogProvider(AbstractCache.class);

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
	 * Se a expressão com os parâmetros informados está cacheada, retorna o valor do cache, 
	 * senão, avalia a expressão, armazena o valor no cache e o retorna
	 * @param expressao expressão a ser avaliada
	 * @param objects parâmetros da expressão
	 * @return resultado da expressão
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
	 * Se a expressão está cacheada, retorna o valor do cache, senão, avalia 
	 * a expressão, armazena o valor no cache e o retorna
	 * @param expressao a ser avaliada
	 * @return resultado da expressão
	 */
	public Object getValue(String expressao) {
		return getValue(expressao, expressao);
	}

	/**
	 * Loga os dados estatísticos sobre a execução do cache
	 */
	public void printEstatistica() {
		if (LOG.isInfoEnabled()) {
			LOG.info("----------------------------------");
			for (Cache cache : map.values()) {
				LOG.info(cache);
			}
			LOG.info("Quantidade de registros" + map.values().size());
			LOG.info("----------------------------------");
		}
	}

	private static class Cache {
		private String identificador;
		private String expressao;
		private Object resultado;
		private int count = 0;
		private long tempo = 0;

		@Override
		public String toString() {
			return identificador + " - " + count + " = " + resultado + " - " + tempo;
		}
	}

}