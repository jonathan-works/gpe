package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

@Name(ParticipantesController.NAME)
public class ParticipantesController extends AbstractParticipantesController {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "participantesController";
    private static final LogProvider LOG = Logging.getLogProvider(ParticipantesController.class);

    private Natureza natureza;
    private List<TipoParte> tipoPartes;
    private List<ParticipanteProcesso> participantesProcesso = new ArrayList<>();

    @In
    private TipoParteManager tipoParteManager;

    public Natureza getNatureza() {
        return natureza;
    }

    public void setNatureza(Natureza natureza) {
        this.natureza = natureza;
    }

    public List<ParticipanteProcesso> getParticipantesProcesso() {
		return participantesProcesso;
	}

	public void setParticipantesProcesso(List<ParticipanteProcesso> participantesProcesso) {
		this.participantesProcesso = participantesProcesso;
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
            pessoaFisicaManager.persist((PessoaFisica) getParticipantePessoaFisica().getPessoa());
            includeParticipanteProcesso(getParticipantePessoaFisica());
        } catch (DAOException e) {
            LOG.error("Não foi possível inserir a pessoa " + getParticipantePessoaFisica().getPessoa(), e);
        } finally {
            clearParticipantePessoaFisica();
        }
    }

    @Override
    public void includePessoaJuridica() {
        try {
            pessoaJuridicaManager.persist((PessoaJuridica) getParticipantePessoaJuridica().getPessoa());
            includeParticipanteProcesso(getParticipantePessoaJuridica());
        } catch (DAOException e) {
            LOG.error("Não foi possível inserir a pessoa " + getParticipantePessoaJuridica().getPessoa(), e);
        } finally {
            clearParticipantePessoaJuridica();
        }
    }

    private void includeParticipanteProcesso(ParticipanteProcesso participanteProcesso) {
        if (!getParticipantesProcesso().contains(participanteProcesso)) {
        	getParticipantesProcesso().add(participanteProcesso);
        } else {
            FacesMessages.instance().add(Severity.WARN, participanteProcesso.getPessoa()
                    + "já cadastrada na lista de partes");
        }
    }

    public void removeParticipanteProcesso(ParticipanteProcesso participanteProcesso) {
    	getParticipantesProcesso().remove(participanteProcesso);
    }

    @Override
    public boolean podeAdicionarPartesFisicas() {
        return hasPartes()
                && !apenasPessoaJuridica()
                && (natureza.getNumeroPartesFisicas() == 0 || filtrar(getParticipantesProcesso(), TipoPessoaEnum.F).size() < natureza.getNumeroPartesFisicas());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return hasPartes()
                && !apenasPessoaFisica()
                && (natureza.getNumeroPartesJuridicas() == 0 || filtrar(getParticipantesProcesso(), TipoPessoaEnum.J).size() < natureza.getNumeroPartesJuridicas());
    }
    
    @Override
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getNatureza().getTipoPartes());
    }

    @Override
    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getNatureza().getTipoPartes());
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

    private boolean hasPartes() {
        return natureza != null && natureza.getHasPartes();
    }

}
