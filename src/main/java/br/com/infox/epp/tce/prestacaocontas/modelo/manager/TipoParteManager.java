package br.com.infox.epp.tce.prestacaocontas.modelo.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.tce.prestacaocontas.modelo.dao.TipoParteDAO;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.TipoParte;

@Name(TipoParteManager.NAME)
@AutoCreate
public class TipoParteManager extends Manager<TipoParteDAO, TipoParte> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoParteManager";
    
    public List<TipoParte> listTiposParteParaModeloPrestacaoContas() {
        return getDao().listTiposParteParaModeloPrestacaoContas();
    }
}
