package br.com.infox.epp.processo.partes.manager;

import javax.xml.bind.ValidationException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.home.PessoaFisicaHome;
import br.com.infox.epp.pessoa.home.PessoaJuridicaHome;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.dao.ProcessoEpaDAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.partes.dao.HistoricoParteProcessoDAO;
import br.com.infox.epp.processo.partes.dao.ParteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.HistoricoParteProcesso;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;

@Name(ParteProcessoManager.NAME)
@AutoCreate
public class ParteProcessoManager extends Manager<ParteProcessoDAO, ParteProcesso> {

	public static final String NAME = "parteProcessoManager";
	private static final long serialVersionUID = 1L;
	
	@In private ProcessoEpaDAO processoEpaDAO; 
	@In private PessoaFisicaDAO pessoaFisicaDAO;
	@In private PessoaJuridicaDAO pessoaJuridicaDAO;
	@In private HistoricoParteProcessoDAO historicoParteProcessoDAO;
	
	public void alternarAtividade(ParteProcesso parteProcesso, String motivoModificacao) throws DAOException{
		HistoricoParteProcesso hpp = new HistoricoParteProcesso(parteProcesso, motivoModificacao);
		parteProcesso.setAtivo(!parteProcesso.getAtivo());
		update(parteProcesso);
		historicoParteProcessoDAO.persist(hpp);
	}
	
	public void incluir(Processo processo, String tipoPessoa) throws DAOException{
		ProcessoEpa processoEpa = processoEpaDAO.getProcessoEpaByProcesso(processo);
		if ("F".equals(tipoPessoa) || "f".equals(tipoPessoa)) {
			PessoaFisicaHome pf = (PessoaFisicaHome) Component.getInstance("pessoaFisicaHome");
			PessoaFisica p = pf.getInstance();
			if (p.getAtivo() == null){
				p.setAtivo(true);
				p.setTipoPessoa(TipoPessoaEnum.F);
				pessoaFisicaDAO.persist(p);
				flush();
			}
			if (processoEpa.getPartes().contains(p)) {
			    FacesMessages.instance().add(Severity.ERROR, "Parte já cadastrada no processo");
			}
			else {
			    persist(new ParteProcesso(processoEpa, p));
			}
			pf.setInstance(null);
		}
		else if ("J".equals(tipoPessoa) || "j".equals(tipoPessoa)) {
			PessoaJuridicaHome pj = (PessoaJuridicaHome) Component.getInstance("pessoaJuridicaHome");
			PessoaJuridica p = pj.getInstance();
			if (p.getAtivo() == null){
				p.setAtivo(true);
				p.setTipoPessoa(TipoPessoaEnum.J);
				pessoaJuridicaDAO.persist(p);
				flush();
			}
			if (processoEpa.getPartes().contains(p)) {
			    FacesMessages.instance().add(Severity.ERROR, "Parte já cadastrada no processo");
			}
			else {
			    persist(new ParteProcesso(processoEpa, p));
			}
			pj.setInstance(null);
		} else {
		    return;
		}
	}
	
	public HistoricoParteProcesso restaurarParteProcesso(ParteProcesso parteProcessoAtual, HistoricoParteProcesso versaoAnterior, String motivoRestauracao) throws ValidationException, DAOException {
		if (!parteProcessoAtual.getIdParteProcesso().equals(versaoAnterior.getParteModificada().getIdParteProcesso())) {
			throw new ValidationException("Restauração inválida: Histórico passado não pertence ao Histórico da Parte de Processo instanciada");
		}
		
		HistoricoParteProcesso novoHistorico = new HistoricoParteProcesso(parteProcessoAtual, motivoRestauracao);
		historicoParteProcessoDAO.persist(novoHistorico);
		parteProcessoAtual.setAtivo(parteProcessoAtual.getAtivo());
		update(parteProcessoAtual);
		return novoHistorico;
	}
}
