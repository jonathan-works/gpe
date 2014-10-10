package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

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
    private PessoaFisicaManager pessoaFisicaManager;
    @In
    private PessoaJuridicaManager pessoaJuridicaManager;
    @In
    private ProcessoEpaManager processoEpaManager;
    
    private ProcessoEpa processoEpa;

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
        setPessoaFisica(new PessoaFisica());
        setPessoaJuridica(new PessoaJuridica());
    }

    private Natureza getNatureza() {
        return processoEpa.getNaturezaCategoriaFluxo().getNatureza();
    }

    public List<ParticipanteProcesso> getPartesFisicas() {
        List<ParticipanteProcesso> fisicas = filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.F);
        if (Authenticator.isUsuarioAtualResponsavel()) {
            return fisicas;
        } else {
            return getPartesAtivas(fisicas);
        }
    }

    public List<ParticipanteProcesso> getPartesJuridicas() {
        List<ParticipanteProcesso> juridicas = filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.J);
        if (Authenticator.isUsuarioAtualResponsavel()) {
            return juridicas;
        } else {
            return getPartesAtivas(juridicas);
        }
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

    @Override
    public void includePessoaFisica() {
        try {
            pessoaFisicaManager.persist(getPessoaFisica());
            participanteProcessoManager.incluir(processoEpa, getPessoaFisica(), Boolean.TRUE);
            processoEpaManager.refresh(processoEpa);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error("Não foi possível inserir a pessoa " + getPessoaFisica(), e);
        } finally {
            setPessoaFisica(new PessoaFisica());
        }
    }

    @Override
    public void includePessoaJuridica() {
        try {
            pessoaJuridicaManager.persist(getPessoaJuridica());
            participanteProcessoManager.incluir(processoEpa, getPessoaJuridica(), Boolean.TRUE);
            processoEpaManager.refresh(processoEpa);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error("Não foi possível inserir a pessoa "
                    + getPessoaJuridica(), e);
        } finally {
            setPessoaJuridica(new PessoaJuridica());
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

    @Override
    public boolean podeAdicionarPartesFisicas() {
        return getNatureza().getHasPartes()
                && !apenasPessoaJuridica()
                && (getNatureza().getNumeroPartesFisicas() == QUANTIDADE_INFINITA_PARTES || getPartesAtivas(filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.F)).size() < getNatureza().getNumeroPartesFisicas());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return getNatureza().getHasPartes()
                && !apenasPessoaFisica()
                && (getNatureza().getNumeroPartesJuridicas() == QUANTIDADE_INFINITA_PARTES || getPartesAtivas(filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.J)).size() < getNatureza().getNumeroPartesJuridicas());
    }

    public boolean podeInativarPartesFisicas() {
        return getPartesAtivas(filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.F)).size() > QUANTIDADE_MINIMA_PARTES;
    }

    public boolean podeInativarPartesJuridicas() {
        return getPartesAtivas(filtrar(processoEpa.getParticipantes(), TipoPessoaEnum.J)).size() > QUANTIDADE_MINIMA_PARTES;
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

}
