package br.com.infox.epp.processo.partes.controller;

import java.io.Serializable;

import javax.inject.Inject;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

public abstract class AbstractParticipantesController implements Serializable {

	private static final long serialVersionUID = 1L;
	protected static final String RECURSO_ADICIONAR = "/pages/Processo/adicionarParticipanteProcesso";
	protected static final String RECURSO_EXCLUIR = "/pages/Processo/excluirParticipanteProcesso";
	protected static final String RECURSO_VISUALIZAR = "/pages/Processo/exibirDetalhesParticipanteProcesso";
	private static final LogProvider LOG = Logging.getLogProvider(AbstractParticipantesController.class);
	
    @Inject
    protected PessoaFisicaManager pessoaFisicaManager;
    @Inject
    protected PessoaJuridicaManager pessoaJuridicaManager;
    @Inject
    protected ParticipanteProcessoManager participanteProcessoManager;
    @Inject
    protected MeioContatoManager meioContatoManager;
    @Inject
    protected ProcessoManager processoManager;
    @Inject
    protected ActionMessagesService actionMessagesService;
    
    private ParticipanteProcesso participanteProcesso = new ParticipanteProcesso();
    private Processo processo;
    private TipoPessoaEnum tipoPessoa = TipoPessoaEnum.F;
    protected String email;
    protected MeioContato meioContato;
    
    protected void clearParticipanteProcesso() {
    	participanteProcesso = new ParticipanteProcesso();
    	participanteProcesso.setPessoa(tipoPessoa == TipoPessoaEnum.F ? new PessoaFisica() : new PessoaJuridica());
    	meioContato = new MeioContato();
    	email = null;
    }
    
    public abstract boolean podeAdicionarPartesFisicas();
    
    public abstract boolean podeAdicionarPartesJuridicas();

    public abstract boolean apenasPessoaFisica();

    public abstract boolean apenasPessoaJuridica();
    
    protected void afterSaveParticipante(ParticipanteProcesso participanteProcesso){};

	public void searchByCpf() {
        String cpf = getParticipanteProcesso().getPessoa().getCodigo();
        getParticipanteProcesso().setPessoa(pessoaFisicaManager.getByCpf(cpf));
        if (getParticipanteProcesso().getPessoa() == null) {
        	PessoaFisica pessoaFisica = new PessoaFisica();
        	pessoaFisica.setCpf(cpf);
        	pessoaFisica.setAtivo(true);
        	getParticipanteProcesso().setPessoa(pessoaFisica);
        } else {
        	initEmailParticipante();
        }
    }

	protected void initEmailParticipante() {
		meioContato = meioContatoManager.getMeioContatoByPessoaAndTipo(getParticipanteProcesso().getPessoa(), TipoMeioContatoEnum.EM);
		if (meioContato == null){
			meioContato = new MeioContato();
		} else {
			email = meioContato.getMeioContato();
		}
	}

    public void searchByCnpj() {
        String cnpj = getParticipanteProcesso().getPessoa().getCodigo();
        getParticipanteProcesso().setPessoa(pessoaJuridicaManager.getByCnpj(cnpj));
        if (getParticipanteProcesso().getPessoa() == null) {
        	PessoaJuridica pessoaJuridica = new PessoaJuridica();
        	pessoaJuridica.setCnpj(cnpj);
        	pessoaJuridica.setAtivo(true);
        	getParticipanteProcesso().setPessoa(pessoaJuridica);
        }
    }
    
    public TipoPessoaEnum[] getTipoPessoaValues(){
    	return TipoPessoaEnum.values();
    }
    
    public boolean podeAdicionarAlgumTipoDeParte() {
    	return podeAdicionarPartesFisicas() || podeAdicionarPartesJuridicas();
    }
    
    public boolean podeAdicionarAmbosTiposDeParte() {
    	return podeAdicionarPartesFisicas() && podeAdicionarPartesJuridicas();
    }
    
    @Transactional
    public void includeParticipanteProcesso(){
		try {
			getParticipanteProcesso().setProcesso(getProcesso());
	    	existeParticipante(getParticipanteProcesso());
	    	if (getParticipanteProcesso().getPessoa().getIdPessoa() == null) {
	    		if (getParticipanteProcesso().getPessoa().getTipoPessoa() == TipoPessoaEnum.F) {
		    		pessoaFisicaManager.persist((PessoaFisica) getParticipanteProcesso().getPessoa());
		    	} else {
		    		pessoaJuridicaManager.persist((PessoaJuridica) getParticipanteProcesso().getPessoa());
		    	}
	    	} 
	    	if (getParticipanteProcesso().getPessoa().getTipoPessoa() == TipoPessoaEnum.F) {
	    		includeMeioContato(getParticipanteProcesso().getPessoa());
	    	}
		    participanteProcessoManager.persist(getParticipanteProcesso());
		    setProcesso(processoManager.merge(getProcesso()));
		    processoManager.refresh(getProcesso());
		    participanteProcessoManager.flush();
		    afterSaveParticipante(getParticipanteProcesso());
		} catch (DAOException e) {
		    actionMessagesService.handleDAOException(e);
		    LOG.error("Não foi possível inserir a pessoa " + getParticipanteProcesso().getPessoa(), e);
		} catch (BusinessException e){
		 	FacesMessages.instance().add(e.getMessage());
		} finally {
		    clearParticipanteProcesso();
		}
    }
    
    protected void includeMeioContato(Pessoa pessoa) throws DAOException {
    	if (getEmail() == null || getEmail().equals(getMeioContato().getMeioContato())) {
    		return;
    	}
    	boolean jaPossuiEmail = meioContatoManager.existeMeioContatoByPessoaTipoValor(pessoa, TipoMeioContatoEnum.EM, getEmail());
    	if (!jaPossuiEmail) {
    		getMeioContato().setPessoa(pessoa);
    		getMeioContato().setMeioContato(getEmail());
    		getMeioContato().setTipoMeioContato(TipoMeioContatoEnum.EM);
    		meioContatoManager.persist(getMeioContato());
    	}
	}
    
    protected void existeParticipante(ParticipanteProcesso participanteProcesso){
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
    
	public ParticipanteProcesso getParticipanteProcesso() {
		return participanteProcesso;
	}

	public void setParticipanteProcesso(ParticipanteProcesso participanteProcesso) {
		this.participanteProcesso = participanteProcesso;
	}

	public MeioContato getMeioContato() {
		return meioContato;
	}

	public void setMeioContato(MeioContato meioContato) {
		this.meioContato = meioContato;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public TipoPessoaEnum getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(TipoPessoaEnum tipoPessoa) {
		if (!getTipoPessoa().equals(tipoPessoa)) {
			this.tipoPessoa = tipoPessoa;
			clearParticipanteProcesso();
		}
	}
	
}
