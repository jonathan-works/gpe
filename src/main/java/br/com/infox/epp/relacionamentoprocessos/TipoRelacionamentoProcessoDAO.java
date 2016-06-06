package br.com.infox.epp.relacionamentoprocessos;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;

@Stateless
@AutoCreate
@Name(TipoRelacionamentoProcessoDAO.NAME)
public class TipoRelacionamentoProcessoDAO extends DAO<TipoRelacionamentoProcesso> {
    
    public static final String NAME = "tipoRelacionamentoProcessoDAO";
    private static final long serialVersionUID = 1L;

}
