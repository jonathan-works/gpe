package br.com.infox.epp.estatistica.produtividade;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;

@AutoCreate
@Name(ProdutividadeManager.NAME)
@Scope(ScopeType.EVENT)
public class ProdutividadeManager extends Manager<ProdutividadeDAO, ProdutividadeBean> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "produtividadeManager";

    public List<ProdutividadeBean> listProdutividade(Map<String, Object> params) {
        return getDao().listProdutividade(params);
    }

    public Long totalProdutividades(Map<String, Object> params) {
        return getDao().totalProdutividades(params);
    }
}
