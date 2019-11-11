package br.com.infox.epp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.SingularAttribute;

import br.com.infox.core.type.Displayable;

public class DynamicField {

	private String id;
	private String label;
	private String path;
	private String tooltip;
	private Object previousValue;
	private Object value;
	private FieldType type;
	private SingularAttribute<?, String> keyAttribute;
	private SingularAttribute<?, String> labelAttribute;
	private final Map<String, Object> options;
	private final Set<DynamicFieldAction> actions;
	private Enum<? extends Displayable>[] enumValues;
	
	public DynamicField() {
		this.options = new HashMap<>();
		this.previousValue = null;
		this.actions = new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public FieldType getType() {
		return type;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if (this.previousValue == null) {
			this.previousValue = this.value;
		}
		this.value = value;
	}

	public SingularAttribute<?, String> getKeyAttribute() {
		return keyAttribute;
	}

	public void setKeyAttribute(SingularAttribute<?, String> keyAttribute) {
		this.keyAttribute = keyAttribute;
	}

	public SingularAttribute<?, String> getLabelAttribute() {
		return labelAttribute;
	}

	public void setLabelAttribute(SingularAttribute<?, String> labelAttribute) {
		this.labelAttribute = labelAttribute;
	}

	public void addAction(DynamicFieldAction action){
	    actions.add(action);
	}
	
	public Set<DynamicFieldAction> getActions() {
            return actions;
        }

        public void set(String key, Object value) {
		this.options.put(key, value);
	}

	public Object get(String key) {
		return this.options.get(key);
	}

	@Override
	public String toString() {
		return "DynamicField [id=" + id + ", value=" + value + ", type=" + type + "]";
	}
	
	public Enum<? extends Displayable>[] getEnumValues() {
		return enumValues;
	}
	
	public void setEnumValues(Enum<? extends Displayable>[] enumValues) {
		this.enumValues = enumValues;
	}
}
