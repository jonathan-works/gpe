package br.com.infox.epp.estatistica.manager;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.estatistica.bean.ProdutividadeBean;
import br.com.infox.epp.estatistica.dao.ProdutividadeDAO;

@AutoCreate
@Name(ProdutividadeManager.NAME)
@Scope(ScopeType.EVENT)
public class ProdutividadeManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "produtividadeManager";
	
	@In
	private ProdutividadeDAO produtividadeDAO;
	
	public List<ProdutividadeBean> listProdutividade(Map<String, Object> params) {
		return produtividadeDAO.listProdutividade(params);
	}
	
	public Long totalProdutividades(Map<String, Object> params) {
		return produtividadeDAO.totalProdutividades(params);
	}
}
