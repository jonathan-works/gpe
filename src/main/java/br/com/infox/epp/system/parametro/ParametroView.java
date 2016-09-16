package br.com.infox.epp.system.parametro;

import java.beans.Introspector;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.type.Displayable;
import br.com.infox.epp.DynamicField;
import br.com.infox.epp.DynamicFieldAction;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;

@Named
@ViewScoped
public class ParametroView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ParametroManager parametroManager;
	@Inject
	private ParametroService parametroService;
	@Inject
	private InfoxMessages infoxMessages;
	private String grupo;
	private Map<String,DynamicField> formFields;

	private List<SelectItem> grupos;

	public Map<String, DynamicField> getFormFields() {
		return formFields;
	}

	@PostConstruct
	public void init() {
		this.formFields = new HashMap<>();
		grupos = new ArrayList<>();
		for (String grupo : parametroService.listGrupos()) {
			grupos.add(new SelectItem(grupo, infoxMessages.get(String.format("parametro.grupo.%s", grupo))));
			if (getGrupo() == null){
				setGrupo(grupo);
			}
		}
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
		refreshFormFields(parametroService.getParametros(grupo));
	}
	
	@SuppressWarnings("unchecked")
	public DynamicField createFormField(ParametroDefinition<?> definicaoParametro) {
	    if (definicaoParametro.isSistema())
	        return null;
	    
		Parametro parametro = parametroManager.getParametro(definicaoParametro.getNome());
		DynamicField ff = new DynamicField();
		ff.setId(definicaoParametro.getNome());
		ff.setType(definicaoParametro.getTipo());
                ff.set("readonly", definicaoParametro);
                ff.setLabel(infoxMessages.get(String.format("parametro.%s.label", definicaoParametro.getNome())));
                ff.setTooltip(infoxMessages.get(String.format("parametro.%s.descricao", definicaoParametro.getNome())));
                ff.setPath(MessageFormat.format("{0}.{1}", Introspector.decapitalize(ParametroView.class.getSimpleName()), "formFields"));
                if (definicaoParametro.getKeyAttribute() != null && definicaoParametro.getLabelAttribute() != null){
                    List<SelectItem> items = parametroService.getItems(definicaoParametro);
                    ff.set("items", items);
                }
                ff.setEnumValues(definicaoParametro.getEnumValues());
                for (Entry<String, String> action : definicaoParametro.getActions()) {
                    String label = infoxMessages.get(String.format("parametro.%s.actions.%s", definicaoParametro.getNome(), action.getKey()));
                    String actionListener = action.getValue();
                    ff.addAction(new DynamicFieldAction(null, label, actionListener));
                }
                
                if (parametro == null) {
                	ff.setValue(null);
                } else if (definicaoParametro.getEnumValues() == null) {
                	ff.setValue(parametro.getValorVariavel());
                } else {
                	Enum<? extends Displayable> enumValue = definicaoParametro.getEnumValues()[0];
                	String[] split = parametro.getValorVariavel().split(",");
                	Enum<? extends Displayable>[] valores = new Enum[split.length];
                	for (int i = 0; i < split.length; i++) {
                		valores[i] = Enum.valueOf(enumValue.getDeclaringClass(), split[i]);
                	}
                	ff.setValue(valores);
                }
		ff.set("actions", definicaoParametro.getActions());
		return ff;
	}
	
	private void refreshFormFields(Collection<ParametroDefinition<?>> parametros) {
		formFields.clear();
		for (ParametroDefinition<?> parametro : parametros) {
			DynamicField ff = createFormField(parametro);
			if (ff != null){
				formFields.put(ff.getId(), ff);
			}
		}
	}

	public void setGrupos(List<SelectItem> grupos) {
		this.grupos = grupos;
	}
	public String getGrupo() {
		return this.grupo;
	}
	public List<SelectItem> getGrupos() {
		return grupos;
	}
	
	public String publish() {
		for (Entry<String, DynamicField> entry : formFields.entrySet()) {
			Parametro parametro = parametroManager.getParametro(entry.getKey());
			DynamicField formField = entry.getValue();
			if (parametro == null){
				parametro = new Parametro();
				
				parametro.setNomeVariavel(formField.getId());
				parametro.setDescricaoVariavel(String.format("parametro.%s.descricao", formField.getId()));
				parametro.setUsuarioModificacao(Authenticator.getUsuarioLogado());
				parametro.setDataAtualizacao(new Date());
				parametro.setSistema(Boolean.FALSE);
				parametro.setAtivo(Boolean.TRUE);
			}
			if (formField.getValue() == null) {
				parametro.setValorVariavel(null);
			} else if (formField.getEnumValues() == null) {
				parametro.setValorVariavel(formField.getValue().toString());
			} else {
				parametro.setValorVariavel(StringUtils.join((Object[]) formField.getValue(), ','));
			}
			parametroManager.update(parametro);
			Contexts.getApplicationContext().set(parametro.getNomeVariavel().trim(), parametro.getValorVariavel());
		}
		return "";
	}	
}
