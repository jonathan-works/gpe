package br.com.infox.certificado.service;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;

public class CertificadoDigitalMapSingleton {

	private static final int MAX_ENTRIES = 1000;
	private static final int MINUTOS_CACHE = 1;
	private DefaultCacheManager cacheManager;

	private CertificadoDigitalMapSingleton() {
		GlobalConfiguration globalConfiguration = new GlobalConfigurationBuilder().globalJmxStatistics()
				.allowDuplicateDomains(true).build();
		Configuration c = new ConfigurationBuilder().jmxStatistics().disable().eviction().strategy(EvictionStrategy.LRU)
				.maxEntries(MAX_ENTRIES).expiration().lifespan(MINUTOS_CACHE, TimeUnit.MINUTES).build();
		cacheManager = new DefaultCacheManager(globalConfiguration, c);
	}

	private static final class LazyLoader {
		public static final CertificadoDigitalMapSingleton INSTANCE = new CertificadoDigitalMapSingleton();
	}

	public static final <K, V> Cache<K, V> getCache(String name) {
		return LazyLoader.INSTANCE.cacheManager.getCache(name);
	}

}
