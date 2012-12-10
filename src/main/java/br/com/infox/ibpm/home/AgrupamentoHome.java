package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.Agrupamento;
import br.com.infox.list.AgrupamentoList;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(AgrupamentoHome.NAME)
@BypassInterceptors
public class AgrupamentoHome extends AbstractHome<Agrupamento> {

	public static final String NAME = "agrupamentoHome";
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/CBO/CboTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Cbo.xls";

	@Override
	public List<Agrupamento> getBeanList() {
		return AgrupamentoList.instance().list();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	public static AgrupamentoHome instance() {
		return ComponentUtil.getComponent(AgrupamentoHome.NAME);
	}

	public void setAgrupamentoIdAgrupamento(Integer id) {
		setId(id);
	}

	public Integer getAgrupamentoIdAgrupamento() {
		return (Integer) getId();
	}	

}