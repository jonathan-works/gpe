package br.com.infox.epp.system.parametro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.Filter;

@Startup
@Singleton
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ParametroService extends PersistenceController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, SortedSet<ParametroDefinition<?>>> parametrosByGrupo;
    private Map<String, SortedSet<ParametroDefinition<?>>> parametrosByNome;

    @Inject
    private transient Instance<ParametroProvider> parametroProviders;

    @PostConstruct
    public void init() {
        parametrosByGrupo = new HashMap<>();
        parametrosByNome = new HashMap<>();
        for (ParametroProvider parametroProvider : parametroProviders) {
            for (ParametroDefinition<?> parametroDefinition : parametroProvider.getParametroDefinitions()) {
                addParametro(parametroDefinition);
            }
        }
    }

    public List<SelectItem> getItemsCriteria(ParametroDefinition<?> parametroDefinition) {
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> List<SelectItem> getItems(ParametroDefinition parametroDefinition) {
        SingularAttribute<?, ?> key = parametroDefinition.getKeyAttribute();
        SingularAttribute<?, ?> label = parametroDefinition.getLabelAttribute();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SelectItem> cq = cb.createQuery(SelectItem.class);

        Class<?> rootClass = key.getDeclaringType().getJavaType();
        Root<?> from = cq.from(rootClass);
        cq = cq.select(cb.construct(AnySelectItem.class, from.get(key.getName()), from.get(label.getName())));
        List<Filter<?, ?>> filters = parametroDefinition.getFilters();
        if (filters != null && !filters.isEmpty()) {
            Predicate predicate = cb.and();
            for (Filter<?, ?> entry : filters) {
                predicate = cb.and(predicate, entry.filter((Path) from, cb));
            }
            cq.where(predicate);
        }
        cq = cq.orderBy(cb.asc(from.get(label.getName())));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<String> listGrupos() {
        return new ArrayList<>(parametrosByGrupo.keySet());
    }

    private <K, V> SortedSet<V> get(K key, Map<K, SortedSet<V>> map) {
        if (!map.containsKey(key)) {
            map.put(key, new TreeSet<V>());
        }
        return map.get(key);
    }

    private void addParametro(ParametroDefinition<?> parametroDefinition) {
        String grupo = parametroDefinition.getGrupo();
        String nome = parametroDefinition.getNome();
        if (get(nome, parametrosByNome).contains(parametroDefinition) || get(grupo, parametrosByGrupo).contains(parametroDefinition)) {
            throw new IllegalStateException("Tentativa de declarar um parâmetro com nome e precedência já existente");
        }
        get(grupo, parametrosByGrupo).add(parametroDefinition);
        get(nome, parametrosByNome).add(parametroDefinition);
    }

    public List<ParametroDefinition<?>> getParametros(String grupo) {
        return new ArrayList<>(get(grupo, parametrosByGrupo));
    }

    static class AnySelectItem extends SelectItem {

        public AnySelectItem(Object value, Object label) {
            super(String.valueOf(value), String.valueOf(label));
        }

        private static final long serialVersionUID = 1L;

    }

}
