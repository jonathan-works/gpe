package br.com.infox.epp.processo.comunicacao.timer;

import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.processo.comunicacao.Comunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ComunicacaoManager;

@Name(ContabilizarPrazoProcessor.NAME)
@AutoCreate
public class ContabilizarPrazoProcessor {
    static final String NAME = "contabilizarPrazoProcessor";
    
    @In
    private ComunicacaoManager comunicacaoManager;
    
    @Asynchronous
    @Transactional
    public QuartzTriggerHandle processContabilizarPrazo(@IntervalCron String cron) {
        processPrazoCiencia();
        processPrazoAtendimento();
        
        return null;
    }

    private void processPrazoCiencia() {
        List<Comunicacao> comunicacoes = comunicacaoManager.getComunicacoesAguardandoCumprimento();
        
        for (Comunicacao comunicacao : comunicacoes) {
            Date hoje = new Date();
            Date prazoCiencia = comunicacaoManager.getDataFimPrazoCiencia(comunicacao);
            if (prazoCiencia.before(hoje)) {
                finalizarPrazoCiencia(comunicacao);
            }
        }
    }
    
    private void processPrazoAtendimento() {
        List<Comunicacao> comunicacoes = comunicacaoManager.getComunicacoesAguardandoCiencia();
        
        for (Comunicacao comunicacao : comunicacoes) {
            Date hoje = new Date();
            Date prazoCiencia = comunicacaoManager.getDataFimPrazoCiencia(comunicacao);
            if (prazoCiencia.before(hoje)) {
                finalizarPrazoAtendimento(comunicacao);
            }
        }
    }

    private void finalizarPrazoCiencia(Comunicacao comunicacao) {
        /*
         * Seta data de ciência
         * Seta usuário da ciência como o Sistema (usuarioLogin com id = 0)
         * Calcula prazo para cumprimento
         * Anda o processo
         */
    }

    private void finalizarPrazoAtendimento(Comunicacao comunicacao) {
        /*
         * Seta a data de atendimento
         * Seta o usuário de atendimento?
         * Anda o processo? Finaliza?
         */
    }
}
