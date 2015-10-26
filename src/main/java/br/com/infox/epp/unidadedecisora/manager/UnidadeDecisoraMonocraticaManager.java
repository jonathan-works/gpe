package br.com.infox.epp.unidadedecisora.manager;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraMonocraticaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@AutoCreate
@Scope(ScopeType.EVENT)
@Stateless
@Name(UnidadeDecisoraMonocraticaManager.NAME)
public class UnidadeDecisoraMonocraticaManager extends Manager<UnidadeDecisoraMonocraticaDAO, UnidadeDecisoraMonocratica> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaManager";
	
	public List<UnidadeDecisoraMonocratica> getListUnidadeDecisoraMonocraticaWithIdColegiada(Integer idColegiada) {
		return getDao().searchUnidadeDecisoraMonocraticaAtivoWithIdColegiada(idColegiada);
	}
	
	public List<Map<String, String>> getUnidadeDecisoraListByUsuario(Integer idUsuario){
		return getDao().searchUnidadeDecisoraMonocraticaWithIdUsuario(idUsuario);
	}
	
	public UnidadeDecisoraMonocratica existeUnidadeMonocraticaComLocalizacao(Integer idLocalizacao){
		return getDao().existeUnidadeDecisoraComLocalizacao(idLocalizacao);
	}

    public UnidadeDecisoraMonocratica findByCodigoLocalizacao(String codigoLocalizacao) {
    	return getDao().findByCodigoLocalizacao(codigoLocalizacao);
    }
}
