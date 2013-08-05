package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.NatCatFluxoLocalizacaoDAO;
import br.com.infox.epp.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.entity.NaturezaCategoriaFluxo;
import br.com.infox.ibpm.entity.Localizacao;

/**
 * Classe com as regras de neg�cio para persistencia, 
 * vincula��o e altera��o dos dados na entidade
 * NatCatFluxoLocalizacao.
 * @author Daniel
 *
 */
@Name(NatCatFluxoLocalizacaoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class NatCatFluxoLocalizacaoManager extends GenericManager {

	private static final long serialVersionUID = -9025640498790727799L;

	public static final String NAME = "natCatFluxoLocalizacaoManager";

	@In
	public NatCatFluxoLocalizacaoDAO natCatFluxoLocalizacaoDAO;
	
	/**
	 * Persiste o registro e para cada localizacao filha a localizacao contida
	 * no parametro <code>natCatFluxoLocalizacao</code> tamb�m � inseirdo um 
	 * registro com o mesmo registro de naturezaCategoriaFluxo.
	 * @param natCatFluxoLocalizacao
	 */
	public void persistWithChildren(NatCatFluxoLocalizacao natCatFluxoLocalizacao) {
		persist(natCatFluxoLocalizacao);
		persistChildren(natCatFluxoLocalizacao.getLocalizacao(), natCatFluxoLocalizacao);
	}

	/**
	 * M�todo recursivo para inserir todas as localiza��es filhas.
	 * @param localizacao
	 * @param natCatFluxoLocalizacao
	 */
	private void persistChildren(Localizacao localizacao, 
								 NatCatFluxoLocalizacao natCatFluxoLocalizacao) {
		if(localizacao.getLocalizacaoList() != null && localizacao.getLocalizacaoList().size() > 0) {
			for(Localizacao l : localizacao.getLocalizacaoList()) {
				persistChildren(l, natCatFluxoLocalizacao);
				if(natCatFluxoLocalizacaoDAO.existsNatCatFluxoLocalizacao
						(natCatFluxoLocalizacao.getNaturezaCategoriaFluxo(), l)) {
					continue;
				}
				NatCatFluxoLocalizacao ncfl = new NatCatFluxoLocalizacao();
				ncfl.setLocalizacao(l);
				ncfl.setNaturezaCategoriaFluxo(natCatFluxoLocalizacao.getNaturezaCategoriaFluxo());
				ncfl.setHeranca(natCatFluxoLocalizacao.getHeranca());
				persist(ncfl);
			}
		}
	}
	
	/**
	 * Salva as altera��es feitas e replica quando necess�rio para 
	 * os filhos, tamb�m corrigi a �rvore hierarquica quando � modificado
	 * apenas a localiza��o ou a NaturezaCategoriaFluxo de um registro
	 * onde a heranca = true.
	 * @param natCatFluxoLocalizacao
	 */
	public void saveWithChidren(NatCatFluxoLocalizacao natCatFluxoLocalizacao, NatCatFluxoLocalizacao dataBaseOldObject) {
		/*
		 * Verifica se a heranca nunca foi ativada
		 */
		if(!dataBaseOldObject.getHeranca() && !natCatFluxoLocalizacao.getHeranca()) {
			update(natCatFluxoLocalizacao);
		/*
		 * Verifica se ativaram a heranca
		 */
		} else if(!dataBaseOldObject.getHeranca() && natCatFluxoLocalizacao.getHeranca()) {
			persistChildren(natCatFluxoLocalizacao.getLocalizacao(), natCatFluxoLocalizacao);
			update(natCatFluxoLocalizacao);
		/*
		 * Verifica se desativaram a heranca 
		 */
		} else if(dataBaseOldObject.getHeranca() && !natCatFluxoLocalizacao.getHeranca()) {
			removeChildren(natCatFluxoLocalizacao.getLocalizacao(), natCatFluxoLocalizacao);
			update(natCatFluxoLocalizacao);
		/*
		 * Verifica se a heranca era e continua ativa
		 */
		} else {
			/*
			 * Verifica se foi modificado o valor da localiza��o, se sim
			 * � necess�rio modificar toda a �rvore, do antigo para o novo.
			 */
			if(dataBaseOldObject.getLocalizacao().getIdLocalizacao() !=
			   natCatFluxoLocalizacao.getLocalizacao().getIdLocalizacao()){
				removeChildren(dataBaseOldObject.getLocalizacao(), dataBaseOldObject);
				update(natCatFluxoLocalizacao);
				persistChildren(natCatFluxoLocalizacao.getLocalizacao(), natCatFluxoLocalizacao);
			/*
			 * Verifica se foi modificada a rela��o com NaturezaCategoriaFluxo
			 * caso sim, ser� necess�rio atualizar toda a �rvore.
			 */
			} else if(dataBaseOldObject.getNaturezaCategoriaFluxo().getIdNaturezaCategoriaFluxo() !=
					  natCatFluxoLocalizacao.getNaturezaCategoriaFluxo().getIdNaturezaCategoriaFluxo()) {
				updateChildren(dataBaseOldObject.getLocalizacao(), 
							   dataBaseOldObject.getNaturezaCategoriaFluxo(), 
							   natCatFluxoLocalizacao.getNaturezaCategoriaFluxo());
			}
		}
	}
	
	/**
	 * Atualiza todas as NaturezaCategoriaFluxo contidas na heranca.
	 * @param localizacao para buscar a �rvore
	 * @param oldNCF naturezaCategoriaFluxo antiga
	 * @param newNCF naturezaCategoriaFluxo nova a ser atualizada.
	 */
	private void updateChildren(Localizacao localizacao, NaturezaCategoriaFluxo oldNCF, 
							   NaturezaCategoriaFluxo newNCF) {
		if(localizacao.getLocalizacaoList() != null && localizacao.getLocalizacaoList().size() > 0) {
			for(Localizacao l : localizacao.getLocalizacaoList()) {
				updateChildren(l, oldNCF, newNCF);
				NatCatFluxoLocalizacao needUpdate = natCatFluxoLocalizacaoDAO.
					getByNatCatFluxoAndLocalizacao(oldNCF, l);
				needUpdate.setNaturezaCategoriaFluxo(newNCF);
				update(needUpdate);
			}
		}
	}
	
	/**
	 * Remove todas as vincula��es das localiza��es filhas da 
	 * naturezaCategoriaFluxo vinculada.
	 * @param localizacao
	 * @param ncfl
	 */
	private void removeChildren(Localizacao localizacao, NatCatFluxoLocalizacao ncfl) {
		if(localizacao.getLocalizacaoList() != null && localizacao.getLocalizacaoList().size() > 0) {
			for(Localizacao l : localizacao.getLocalizacaoList()) {
				removeChildren(l, ncfl);
				natCatFluxoLocalizacaoDAO.deleteByNatCatFluxoAndLocalizacao
										  (ncfl.getNaturezaCategoriaFluxo(), l);
			}
		}
	}
	
	public List<NaturezaCategoriaFluxo> listByLocalizacaoAndPapel(Localizacao l, Papel p) {
		return natCatFluxoLocalizacaoDAO.listByLocalizacaoAndPapel(l, p);
	}
	
	public List<NatCatFluxoLocalizacao> listByNaturezaCategoriaFluxo(NaturezaCategoriaFluxo ncf) {
		return natCatFluxoLocalizacaoDAO.listByNaturezaCategoriaFluxo(ncf);
	}
	
}