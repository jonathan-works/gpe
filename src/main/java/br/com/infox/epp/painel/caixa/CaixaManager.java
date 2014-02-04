package br.com.infox.epp.painel.caixa;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.entity.Processo;

@Name(CaixaManager.NAME)
@AutoCreate
public class CaixaManager extends Manager<CaixaDAO, Caixa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "caixaManager";
	
	@In private ProcessoDAO processoDAO;
	
	public void removeCaixaByIdCaixa(int idCaixa){
		getDao().removeCaixaByIdCaixa(idCaixa);
	}
	
	/**
	 * Adiciona o processo em uma caixa
	 * @param caixaList - Lista da caixas nas quais o processo pode ser inserido
	 * @param processo - Processo em Movimentação
	 */
	public void moverProcessoParaCaixa(List<Caixa> caixaList, Processo processo){
		Caixa caixa = escolherCaixa(caixaList);
		processoDAO.moverProcessoParaCaixa(caixa, processo);
	}
	
	private Caixa escolherCaixa(List<Caixa> caixaList){
		return caixaList.get(0);
	}

}
