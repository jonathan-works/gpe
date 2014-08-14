package br.com.infox.epp.tce.prestacaocontas.modelo.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.TipoParte;
import br.com.infox.epp.tce.prestacaocontas.modelo.query.TipoParteQuery;

@Name(TipoParteDAO.NAME)
@AutoCreate
public class TipoParteDAO extends DAO<TipoParte> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoParteDAO";
    
    public List<TipoParte> listTiposParteParaModeloPrestacaoContas() {
        return getNamedResultList(TipoParteQuery.LIST_TIPOS_PARTE_PARA_MODELO_PRESTACAO_CONTAS);
    }
}
