package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ProcessoEpaDAO;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.type.SituacaoPrazoEnum;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.Item;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Pessoa;
import br.com.infox.ibpm.entity.Processo;

@Name(ProcessoEpaManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoEpaManager extends GenericManager {

	private static final long serialVersionUID = 168832523707680478L;

	public static final String NAME = "processoEpaManager";

	@In private ProcessoEpaDAO processoEpaDAO;

	public List<ProcessoEpa> listAllNotEnded() {
		return processoEpaDAO.listAllNotEnded();
	}
	
	public List<ProcessoEpa> listNotEnded(Fluxo fluxo) {
		return processoEpaDAO.listNotEnded(fluxo);
	}
	
	public void incluirParteProcesso(ProcessoEpa processoEpa, Pessoa pessoa){
		processoEpa.getPartes().add(new ParteProcesso(processoEpa, pessoa));
		update(processoEpa);
	}
	
	public void incluirParteProcesso(ProcessoEpa processoEpa, ParteProcesso parteProcesso){
		processoEpa.getPartes().add(parteProcesso);
		update(processoEpa);
	}
	
	public Boolean podeInativarPartesDoProcesso(Processo processo){
		return processoEpaDAO.podeInativarPartes(
				processoEpaDAO.getProcessoEpaByProcesso(processo));
	}
	
	public boolean podeInativarPartesDoProcesso(ProcessoEpa processoEpa){
		return processoEpaDAO.podeInativarPartes(processoEpa);
	}
	
	public void updateTempoGastoProcessoEpa() {
		List<ProcessoEpa> listAllNotEnded = listAllNotEnded();
		for (ProcessoEpa processoEpa : listAllNotEnded) {
			Fluxo f = processoEpa.getNaturezaCategoriaFluxo().getFluxo();
			
			Integer tempoGasto = processoEpa.getTempoGasto();
			if (tempoGasto == null) {
				tempoGasto = 0;
			}
			processoEpa.setTempoGasto(tempoGasto + 1);//? sem critério
			if(f.getQtPrazo() != null && f.getQtPrazo() != 0) {
				processoEpa.setPorcentagem((processoEpa.getTempoGasto()*100) / f.getQtPrazo());
			}
			if (processoEpa.getPorcentagem() > 100) {
				processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.PAT);
			}
			update(processoEpa);
		}
	}
	
	public Item getItemDoProcesso(int idProcesso){
		return processoEpaDAO.getItemDoProcesso(idProcesso);
	}
	
	public boolean hasPartes(Processo processo){
		return processoEpaDAO.hasPartes(processo);
	}
	
	public boolean hasPartes(Long idJbpm){
		return processoEpaDAO.hasPartes(idJbpm);
	}
}