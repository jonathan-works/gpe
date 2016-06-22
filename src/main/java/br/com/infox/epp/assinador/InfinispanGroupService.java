package br.com.infox.epp.assinador;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Alternative;
import javax.validation.ValidationException;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;

import br.com.infox.util.time.DateRange;

@Alternative
public class InfinispanGroupService implements AssinadorGroupService {
	private static final int MAX_ENTRIES = 1000;
	private static final int MINUTOS_CACHE = 30;
	private static final String CACHE_NAME = "Cache_Grupos_Assinador";
	
	private static final int TOKEN_LIFESPAN = 8;
	
	private static DefaultCacheManager cacheManager;
	
	public static class GrupoDocumentosAssinatura {
		private StatusToken status;
		private List<UUID> documentos;
		private Date dataCriacao;
		
		public GrupoDocumentosAssinatura(StatusToken staus, List<UUID> documentos) {
			super();
			this.status = staus;
			this.documentos = documentos;
			this.dataCriacao = new Date();
		}

		public Date getDataCriacao() {
			return dataCriacao;
		}

		public StatusToken getStatus() {
			return status;
		}

		public void setStatus(StatusToken status) {
			this.status = status;
		}

		public List<UUID> getDocumentos() {
			return documentos;
		}
	}

	private InfinispanGroupService() {
		if(cacheManager == null) {
			GlobalConfiguration globalConfiguration = new GlobalConfigurationBuilder().globalJmxStatistics()
					.allowDuplicateDomains(true).build();
			Configuration c = new ConfigurationBuilder().jmxStatistics().disable().eviction().strategy(EvictionStrategy.LRU)
					.maxEntries(MAX_ENTRIES).expiration().lifespan(MINUTOS_CACHE, TimeUnit.MINUTES).build();
			cacheManager = new DefaultCacheManager(globalConfiguration, c);
		}
		
	}
	
	private Cache<String, GrupoDocumentosAssinatura> getCacheGrupos() {
		return cacheManager.getCache(CACHE_NAME);
	}

	@Override
	public String createNewGroup(List<UUID> documentos) {
		String token = UUID.randomUUID().toString();
		GrupoDocumentosAssinatura dadosGrupo = new GrupoDocumentosAssinatura(StatusToken.AGUARDANDO_ASSINATURA, documentos);
		getCacheGrupos().put(token, dadosGrupo);
		return token;
	}
	
	public GrupoDocumentosAssinatura findByToken(String token) {
		GrupoDocumentosAssinatura group = getCacheGrupos().get(token);
		if (group == null) {
			throw new ValidationException("Token inválido");
		}
		return group;
	}
	
	@Override
	public void validarToken(String token) {
		GrupoDocumentosAssinatura group = findByToken(token);
		if (group == null) {
			throw new ValidationException("Token inválido");
		}

		StatusToken status = group.getStatus();

		// Status válido
		if (status == StatusToken.AGUARDANDO_ASSINATURA) {
			return;
		}

		switch (status) {
		case EXPIRADO :
			throw new ValidationException("Token expirado");
		case SUCESSO:
		case ERRO:
			throw new ValidationException("Token já processado");
		default:
			throw new ValidationException("Token com status desconhecido");
		}
	}

	@Override
	public boolean isTokenExpired(String token) {
		GrupoDocumentosAssinatura group = findByToken(token);
		return new DateRange(group.getDataCriacao(), new Date()).get(DateRange.MINUTES) > TOKEN_LIFESPAN;		
	}

	@Override
	public StatusToken getStatus(String token) {
		GrupoDocumentosAssinatura group = findByToken(token);
		return group.getStatus();
	}

	@Override
	public List<UUID> getDocumentos(String token) {
		GrupoDocumentosAssinatura group = findByToken(token);
		return group.getDocumentos();
	}

	@Override
	public void apagarGrupo(String token) {
		getCacheGrupos().remove(token);
	}

	private void setStatus(String token, StatusToken status) {
		GrupoDocumentosAssinatura group = findByToken(token);
		group.setStatus(status);
	}
	
	@Override
	public void cancelar(String token) {
		erroProcessamento(token, "Operação cancelada pelo assinador");
	}

	@Override
	public void erroProcessamento(String token, String mensagem) {
		setStatus(token, StatusToken.ERRO);
	}

	@Override
	public void processamentoFinalizado(String token) {
		setStatus(token, StatusToken.SUCESSO);
	}

	@Override
	public void atualizarAssinaturaTemporaria(String tokenGrupo, UUID uuid,
			DadosAssinaturaLegada dadosAssinaturaLegada) {
	}
}
