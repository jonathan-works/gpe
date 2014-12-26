package br.com.infox.epp.processo.metadado.system;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;

public class MetadadoProcessoProvider {
	
	private static final LogProvider LOG = Logging.getLogProvider(MetadadoProcessoProvider.class);
	
	private Processo processo;
	
	public MetadadoProcessoProvider() {}
	
	public MetadadoProcessoProvider(Processo processo) {
		this.processo = processo;
	}

	public Map<String, MetadadoProcessoDefinition> getDefinicoesMetadados() {
		Map<String, MetadadoProcessoDefinition> metadados = new HashMap<>();
		for (Field field : getClass().getFields()) {
			if (field.getType().equals(MetadadoProcessoDefinition.class)) {
				try {
					MetadadoProcessoDefinition definition = (MetadadoProcessoDefinition) field.get(this);
					if (definition.getLabel() != null) {
						metadados.put(definition.getMetadadoType(), definition);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					LOG.warn("Erro ao recuperar a definição do metadado " + field.getName(), e);
				}
			}
		}
		return metadados;
	}
	
	public MetadadoProcesso gerarMetadado(MetadadoProcessoDefinition definition) {
		MetadadoProcesso metadado = new MetadadoProcesso();
		metadado.setVisivel(definition.getLabel() != null);
		metadado.setClassType(definition.getClassType());
		metadado.setMetadadoType(definition.getMetadadoType());
		return metadado;
	}
	
	public MetadadoProcesso gerarMetadado(MetadadoProcessoDefinition definition, String valor) {
		return gerarMetadado(definition, this.processo, valor);
	}
	
	public MetadadoProcesso gerarMetadadoVisivel(MetadadoProcessoDefinition definition, String valor) {
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
