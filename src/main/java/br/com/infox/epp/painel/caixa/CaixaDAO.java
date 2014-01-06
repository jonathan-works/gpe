package br.com.infox.epp.painel.caixa;

import static br.com.infox.epp.painel.caixa.CaixaQuery.PARAM_ID_CAIXA;
import static br.com.infox.epp.painel.caixa.CaixaQuery.REMOVE_BY_ID;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(CaixaDAO.NAME)
@AutoCreate
public class CaixaDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "caixaDAO";

    public void removeCaixaByIdCaixa(int idCaixa) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_CAIXA, idCaixa);
        executeNamedQueryUpdate(REMOVE_BY_ID, parameters);
    }
}
