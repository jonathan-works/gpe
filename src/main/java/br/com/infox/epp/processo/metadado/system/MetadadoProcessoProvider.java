package br.com.infox.epp.processo.metadado.system;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

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
		metadado.setProcesso(getProcesso());
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
	
	public MetadadoProcesso gerarMetadado(String nome, Object value) {
	    MetadadoProcesso metadadoProcesso = null;
	    MetadadoProcessoDefinition metadadoProcessoDefinition = getDefinicoesMetadados().get(nome);
	    if (metadadoProcessoDefinition != null) {
	        metadadoProcesso = gerarMetadado(metadadoProcessoDefinition);
	    } else {
	        metadadoProcesso = new MetadadoProcesso();
	        metadadoProcesso.setClassType(value.getClass());
	        metadadoProcesso.setProcesso(getProcesso());
	        metadadoProcesso.setMetadadoType(nome);
	        metadadoProcesso.setVisivel(false);
	    }
	    if (EntityUtil.isEntity(value)) {
	        metadadoProcesso.setValor(EntityUtil.getIdentifier(value).toString());
	    } else if (value.getClass().isAssignableFrom(Enum.class)) {
	        Enum<?> enums = (Enum<?>) value;
	        metadadoProcesso.setValor(enums.name());
	    } else if (value.getClass().isAssignableFrom(Date.class)) {
	        Date data = (Date) value;
	        metadadoProcesso.setValor(new DateTime(data.getTime()).toString(MetadadoProcesso.DATE_PATTERN));
	    } else if (value.getClass().isAssignableFrom(TipoProcesso.class)) {
	        TipoProcesso tipoProcesso = (TipoProcesso) value;
	        metadadoProcesso.setValor(tipoProcesso.value());
	    } else {
	        metadadoProcesso.setValor(value.toString());
	    }
	    return metadadoProcesso;
	}
	
	public Processo getProcesso() {
		return processo;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
}
