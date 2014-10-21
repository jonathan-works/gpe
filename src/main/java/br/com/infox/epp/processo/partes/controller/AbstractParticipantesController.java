package br.com.infox.epp.processo.partes.controller;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@Scope(ScopeType.CONVERSATION)
public abstract class AbstractParticipantesController implements Serializable {

	private static final long serialVersionUID = 1L;
	protected static final String RECURSO_ADICIONAR = "/pages/Processo/adicionarParticipanteProcesso";
	protected static final String RECURSO_EXCLUIR = "/pages/Processo/excluirParticipanteProcesso";
	
    @In
    protected PessoaFisicaManager pessoaFisicaManager;
    @In
    protected PessoaJuridicaManager pessoaJuridicaManager;
    @In
    protected MeioContatoManager meioContatoManager;
    
    private ParticipanteProcesso participantePessoaFisica;
    private ParticipanteProcesso participantePessoaJuridica;
    private String email;
    private MeioContato meioContato;
    
    @Create
    public void init(){
    	clearParticipantePessoaFisica();
    	clearParticipantePessoaJuridica();
    }
    
    protected void clearParticipantePessoaFisica(){
    	participantePessoaFisica = new ParticipanteProcesso();
    	participantePessoaFisica.setPessoa(new PessoaFisica());
    	meioContato = new MeioContato();
    	email = null;
    }
    
    protected void clearParticipantePessoaJuridica(){
    	participantePessoaJuridica = new ParticipanteProcesso();
    	participantePessoaJuridica.setPessoa(new PessoaJuridica());
    }
    
    public abstract boolean podeAdicionarPartesFisicas();
    
    public abstract boolean podeAdicionarPartesJuridicas();

    public abstract void includePessoaFisica();

    public abstract void includePessoaJuridica();

    public abstract boolean apenasPessoaFisica();

    public abstract boolean apenasPessoaJuridica();

	public void searchByCpf() {
        final String cpf = getParticipantePessoaFisica().getPessoa().getCodigo();
        getParticipantePessoaFisica().setPessoa(pessoaFisicaManager.getByCpf(cpf));
        if (getParticipantePessoaFisica().getPessoa() == null) {
        	PessoaFisica pessoaFisica = new PessoaFisica();
        	pessoaFisica.setCpf(cpf);
        	pessoaFisica.setAtivo(true);
        	getParticipantePessoaFisica().setPessoa(pessoaFisica);
        } else {
        	meioContato = meioContatoManager.getMeioContatoByPessoaAndTipo(getParticipantePessoaFisica().getPessoa(), TipoMeioContatoEnum.EM);
        	if (meioContato == null){
        		meioContato = new MeioContato();
        	} else {
        		email = meioContato.getMeioContato();
        	}
        }
    }

    public void searchByCnpj() {
        final String cnpj = getParticipantePessoaJuridica().getPessoa().getCodigo();
        getParticipantePessoaJuridica().setPessoa(pessoaJuridicaManager.getByCnpj(cnpj));
        if (getParticipantePessoaJuridica().getPessoa() == null) {
        	PessoaJuridica pessoaJuridica = new PessoaJuridica();
        	pessoaJuridica.setCnpj(cnpj);
        	pessoaJuridica.setAtivo(true);
            getParticipantePessoaJuridica().setPessoa(pessoaJuridica);
        }
    }
    
    public ParticipanteProcesso getParticipantePessoaFisica() {
		return participantePessoaFisica;
	}

	public void setParticipantePessoaFisica(
			ParticipanteProcesso participantePessoaFisica) {
		this.participantePessoaFisica = participantePessoaFisica;
	}

	public ParticipanteProcesso getParticipantePessoaJuridica() {
		return participantePessoaJuridica;
	}

	public void setParticipantePessoaJuridica(
			ParticipanteProcesso participantePessoaJuridica) {
		this.participantePessoaJuridica = participantePessoaJuridica;
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
	
}
