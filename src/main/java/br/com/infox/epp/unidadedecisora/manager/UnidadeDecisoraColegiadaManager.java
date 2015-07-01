package br.com.infox.epp.unidadedecisora.manager;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraColegiadaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;

@Stateless
@AutoCreate
@Name(UnidadeDecisoraColegiadaManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UnidadeDecisoraColegiadaManager extends Manager<UnidadeDecisoraColegiadaDAO, UnidadeDecisoraColegiada>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaManager";
	
	public List<UnidadeDecisoraColegiada> findUnidadeDecisoraListByIdUsuario(Integer idUsuario){
		return getDao().findUnidadeDecisoraColegiadaWithIdUsuario(idUsuario);
	}
	
	public List<Map<String, String>> getUnidadeDecisoraListByIdUsuario(Integer idUsuario){
		return getDao().searchUnidadeDecisoraColegiadaWithIdUsuario(idUsuario);
	}
	
	public boolean existeUnidadeColegiadaComLocalizacao(Integer idLocalizacao){
		return getDao().existeUnidadeDecisoraComLocalizacao(idLocalizacao);
	}
	
	public List<UnidadeDecisoraColegiada> findAllAtivo() {
        return getDao().findAllAtivo();
    }

	public UnidadeDecisoraColegiada findByCodigoLocalizacao(String codigoLocalizacao) {
		return getDao().findByCodigoLocalizacao(codigoLocalizacao);
	}
}
