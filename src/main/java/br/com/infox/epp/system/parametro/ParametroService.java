package br.com.infox.epp.system.parametro;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.SingularAttribute;

@Stateless
public class ParametroService implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, SortedSet<ParametroDefinition>> parametrosByGrupo;
	private Map<String, SortedSet<ParametroDefinition>> parametrosByNome;

	@Inject
	private EntityManager entityManager;
	@Any
	@Inject
	private Instance<ParametroProvider> parametroProviders;
	
	public List<SelectItem> getItems(SingularAttribute<?,?> key, SingularAttribute<?,?> label){
		String pattern="select new javax.faces.model.SelectItem( concat('''',o.{0}), concat('''',o.{1}) ) from {2} o order by o.{1}";
		Object[] arguments = {label.getName(), key.getName(), key.getDeclaringType().getJavaType().getName()};
		return entityManager.createQuery(MessageFormat.format(pattern, arguments), SelectItem.class).getResultList();
	}
	
	@PostConstruct
	public void init(){
		this.parametrosByGrupo = new HashMap<>();
		this.parametrosByNome = new HashMap<>();
		for (ParametroProvider parametroProvider : parametroProviders) {
			for(ParametroDefinition parametroDefinition : parametroProvider.getParametroDefinitions()){
				addParametro(parametroDefinition);
			}
		}
	}
	
	public List<String> listGrupos(){
		return new ArrayList<>(parametrosByGrupo.keySet());
	}
	
	private <K,V> SortedSet<V> get(K key, Map<K,SortedSet<V>> map){
		if (!map.containsKey(key)){
			map.put(key, new TreeSet<V>());
		}
		return map.get(key);
	}
	
	private void addParametro(ParametroDefinition parametroDefinition) {
		String grupo = parametroDefinition.getGrupo();
		String nome = parametroDefinition.getNome();
		if (get(nome, parametrosByNome).contains(parametroDefinition) || get(grupo, parametrosByGrupo).contains(parametroDefinition)) {
			throw new IllegalStateException("Tentativa de declarar um parâmetro com nome e precedência já existente");
		}
		get(grupo, parametrosByGrupo).add(parametroDefinition);
		get(nome, parametrosByNome).add(parametroDefinition);
	}

	public List<ParametroDefinition> getParametros(String grupo){
		return new ArrayList<>(get(grupo, parametrosByGrupo));
	}
	
}
