package br.com.infox.epp.ajuda.manager;

import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ajuda.dao.AjudaDAO;
import br.com.infox.epp.ajuda.entity.Ajuda;
import br.com.infox.epp.ajuda.entity.HistoricoAjuda;

@Name(AjudaManager.NAME)
@AutoCreate
public class AjudaManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "ajudaManager";

    @In
    private AjudaDAO ajudaDAO;

    public Ajuda getAjudaByPaginaUrl(String url) {
        return ajudaDAO.getAjudaByPaginaUrl(url);
    }
    
    @SuppressWarnings("rawtypes")
    public List pesquisar(String textoPesquisa) throws ParseException{
        return ajudaDAO.pesquisar(textoPesquisa);
    }
    
    public void gravarHistorico(Ajuda oldInstance) throws DAOException {
        HistoricoAjuda historico = new HistoricoAjuda();
        historico.setDataRegistro(oldInstance.getDataRegistro());
        historico.setPagina(oldInstance.getPagina());
        historico.setTexto(oldInstance.getTexto());
        historico.setUsuario(oldInstance.getUsuario());
        try {
            remove(oldInstance);
            persist(historico);
        } catch (DAOException e) {
            throw new DAOException("Não foi possível atualizar o histórico da ajuda", e);
        }
    }

}
