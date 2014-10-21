package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.action.IniciarProcessoAction;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.seam.exception.BusinessException;

@Name(ParticipantesController.NAME)
public class ParticipantesController extends AbstractParticipantesController {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "participantesController";
    private static final LogProvider LOG = Logging.getLogProvider(ParticipantesController.class);
    
    @In
    private TipoParteManager tipoParteManager;
    @In
    private ProcessoEpaManager processoEpaManager;
    @In
    private IniciarProcessoAction iniciarProcessoAction;
    @In
    private ParticipanteProcessoManager participanteProcessoManager;
    @In
    private ActionMessagesService actionMessagesService;

    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private ProcessoEpa processoEpa;
    private List<TipoParte> tipoPartes;
    
    private void createProcessoEpa() {
    	if (processoEpa != null)  {
    		return;
    	}
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        final Localizacao localizacao = Authenticator.getLocalizacaoAtual();
        processoEpa = new ProcessoEpa(SituacaoPrazoEnum.SAT, new Date(), "", usuarioLogado, naturezaCategoriaFluxo, localizacao, null);
    	try {
			processoEpaManager.persist(processoEpa);
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);
		}
	}

    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}

	public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
		if (naturezaCategoriaFluxo != null ){
			createProcessoEpa();
		}
	}
	
	public List<ParticipanteProcesso> getParticipantesAtivos(){
    	return getPartesAtivas(processoEpa.getParticipantes());
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

	@SuppressWarnings("unchecked")
	public List<ParticipanteProcesso> getParticipantesProcesso() {
		return processoEpa != null ? processoEpa.getParticipantes() : Collections.EMPTY_LIST;
	}

	public List<TipoParte> getTipoPartes() {
    	if (tipoPartes == null){
    		tipoPartes = tipoParteManager.findAll();
    	}
		return tipoPartes;
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

    public void removeParticipanteProcesso(ParticipanteProcesso participanteProcesso) {
    	try {
    		processoEpa.getParticipantes().remove(participanteProcesso);
			participanteProcessoManager.remove(participanteProcesso);
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);		
		}
    }

    @Override
    public boolean podeAdicionarPartesFisicas() {
        return hasPartes()
                && !apenasPessoaJuridica()
                && (getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesFisicas() == 0 || filtrar(getParticipantesProcesso(), TipoPessoaEnum.F).size() < getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesFisicas());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return hasPartes()
                && !apenasPessoaFisica()
                && (getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesJuridicas() == 0 || filtrar(getParticipantesProcesso(), TipoPessoaEnum.J).size() < getNaturezaCategoriaFluxo().getNatureza().getNumeroPartesJuridicas());
    }
    
    @Override
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getNaturezaCategoriaFluxo().getNatureza().getTipoPartes());
    }

    @Override
    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getNaturezaCategoriaFluxo().getNatureza().getTipoPartes());
    }
    
    public boolean podeRemoverParticipante(ParticipanteProcesso participanteProcesso) {
    	for (ParticipanteProcesso participante : getParticipantesProcesso()){
    		if (participanteProcesso.equals(participante.getParticipantePai())){
    			return false;
    		}
    	}
    	return true;
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
    
    public boolean podeIniciarProcesso(){
    	return getParticipantesProcesso() != null 
    			&& filtrar(getParticipantesProcesso(), TipoPessoaEnum.F).size() > 0
    			&& filtrar(getParticipantesProcesso(), TipoPessoaEnum.J).size() > 0;
    }

    private boolean hasPartes() {
        return getNaturezaCategoriaFluxo() != null 
        		&& getNaturezaCategoriaFluxo().getNatureza() != null 
        		&& getNaturezaCategoriaFluxo().getNatureza().getHasPartes();
    }

	public ProcessoEpa getProcessoEpa() {
		return processoEpa;
	}

	public void setProcessoEpa(ProcessoEpa processoEpa) {
		this.processoEpa = processoEpa;
	}
    
}
