package br.com.infox.ibpm.task.home;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.seam.contexts.Contexts;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

final class TaskVariableResolver extends TaskVariable {

	private static final LogProvider LOG = Logging.getLogProvider(TaskVariableResolver.class);

	private Object value;

	public TaskVariableResolver(VariableAccess variableAccess, TaskInstance taskInstance) {
		super(variableAccess, taskInstance);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void resolve() {
		if (value != null) {
			switch (type) {
			case MONETARY:
				if (value instanceof String) {
					try {
						value = NumberFormat.getNumberInstance().parse(value.toString()).doubleValue();
					} catch (ParseException e) {
						LOG.info("TaskVariableResolverRF001", e);
					}
				}
				atribuirValorDaVariavelNoContexto();
				break;
			case INTEGER:
				if (value instanceof String) {
					try {
						value = NumberFormat.getNumberInstance().parse(value.toString()).longValue();
					} catch (ParseException e) {
						LOG.info("TaskVariableResolverRF002", e);
					}
				}
				atribuirValorDaVariavelNoContexto();
				break;
			case DATE:
				if (value instanceof String) {
					try {
						value = new SimpleDateFormat("dd/MM/yyyy").parse(value.toString());
					} catch (ParseException e) {
						LOG.info("TaskVariableResolverRF003", e);
					}
				}
				atribuirValorDaVariavelNoContexto();
				break;
			case EDITOR:
				if (!(this.value instanceof Integer)) {
					this.value = getIdDocumento();
				}
				atribuirValorDaVariavelNoContexto();
				break;
			case TEXT:
				if (((String) value).length() > 4000) {
					throw new BusinessException("O tamanho do texto excede 4000 caracteres");
				}
				atribuirValorDaVariavelNoContexto();
				break;
			case FILE:
				if (!(this.value instanceof Integer)) {
					this.value = getIdDocumento();
				}
				atribuirValorDaVariavelNoContexto();
				break;
			default:
				atribuirValorDaVariavelNoContexto();
				break;
			}
		}
	}

	private Integer getIdDocumento() {
		Object variable = taskInstance.getVariable(variableAccess.getMappedName());
		if (variable != null) {
			return (Integer) variable;
		} else {
			return null;
		}
	}

	public void atribuirValorDaVariavelNoContexto() {
		Contexts.getBusinessProcessContext().set(variableAccess.getMappedName(), value);
	}

	private Object getValueFromMapaDeVariaveis(Map<String, Object> mapaDeVariaveis) {
		if (mapaDeVariaveis == null) {
			return null;
		}
		Set<Entry<String, Object>> entrySet = mapaDeVariaveis.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			if (entry.getKey().split("-")[0].equals(name) && entry.getValue() != null) {
				return entry.getValue();
			}
		}
		return null;
	}

	public void assignValueFromMapaDeVariaveis(Map<String, Object> mapaDeVariaveis) {
		value = getValueFromMapaDeVariaveis(mapaDeVariaveis);
	}

}
