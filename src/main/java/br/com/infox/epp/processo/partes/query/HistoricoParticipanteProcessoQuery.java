package br.com.infox.epp.processo.partes.query;

public interface HistoricoParticipanteProcessoQuery {
    String PARAM_PARTICIPANTE_PROCESSO = "participanteProcesso";
    
    String LIST_BY_PARTICIPANTE_PROCESSO = "listByParticipanteProcesso";
    String LIST_BY_PARTICIPANTE_PROCESSO_QUERY = "select o from HistoricoParticipanteProcesso o "
            + "where o.participanteModificado = :" + PARAM_PARTICIPANTE_PROCESSO;
    
    String HAS_HISTORICO_BY_PARTICIPANTE = "hasHistoricoByParticipante";
    String HAS_HISTORICO_BY_PARTICIPANTE_QUERY = "select count(*) from HistoricoParticipanteProcesso " +
            "where participanteModificado = :" + PARAM_PARTICIPANTE_PROCESSO;
}
