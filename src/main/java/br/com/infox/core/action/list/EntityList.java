package br.com.infox.core.action.list;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.el.PropertyNotFoundException;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.itx.util.EntityUtil;
import br.com.itx.util.ReflectionsUtil;

public abstract class EntityList<E> extends EntityQuery<E> implements Pageable {

	private static final String FIELD_EXPRESSION = "#'{'{0}List.entity.{1}}";

	private static final long serialVersionUID = 1L;
	
	private static final LogProvider LOG = Logging.getLogProvider(EntityList.class);

	protected static final int DEFAULT_MAX_RESULT = 20;

	private Map<String, SearchField> searchFieldMap = new HashMap<String, SearchField>();
	
	private Properties customColumnsOrder = new Properties();

	private Integer page = 1;

	private E entity;

	private String orderedColumn;
	
	public EntityList () {
		addSearchFields();
		Map<String, String> map = getCustomColumnsOrder();
		if (map != null) {
			customColumnsOrder.putAll(map);
		}
		setEjbql(getDefaultEjbql());
		setOrderedColumn(getDefaultOrder());
		setRestrictions();
	}
	
	/**
	 * Método para adicionar campos de pesquisa, utilizando um dos métodos:
	 * - {@link addSearchField(String fieldName, SearchCriteria criteria)}
	 * - {@link addSearchField(String fieldName, SearchCriteria criteria, 
						String expression)}
	 */
	protected abstract void addSearchFields();
	
	protected abstract String getDefaultEjbql();
	
	protected abstract String getDefaultOrder();

	protected abstract Map<String, String> getCustomColumnsOrder();

	protected Map<String, SearchField> getSearchFieldMap() {
		return searchFieldMap;
	}
	
	protected void addSearchField(String fieldName, SearchCriteria criteria) {
		addSearchField(new SearchField(getEntityListName(), fieldName, criteria));
	}

	protected void addSearchField(String fieldName, SearchCriteria criteria, 
						String expression) {
		addSearchField(new SearchField(getEntityListName(), fieldName, criteria, expression));
	}

	private void addSearchField(SearchField s) {
		searchFieldMap.put(s.getName(), s);
	}
	
	@Override
	public List<E> getResultList() {
		Integer pageCount = getPageCount() != null ? getPageCount() : 1;
		if (page > pageCount) {
			setPage(pageCount);
		}
		return super.getResultList();
	}

	public List<E> list() {
		setMaxResults(null);
		return getResultList();
	}

	public List<E> list(int maxResult) {
		setMaxResults(maxResult > 0 ? maxResult : DEFAULT_MAX_RESULT);
		return getResultList();
	}
	
	public Map<String, String> getSearchParameters() {
		final Map<String, String> map = new HashMap<String, String>();
		visitFields(new FieldCommand() {
			
			@Override
			public void execute(SearchField s, Object object) {
				map.put(s.getName(), EntityList.toString(object));
			}
		});
		return map;
	}

	public String getCriteria() {
		final StringBuilder sb = new StringBuilder();
		visitFields(new FieldCommandImpl(getEntityName(), sb));
		if (sb.length() != 0) {
		    sb.insert(0, ":\n");
	        sb.insert(0,(getRestrictionLogicOperator().equals("and") ?
	                "Todas as expressões" : "Qualquer expressão"));
		}
		sb.append("Classificado por: ");
		String column = getOrder();
		String[] s = column.split(" ");
		sb.append(Messages.instance().get(MessageFormat.format(
				"{0}.{1}", getEntityName(), s[0])));
		sb.append(" ");
		sb.append(s.length > 1 && s[1].equals("desc") ? "decrescente" : "crescente");
				
		return sb.toString();
	}

	/**
	 * Metodo que percorre todos os campos da entidade, chamando o método
	 * execute do command sempre que um campo não for nulo
	 * 
	 * @param command objeto que irá tratar os campos não nulos da
	 * 		  entidade de pesquisa
	 */
	protected void visitFields(FieldCommand command) {
		String entityName = getEntityName();
		for (SearchField s: searchFieldMap.values()) {
			String exp = MessageFormat.format(FIELD_EXPRESSION, entityName, s.getName());
			ValueExpression<Object> ve = Expressions.instance().createValueExpression(exp);
			Object o = null;
			try {
				o = ve.getValue();
			} catch (PropertyNotFoundException e) {
				// para o caso de uma restriction mapeada não seja um campo da entidade
			    LOG.error(".visitFields()", e);
			}
			if (o != null) {
				command.execute(s, o);
			}
		}
	}

	protected String getEntityName() {
		String entityName = getEntity().getClass().getSimpleName();
		return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
	}

	@Override
	public void setPage(Integer page) {
		this.page = page;
		int i = (page - 1) * (getMaxResults() != null ? getMaxResults() : 0);
		if (i < 0) {
			i = 0;
		}
		super.setFirstResult(i);
	}
	
	@Override
	public Integer getPage() {
		return page;
	}

	
	@Override
	protected String getCountEjbql() {
		setUseWildcardAsCountQuerySubject(true);
		return super.getCountEjbql();
	}
	
	/**
	 * Adicona restrictions do EntityQuery baseado na string com as expressões. 
	 * 
	 * @param restriction - String da restriction com ou sem expressões
	 */
	@SuppressWarnings("rawtypes")
	public final void setRestrictions() {
		List<ValueExpression> valueExpressionList = new ArrayList<ValueExpression>();
		for(SearchField s: searchFieldMap.values()) {
			valueExpressionList.add(Expressions.instance().createValueExpression(s.getExpression()));			
		}
		setRestrictions(valueExpressionList);
	}
	
	private static String toString(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof String) {
		    // apenas para encurtar o caminho
			return object.toString();
		} else if (object instanceof Date) {
			return MessageFormat.format("{0,date,yyyy-MM-dd HH:mm:ss:SSS}", object);
		} else if (EntityUtil.isEntity(object)) {
			return EntityUtil.getEntityIdObject(object).toString();
		} else {
			return object.toString();
		}

	}

	@SuppressWarnings("unchecked")
	public void newInstance() {
		try {
			setEntity((E) EntityUtil.newInstance(getClass()));
		} catch (Exception e) {
		    LOG.error(".newInstance()", e);
		}
	}
	
	public void setEntity(E entity) {
		this.entity = entity;
		Contexts.getConversationContext().set(getEntityComponentName(), entity);
	}
	
	/**
	 * Busca a entidade no contexto da conversação, se não encontrar cria
	 * por reflexão e armazena na conversação, para ser utilizado também
	 * na página do relatório, evitando a propagação por parâmetros
	 * 
	 * @return a entidade informado genericamente
	 */
	@SuppressWarnings("unchecked")
	public E getEntity() {
		if (entity == null) {
			entity = (E) Contexts.getConversationContext().get(getEntityComponentName());
			if (entity == null) {
				newInstance(); 
			}
		}
		return entity;
	}

	/**
	 * Cria o nome do componente da entidade que será armazenado na conversação
	 * 
	 * @return
	 */
	protected String getEntityComponentName() {
		return this.getClass().getName() + ".entity";
	}
	
	public boolean showColumn(String columnId) {
		SearchField s = searchFieldMap.get(columnId);
		if (s == null) {
			return false;
		}
		Object object = ReflectionsUtil.getValue(entity, s.getName());
		return !(object != null 
			&& s.getCriteria().equals(SearchCriteria.IGUAL)
			&& "and".equals(getRestrictionLogicOperator()));
	}

	public final void setOrderedColumn(String order) {
	    String newOrder = order;
		if(!newOrder.endsWith("asc") && !newOrder.endsWith("desc")) {
			newOrder = newOrder.trim().concat(" asc");
		}
		String[] fields = newOrder.split(" ");
		newOrder = customColumnsOrder.getProperty(fields[0], fields[0]);
		this.orderedColumn = fields[0];
		if (fields.length > 1) {
			newOrder = newOrder + " " + fields[1];
			this.orderedColumn = fields[0] + " " + fields[1];
		}
		setOrder(newOrder);
	}
	
	public String getOrderedColumn() {
		return orderedColumn;
	}
	
	public String getEntityListName() {
		return Component.getComponentName(this.getClass());
	}

	
	protected String getComponentPesquisa() {
		return this.getClass().getName() + ".pesquisa";
	}

	protected void setSearchFieldMap(Map<String, SearchField> searchFieldMap) {
		this.searchFieldMap = searchFieldMap;
	}
	
}