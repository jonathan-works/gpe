/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.component.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.itx.component.query.EntityQuery;

@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class GridQuery<T> extends EntityQuery<T> {

	private static final long serialVersionUID = 1L;
	
	private static final LogProvider LOG = Logging.getLogProvider(GridQuery.class);

	private List<GridColumn> columns = new ArrayList<GridColumn>();

	private List<GridColumn> visibleColumns = new ArrayList<GridColumn>();

	private List<SearchField> searchFields = new ArrayList<SearchField>();

	private Map<String, SearchField> searchFieldsMap = new HashMap<String, SearchField>();

	private Map<String, GridColumn> columnsMap = new HashMap<String, GridColumn>();

	private String gridId;
	
	private String viewId;

	private String key;

	private Integer page = 1;
	
	private Object entity;

	private String home;

	private String visibleColumnList = "";
	
	private boolean treeMode;
	
	private List<Object> selectedRowsList = new ArrayList<Object>(0);
	private Object selectedRow;

	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
		for (GridColumn col : columns) {
			col.setGridId(gridId);
			col.setTreeMode(treeMode);
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getViewId() {
		if (viewId == null) {
			String pagesDir = 
				(String) org.jboss.seam.Component.getInstance("pagesDir");
			if (pagesDir == null) {
				pagesDir = "/";
			}
			viewId = pagesDir + gridId.substring(0, 1).toUpperCase()
					+ gridId.substring(1);
		}
		return viewId;
	}
	
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public List<GridColumn> getColumns() {
		return columns;
	}

	private GridColumn getColumn(String id) {
		GridColumn column = columnsMap.get(id.trim());
		if (column == null) {
			throw new IllegalArgumentException("Coluna " + id + " não definida no grid " + this.gridId);
		}
		return column;
	}
	
	public void setColumns(List<GridColumn> columns) {
		this.columns = columns;
		columnsMap = new HashMap<String, GridColumn>();
		for (GridColumn col : columns) {
			col.setGridId(gridId);
			col.setTreeMode(treeMode);
			this.columnsMap.put(col.getId(), col);
		}
	}
	
	public List<GridColumn> getVisibleColumns() {
		return visibleColumns;
	}
	
	public String getVisibleColumnList() {
		return visibleColumnList;
	}
	
	public void setVisibleColumnList(String visibleColumnList) {
		getVisibleColumnList(visibleColumnList);
		this.visibleColumnList = visibleColumnList;
	}
	
	public List<GridColumn> getVisibleColumnList(String idList) {
		if (idList.equals("") && visibleColumns.isEmpty()) {
			visibleColumns = new ArrayList<GridColumn>(columns);
			setNewVisibleColumnList();
			return columns;
		}
		if (idList.equals(visibleColumnList)) {
			return visibleColumns;
		}
		visibleColumnList = idList; 
		visibleColumns.clear();
		for (String id : visibleColumnList.split(",")) {
			GridColumn column = getColumn(id);
			visibleColumns.add(column);
		}
		return visibleColumns;
	}
	
	public List<GridColumn> getAllColumns() {
		List<GridColumn> list = new ArrayList<GridColumn>();
		list.addAll(visibleColumns);
		List<GridColumn> others = new ArrayList<GridColumn>(columns);
		Collections.sort(others, new Comparator<GridColumn>() {
			public int compare(GridColumn col1, GridColumn col2) {
				String s1 = Messages.instance().get(col1.toString());
				String s2 = Messages.instance().get(col2.toString());
				return s1.compareTo(s2);
			}
		});
		others.removeAll(visibleColumns);
		list.addAll(others);
		return list;
	}
	
	public void toggleVisibleColumn(String id) {
		GridColumn column = getColumn(id);
		if (visibleColumns.contains(column)) {
			visibleColumns.remove(column);
		} else {
			visibleColumns.add(column);
		}
		setNewVisibleColumnList();
	}

	public void moveColumnUp(GridColumn col) {
		move(col, -1);
	}

	public void moveColumnDown(GridColumn col) {
		move(col, 1);
	}
	
	private void move(GridColumn col, int dir) {
		int i = visibleColumns.indexOf(col);
		if (i < 0 ) {
			return;
		}
		visibleColumns.remove(col);
		visibleColumns.add(i + dir, col);
		setNewVisibleColumnList();
	}
	
	private void setNewVisibleColumnList() {
		StringBuilder sb = new StringBuilder();
		for (GridColumn col : visibleColumns) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(col.getId());
		}
		visibleColumnList = sb.toString();
	}
	
	public List<SearchField> getSearchFields() {
		return searchFields;
	}

	
	public void setSearchFields(List<SearchField> fields) {
		this.searchFields = fields;
		searchFieldsMap.clear();
		for (SearchField field : fields) {
			searchFieldsMap.put(field.getId(), field);
			field.setGrid(this);
		}
	}

	public Map<?, ?> getFields() {
		Context ctx = Contexts.getEventContext();
		if (ctx.get("instance") == null) {
			ctx.set("instance", new HashMap<Object, Object>());
		}
		return searchFieldsMap;
	}
	
	public void setPage(Integer page) {
		this.page = page;
		int i = (page - 1) * getMaxResults();
		if (i < 0) {
			i = 0;
		}
		super.setFirstResult(i);
	}
	
	public Integer getPage() {
		return page;
	}

	private String getHomeName() {
		if (home == null) {
			home = gridId + "Home";
		}
		return home;
	}
	
	@SuppressWarnings("unchecked")
	public EntityHome<T> getHome() {
		return (EntityHome<T>) Component.getInstance(getHomeName(), true);
	}
	
	public void setHome(String home) {
		this.home = home;
	}

	public Object getEntity() {
		if (entity == null && getHome() != null) {
			try {
				entity = getHome().getEntityClass().newInstance();
			} catch (Exception e) {
			    LOG.error(".getEntity()", e);
			}
		}
		return entity;
	}
	
	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public void setTreeMode(boolean treeMode) {
		this.treeMode = treeMode;
		if (columns != null) {
			for (GridColumn col : columns) {
				col.setTreeMode(treeMode);
			}		
		}
	}

	public boolean getTreeMode() {
		return treeMode;
	}

	public List<Object> getSelectedRowsList() {
		return selectedRowsList;
	}
	
	public void addRemoveRowList(Object row) {
		if (selectedRowsList.contains(row)) {
			selectedRowsList.remove(row);
		} else {
			selectedRowsList.add(row);
		}
	}	
	
	public Object getSelectedRow() {
		return selectedRow;
	}
	
	public void setSelectedRow(Object selectedRow) {
		this.selectedRow = selectedRow;
	}
	
}