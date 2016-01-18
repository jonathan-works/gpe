package br.com.infox.epp.system.parametro;

import java.beans.Introspector;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.DynamicField;
import br.com.infox.epp.FieldType;
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
			grupos.add(new SelectItem(grupo, grupo.toUpperCase()));
			if (getGrupo() == null){
				setGrupo(grupo);
			}
		}
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
		refreshFormFields(parametroService.getParametros(grupo));
	}

	public DynamicField createFormField(ParametroDefinition definicaoParametro) {
		Class<?> type = definicaoParametro.getTipo();
		Parametro parametro = parametroManager.getParametro(definicaoParametro.getNome());
		if (parametro == null){
			return null;
		}
		DynamicField ff = new DynamicField();
		ff.setId(parametro.getNomeVariavel());
		ff.setLabel(parametro.getDescricaoVariavel());
		ff.setType(FieldType.getByClass(type));
		ff.setTooltip(parametro.getDescricaoVariavel());
		ff.setPath(MessageFormat.format("{0}.{1}", Introspector.decapitalize(ParametroView.class.getSimpleName()), "formFields"));
		ff.setValue(parametro.getValorVariavel());
		if (definicaoParametro.getKeyAttribute() != null && definicaoParametro.getLabelAttribute() != null){
			List<SelectItem> items = parametroService.getItems(definicaoParametro.getKeyAttribute(), definicaoParametro.getLabelAttribute());
			ff.set("items", items);
		}
		return ff;
	}
	
	private void refreshFormFields(Collection<ParametroDefinition> parametros) {
		formFields.clear();
		for (ParametroDefinition parametro : parametros) {
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
			if (parametro != null) {
				parametro.setValorVariavel(formField.getValue());
				parametroManager.update(parametro);
			}
		}
		return "";
	}	
}
