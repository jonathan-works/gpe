package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.seam.exception.BusinessException;

@Name(ParticipantesProcessoController.NAME)
public class ParticipantesProcessoController extends AbstractParticipantesController {

	private static final long serialVersionUID = 1L;
	private static final int QUANTIDADE_INFINITA_PARTES = 0;
    private static final int QUANTIDADE_MINIMA_PARTES = 1;
    public static final String NAME = "participantesProcessoController";
    private static final LogProvider LOG = Logging.getLogProvider(ParticipantesProcessoController.class);

    @In
    private ActionMessagesService actionMessagesService;
    @In
    private ParticipanteProcessoManager participanteProcessoManager;
    @In
    private ProcessoEpaManager processoEpaManager;
    @In
    private TipoParteManager tipoParteManager;
    
    private ProcessoEpa processoEpa;
    private List<TipoParte> tipoPartes;

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
        clearParticipantePessoaFisica();
        clearParticipantePessoaJuridica();
    }

    private Natureza getNatureza() {
        return processoEpa.getNaturezaCategoriaFluxo().getNatureza();
    }
    
    public List<ParticipanteProcesso> getParticipantes() {
    	return processoEpa.getParticipantes();
    }
    
    public List<ParticipanteProcesso> getParticipantesAtivos(){
    	return getPartesAtivas(processoEpa.getParticipantes());
    }

    private List<ParticipanteProcesso> filtrar(List<ParticipanteProcesso> participantes, 
    		TipoPessoaEnum tipoPessoa) {
        List<ParticipanteProcesso> filtrado = new ArrayList<>();
        for (ParticipanteProcesso participante : participantes) {
            if (tipoPessoa.equals(participante.getPessoa().getTipoPessoa())) {
                filtrado.add(participante);
            }
        }
        return filtrado;
    }
    
    private List<ParticipanteProcesso> getPartesAtivas(List<ParticipanteProcesso> participantes) {
        List<ParticipanteProcesso> participantesAtivas = new ArrayList<>();
        for (ParticipanteProcesso participante : participantes) {
            if (participante.getAtivo()) {
                participantesAtivas.add(participante);
            }
        }
        return participantesAtivas;
    }
    
    public boolean podeInativarPartes(String tipoPessoa){
    	if (TipoPessoaEnum.F.name().equals(tipoPessoa)) {
    		return podeInativarPartesFisicas();
    	} else if (TipoPessoaEnum.J.name().equals(tipoPessoa)) {
    		return podeInativarPartesJuridicas();
    	} else {
    		return false;
    	}
    }
    
    public boolean podeInativarPartesFisicas() {
        return Identity.instance().hasPermission(RECURSO_EXCLUIR, "access")
        		&& getPartesAtivas(filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.F)).size() > QUANTIDADE_MINIMA_PARTES;
    }

    public boolean podeInativarPartesJuridicas() {
        return Identity.instance().hasPermission(RECURSO_EXCLUIR, "access")
        		&& getPartesAtivas(filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.J)).size() > QUANTIDADE_MINIMA_PARTES;
    }
    
    public List<TipoParte> getTipoPartes() {
    	if (tipoPartes == null){
    		tipoPartes = tipoParteManager.findAll();
    	}
		return tipoPartes;
	}
    
    public boolean podeAdicionarPartes(String tipoPessoa){
    	if (TipoPessoaEnum.F.name().equals(tipoPessoa)) {
    		return podeAdicionarPartesFisicas();
    	} else if (TipoPessoaEnum.J.name().equals(tipoPessoa)) {
    		return podeAdicionarPartesJuridicas();
    	} else {
    		return false;
    	}
    }
    
    @Override
    public boolean podeAdicionarPartesFisicas() {
        return getNatureza().getHasPartes()
                && !apenasPessoaJuridica()
                && Identity.instance().hasPermission(RECURSO_ADICIONAR, "access")
                && (getNatureza().getNumeroPartesFisicas() == QUANTIDADE_INFINITA_PARTES || getPartesAtivas(filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.F)).size() < getNatureza().getNumeroPartesFisicas());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return getNatureza().getHasPartes()
                && !apenasPessoaFisica()
                && Identity.instance().hasPermission(RECURSO_ADICIONAR, "access")
                && (getNatureza().getNumeroPartesJuridicas() == QUANTIDADE_INFINITA_PARTES || getPartesAtivas(filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.J)).size() < getNatureza().getNumeroPartesJuridicas());
    }

    @Override
    public void includePessoaFisica() {
        try {
        	getParticipantePessoaFisica().setProcesso(processoEpa);
        	existeParticipante(getParticipantePessoaFisica());
            pessoaFisicaManager.persist((PessoaFisica) getParticipantePessoaFisica().getPessoa());
            participanteProcessoManager.persist(getParticipantePessoaFisica());
            processoEpaManager.refresh(processoEpa);
            participanteProcessoManager.flush();
            includeMeioContato(getParticipantePessoaFisica().getPessoa());
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error("Não foi possível inserir a pessoa " + getParticipantePessoaFisica().getPessoa(), e);
        } catch (BusinessException e){
        	FacesMessages.instance().add(e.getMessage());
        } finally {
        	clearParticipantePessoaFisica();
        }
    }

    private void includeMeioContato(Pessoa pessoa) throws DAOException {
    	if (getEmail() != null && !getEmail().equals(getMeioContato().getMeioContato())) {
    		getMeioContato().setPessoa(pessoa);
    		getMeioContato().setMeioContato(getEmail());
    		getMeioContato().setTipoMeioContato(TipoMeioContatoEnum.EM);
    		meioContatoManager.persist(getMeioContato());
    	}
	}
    
    private void existeParticipante(ParticipanteProcesso participanteProcesso){
    	ParticipanteProcesso pai = participanteProcesso.getParticipantePai();
    	Pessoa pessoa = participanteProcesso.getPessoa();
    	if (pessoa.getIdPessoa() == null) {
    		return; 
    	}
    	Processo processo = participanteProcesso.getProcesso();
    	TipoParte tipo = participanteProcesso.getTipoParte();
    	if (pai != null && pai.getPessoa().equals(participanteProcesso.getPessoa())) {
    		throw new BusinessException("Participante não pode ser filho dele mesmo");
    	}
    	boolean existe = participanteProcessoManager.existeParticipanteByPessoaProcessoPaiTipoLock(pessoa, processo, pai, tipo);
    	if (existe) {
    		throw new BusinessException("Participante já cadastrado");
    	}
    }
    
	@Override
    public void includePessoaJuridica() {
        try {
        	getParticipantePessoaJuridica().setProcesso(processoEpa);
        	existeParticipante(getParticipantePessoaJuridica());
            pessoaJuridicaManager.persist((PessoaJuridica) getParticipantePessoaJuridica().getPessoa());
            participanteProcessoManager.persist(getParticipantePessoaJuridica());
            processoEpaManager.refresh(processoEpa);
            participanteProcessoManager.flush();
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error("Não foi possível inserir a pessoa " + getParticipantePessoaJuridica().getPessoa(), e);
        } catch (BusinessException e){
        	FacesMessages.instance().add(e.getMessage());
        } finally {
            clearParticipantePessoaJuridica();
        }
    }

    @Override
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getNatureza().getTipoPartes());
    }

    @Override
    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getNatureza().getTipoPartes());
    }

}
