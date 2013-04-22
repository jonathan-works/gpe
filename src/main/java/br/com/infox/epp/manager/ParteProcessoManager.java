package br.com.infox.epp.manager;

import javax.persistence.Query;
import javax.xml.bind.ValidationException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ProcessoEpaDAO;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.type.TipoPessoaEnum;
import br.com.infox.ibpm.entity.HistoricoParteProcesso;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Pessoa;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.home.PessoaFisicaHome;
import br.com.infox.ibpm.home.PessoaJuridicaHome;
import br.com.itx.util.EntityUtil;

@Name(ParteProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ParteProcessoManager extends GenericManager {

	public static final String NAME = "parteProcessoManager";
	private static final long serialVersionUID = 1L;
	
	@In private ProcessoEpaDAO processoEpaDAO; 
	
	public void alternarAtividade(ParteProcesso parteProcesso, String motivoModificacao){
		HistoricoParteProcesso hpp = new HistoricoParteProcesso(parteProcesso, motivoModificacao);
		parteProcesso.setAtivo(!parteProcesso.getAtivo());
		update(parteProcesso);
		persist(hpp);
	}
	
	public void carregaPessoa(String tipoPessoa, String codigo){
		StringBuilder sb = new StringBuilder();
		Pessoa pessoa;
		sb.append("select o from ");
		if (tipoPessoa.equals("F") || tipoPessoa.equals("f")) {
			sb.append("PessoaFisica o where o.cpf = :cpf");
			Query query = EntityUtil.createQuery(sb.toString()).setParameter("cpf", codigo);
			pessoa = EntityUtil.getSingleResult(query);
			Events.instance().raiseEvent("evtCarregarPessoaFisica", pessoa);
		} else if (tipoPessoa.equals("J") || tipoPessoa.equals("j")){
			sb.append("PessoaJuridica o where o.cnpj = :cnpj");
			Query query = EntityUtil.createQuery(sb.toString()).setParameter("cnpj", codigo);
			pessoa = EntityUtil.getSingleResult(query);
			Events.instance().raiseEvent("evtCarregarPessoaJuridica", pessoa);
		} else return;
		
	}
	
	public void incluir(Processo processo, String tipoPessoa){
		ProcessoEpa processoEpa = processoEpaDAO.getProcessoEpaByProcesso(processo);
		if (tipoPessoa.equals("F") || tipoPessoa.equals("f")) {
			PessoaFisicaHome pf = (PessoaFisicaHome) Component.getInstance("pessoaFisicaHome");
			PessoaFisica p = pf.getInstance();
			if (p.getAtivo() == null){
				p.setAtivo(true);
				p.setTipoPessoa(TipoPessoaEnum.F);
				EntityUtil.getEntityManager().persist(p);
				EntityUtil.getEntityManager().flush();
			}
			if (processoEpa.getPartes().contains(p))
				FacesMessages.instance().add(Severity.ERROR, "Parte já cadastrada no processo");
			else
				persist(new ParteProcesso(processoEpa, p));
			pf.setInstance(null);
		}
		else if (tipoPessoa.equals("J") || tipoPessoa.equals("j")) {
			PessoaJuridicaHome pj = (PessoaJuridicaHome) Component.getInstance("pessoaJuridicaHome");
			PessoaJuridica p = pj.getInstance();
			if (p.getAtivo() == null){
				p.setAtivo(true);
				p.setTipoPessoa(TipoPessoaEnum.J);
				EntityUtil.getEntityManager().persist(p);
				EntityUtil.getEntityManager().flush();
			}
			if (processoEpa.getPartes().contains(p))
				FacesMessages.instance().add(Severity.ERROR, "Parte já cadastrada no processo");
			else
				persist(new ParteProcesso(processoEpa, p));
			pj.setInstance(null);
		} else return;
	}
	
	public HistoricoParteProcesso restaurarParteProcesso(ParteProcesso parteProcessoAtual, HistoricoParteProcesso versaoAnterior, String motivoRestauracao) throws ValidationException {
		if (!parteProcessoAtual.getIdParteProcesso().equals(versaoAnterior.getParteModificada().getIdParteProcesso()))
			throw new ValidationException("Restauração inválida: Histórico passado não pertence ao Histórico da Parte de Processo instanciada");
		
		HistoricoParteProcesso novoHistorico = new HistoricoParteProcesso(parteProcessoAtual, motivoRestauracao);
		persist(novoHistorico);
		parteProcessoAtual.setAtivo(parteProcessoAtual.getAtivo());
		update(parteProcessoAtual);
		return novoHistorico;
	}
}
