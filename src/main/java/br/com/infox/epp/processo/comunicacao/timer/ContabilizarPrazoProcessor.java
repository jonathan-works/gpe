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

import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;

@Name(ContabilizarPrazoProcessor.NAME)
@AutoCreate
public class ContabilizarPrazoProcessor {
    static final String NAME = "contabilizarPrazoProcessor";
    
    @In
    private ComunicacaoService comunicacaoService;
    
    @Asynchronous
    @Transactional
    public QuartzTriggerHandle processContabilizarPrazo(@IntervalCron String cron) {
        processPrazoCiencia();
        processPrazoAtendimento();
        
        return null;
    }

    private void processPrazoCiencia() {
        List<Processo> comunicacoes = comunicacaoService.getComunicacoesAguardandoCiencia();
        
        for (Processo comunicacao : comunicacoes) {
            Date hoje = new Date();
            Date prazoCiencia = comunicacao.getMetadado(ComunicacaoService.DATA_FIM_PRAZO_CIENCIA).getValue();
            if (prazoCiencia.before(hoje)) {
                finalizarPrazoCiencia(comunicacao);
            }
        }
    }
    
    private void processPrazoAtendimento() {
        List<Processo> comunicacoes = comunicacaoService.getComunicacoesAguardandoCumprimento();
        
        for (Processo comunicacao : comunicacoes) {
            Date hoje = new Date();
            MetadadoProcesso prazoDestinatario = comunicacao.getMetadado(ComunicacaoService.DATA_FIM_PRAZO_DESTINATARIO);
            if (prazoDestinatario != null && ((Date) prazoDestinatario.getValue()).before(hoje)) {
                finalizarPrazoAtendimento(comunicacao);
            }
        }
    }

    private void finalizarPrazoCiencia(Processo comunicacao) {
        /*
         * Seta data de ciência
         * Seta usuário da ciência como o Sistema (usuarioLogin com id = 0)
         * Calcula prazo para cumprimento
         * Anda o processo
         */
    }

    private void finalizarPrazoAtendimento(Processo comunicacao) {
        /*
         * Seta a data de atendimento
         * Seta o usuário de atendimento?
         * Anda o processo? Finaliza?
         */
    }
}
