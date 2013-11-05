package br.com.infox.epp.manager;

import java.util.List;
import java.util.Map;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ProcessoEpaDAO;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.type.SituacaoPrazoEnum;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Pessoa;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.infox.ibpm.entity.Processo;

@Name(ProcessoEpaManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoEpaManager extends GenericManager {

	private static final int PORCENTAGEM = 100;

    private static final int HOURS_OF_DAY = 24;

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
			Map<String, Object> result = processoEpaDAO.getTempoGasto(processoEpa);
			
			if (result != null) {
				Fluxo f = processoEpa.getNaturezaCategoriaFluxo().getFluxo();
				Long dias = (Long)result.get("dias");
				Long tempoGasto = ((Long)result.get("horas"))/HOURS_OF_DAY;
				if (dias != null) {
					tempoGasto += dias;
				}
				processoEpa.setTempoGasto(tempoGasto.intValue());
			
				if(f.getQtPrazo() != null && f.getQtPrazo() != 0) {
					processoEpa.setPorcentagem((processoEpa.getTempoGasto()*PORCENTAGEM) / f.getQtPrazo());
				}
				if (processoEpa.getPorcentagem() > PORCENTAGEM) {
					processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.PAT);
				}
				processoEpaDAO.update(processoEpa);
			}
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
	
	public List<PessoaFisica> getPessoaFisicaList(){
		return processoEpaDAO.getPessoaFisicaList();
	}
	
	public List<PessoaJuridica> getPessoaJuridicaList(){
		return processoEpaDAO.getPessoaJuridicaList();
	}
}