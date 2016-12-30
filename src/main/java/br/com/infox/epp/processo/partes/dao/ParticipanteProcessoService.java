package br.com.infox.epp.processo.partes.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.epp.relacionamentoprocessos.RelacionamentoProcessoDAO;

@Stateless
public class ParticipanteProcessoService {

    @Inject
    private RelacionamentoProcessoDAO relacionamentoProcessoDAO;
    @Inject
    private ParticipanteProcessoManager participanteProcessoManager;

    public void adicionaParticipantesProcessoRelacionado(Processo processo) throws CloneNotSupportedException {
        List<RelacionamentoProcessoInterno> relacionados = relacionamentoProcessoDAO.getListProcessosEletronicosRelacionados(processo);
        Iterator<RelacionamentoProcessoInterno> it = relacionados.iterator();
        while (it.hasNext()) {
            Processo relacionado = it.next().getProcesso();
            List<ParticipanteProcesso> participantesRaizProcesso = participanteProcessoManager.getParticipantesProcessoRaiz(relacionado);
            for (ParticipanteProcesso raiz : participantesRaizProcesso) {
                adicionaParticipantesFilhos(processo, raiz, relacionado);
            }
        }
    }

    private void adicionaParticipantesFilhos(Processo processo, ParticipanteProcesso parte, Processo relacionado)
            throws CloneNotSupportedException {

        List<ParticipanteProcesso> listaPartes = new ArrayList<>();

        ParticipanteProcesso participanteRaizCopy = parte.copiarParticipanteMantendoFilhos();
        participanteRaizCopy.setProcesso(processo);
        participanteRaizCopy.setParticipantesFilhos(getCopyParticipantesFilhos(participanteRaizCopy, processo));
        listaPartes.add(participanteRaizCopy);
        preencherParticipantesFilhos(processo, listaPartes, participanteRaizCopy.getParticipantesFilhos());

        participanteProcessoManager.gravarListaParticipantes(listaPartes);

    }

    private List<ParticipanteProcesso> getCopyParticipantesFilhos(ParticipanteProcesso participanteAtual, Processo processo)
            throws CloneNotSupportedException {

        List<ParticipanteProcesso> participantesFilhos = new ArrayList<>();
        for (ParticipanteProcesso participanteProcesso : participanteAtual.getParticipantesFilhos()) {
            ParticipanteProcesso parteFilho = participanteProcesso.copiarParticipanteMantendoFilhos();
            parteFilho.setProcesso(processo);
            parteFilho.setParticipantePai(participanteAtual);
            participantesFilhos.add(parteFilho);
        }
        return participantesFilhos;
    }

    private void preencherParticipantesFilhos(Processo processo, List<ParticipanteProcesso> participantesParaPersistir,
            List<ParticipanteProcesso> participantesFilhos) throws CloneNotSupportedException {

        for (ParticipanteProcesso filho : participantesFilhos) {
            filho.setParticipantesFilhos(getCopyParticipantesFilhos(filho, processo));
            participantesParaPersistir.add(filho);
            preencherParticipantesFilhos(processo, participantesParaPersistir, filho.getParticipantesFilhos());
        }
    }
}
