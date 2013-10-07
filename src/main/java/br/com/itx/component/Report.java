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
package br.com.itx.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.Component;

import br.com.itx.component.grid.GridColumn;
import br.com.itx.component.grid.GridQuery;

public class Report {

	private List<GridColumn> columns = new ArrayList<GridColumn>();

	private String reportId;
	
	private String grid;

	private String title;
	
	private String widths;

	private String orientation;
	
	private Template template;

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getTitle() {
		if (title == null || title.equals("")) {
			return reportId;
		}
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getOrientation() {
		if (orientation == null || orientation.equals("")) {
			orientation = "portrait";
		}
		return orientation;
	}
	
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	
	public String getWidths() {
		if (widths == null || widths.equals("")) {
			widths = "";
			for (int i = 0; i < columns.size(); i++) {
				if (i > 0) {
					widths += " ";
				}
				widths += "1";
			}
		}
		return widths;
	}
	
	public void setWidths(String widths) {
		this.widths = widths;
	}
	
	public Template getBodyTemplate() {
		if (template == null) {
			template = new Template();
		}
		return template;
	}
	
	public void setBodyTemplate(Template template) {
		this.template = template;
	}
	
	public String getViewId() {
		String viewId = "/" + reportId.substring(0, 1).toUpperCase()
				+ reportId.substring(1);
		return viewId;
	}
	
	public List<GridColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<GridColumn> columns) {
		for (GridColumn col : columns) {
			GridColumn reportCol = new GridColumn(col.getId(), col.getGridId(),
					                   col.getHeaderType(), col.getValueType(),
					                   col.getValueExpression(), col.getProperties());
			if (reportCol.getId().indexOf(".") > -1) {
				if (reportCol.getProperties() == null) {
					reportCol.setProperties(new HashMap<String, String>());
				}
				String p0 = reportCol.getId().split("\\.")[0];
				String p1 = reportCol.getId().split("\\.")[1];
				reportCol.setId(p0);
				reportCol.getProperties().put("field", p1);
			}
			reportCol.setGridId(reportId);
			this.columns.add(reportCol);
		}
	}

	public GridQuery getGrid() {
		if (grid == null) {
			grid = reportId + "Grid";
		}
		return (GridQuery) Component.getInstance(grid, true);
	}
	
	public void setGrid(String grid) {
		this.grid = grid;
	}
	
}