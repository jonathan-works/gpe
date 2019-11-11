package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.entrega.documentos.EntregaResponsavel;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.seam.exception.ApplicationException;

@Stateless
public class EntregaResponsavelService {

    /**
     * Adiciona como {@link ParticipanteProcesso} todos os {@link EntregaResponsavel}.
     * @param processo {@link Processo} que irá adicionar os {@link ParticipanteProcesso};
     * @param entrega {@link Entrega} que irá recuperar os {@link EntregaResponsavel}.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void adicionaParticipantes(Processo processo, Entrega entrega) {
        List<EntregaResponsavel> responsaveisEntrega = entrega.getResponsaveis();
        List<EntregaResponsavel> responsaveisPadaAdd = new ArrayList<>(responsaveisEntrega.size());
        Map<Long, ParticipanteProcesso> responsaveisAdicionados = new HashMap<>(responsaveisEntrega.size());
        responsaveisPadaAdd.addAll(responsaveisEntrega);

        try {
            while (!responsaveisPadaAdd.isEmpty()) {
                for (Iterator<EntregaResponsavel> iterator = responsaveisPadaAdd.iterator(); iterator.hasNext();) {
                    EntregaResponsavel responsavel = iterator.next();
                    EntregaResponsavel responsavelVinculado = responsavel.getResponsavelVinculado();
                    if (responsavelVinculado == null || responsaveisAdicionados.containsKey(responsavelVinculado.getId())) {
                        ParticipanteProcesso parte = createParticipanteFromEntregaResponsavel(processo, responsavel,
                                (responsavelVinculado == null ? null : responsaveisAdicionados.get(responsavelVinculado.getId())));
                        getEntityManager().persist(parte);
                        responsaveisAdicionados.put(responsavel.getId(), parte);
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            throw new ApplicationException("Falha ao executar adicionarParticipantes.", e.getCause());
        }

        getEntityManager().flush();
    }

    private ParticipanteProcesso createParticipanteFromEntregaResponsavel(Processo processo, EntregaResponsavel responsavel, ParticipanteProcesso ppPai) {
        ParticipanteProcesso pp = new ParticipanteProcesso();
        pp.setAtivo(true);
        pp.setDataFim(responsavel.getDataFim());
        pp.setDataInicio(responsavel.getDataInicio());
        pp.setNome(responsavel.getNome());
        pp.setParticipantePai(ppPai);
        pp.setPessoa(responsavel.getPessoa());
        pp.setProcesso(processo);
        pp.setTipoParte(responsavel.getTipoParte());
        return pp;
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
