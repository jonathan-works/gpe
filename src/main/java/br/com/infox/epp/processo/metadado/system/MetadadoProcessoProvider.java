package br.com.infox.epp.processo.metadado.system;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;

public abstract class MetadadoProcessoProvider {
	private static final LogProvider LOG = Logging.getLogProvider(ComunicacaoMetadadoProvider.class);
	
	private Processo processo;

	public Map<String, MetadadoProcessoDefinition> getDefinicoesMetadados() {
		Map<String, MetadadoProcessoDefinition> metadados = new HashMap<>();
		for (Field field : getClass().getFields()) {
			if (field.getType().equals(MetadadoProcessoDefinition.class)) {
				try {
					MetadadoProcessoDefinition definition = (MetadadoProcessoDefinition) field.get(this);
					if (definition.isVisivel()) {
						metadados.put(definition.getMetadadoType(), definition);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					LOG.warn("Erro ao recuperar a definição do metadado " + field.getName(), e);
				}
			}
		}
		return metadados;
	}
	
	public MetadadoProcesso gerarMetadado(String metadadoType) {
		try {
			Field field = getClass().getField(metadadoType);
			MetadadoProcessoDefinition definition = (MetadadoProcessoDefinition) field.get(this);
			return gerarMetadado(definition);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalArgumentException("Metadado não encontrado: " + metadadoType, e);
		}
	}
	
	public MetadadoProcesso gerarMetadado(MetadadoProcessoDefinition definition) {
		MetadadoProcesso metadado = new MetadadoProcesso();
		metadado.setClassType(definition.getClassType());
		metadado.setVisivel(definition.isVisivel());
		metadado.setMetadadoType(definition.getMetadadoType());
		return metadado;
	}
	
	public MetadadoProcesso gerarMetadado(MetadadoProcessoDefinition definition, String valor) {
		return gerarMetadado(definition, this.processo, valor);
	}
	
	public MetadadoProcesso gerarMetadado(MetadadoProcessoDefinition definition, Processo processo, String valor) {
		MetadadoProcesso metadado = gerarMetadado(definition);
		metadado.setProcesso(processo);
		metadado.setValor(valor);
		return metadado;
	}
	
	public Processo getProcesso() {
		return processo;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
}
