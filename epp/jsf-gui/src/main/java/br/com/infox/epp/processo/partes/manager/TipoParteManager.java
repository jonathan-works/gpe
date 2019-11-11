package br.com.infox.epp.processo.partes.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.partes.dao.TipoParteDAO;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@Stateless
@AutoCreate
@Name(TipoParteManager.NAME)
public class TipoParteManager extends Manager<TipoParteDAO, TipoParte> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoParteManager";
    
    public TipoParte getTipoParteByIdentificador(String identificador){
    	return getDao().getTipoParteByIdentificador(identificador);
    }
    
    public List<TipoParte> getTipoPartes() {
    	return getDao().findAll();
    }
   
}
