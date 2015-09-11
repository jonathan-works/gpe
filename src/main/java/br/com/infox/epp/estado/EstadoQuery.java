package br.com.infox.epp.estado;

public interface EstadoQuery {
    String PARAM_SIGLA = "sigla";
    
    String ESTADO_BY_SIGLA = "Estado.estadoBySigla";
    String ESTADO_BY_SIGLA_QUERY = "select o from Estado o where o.sigla = :" + PARAM_SIGLA;
}
