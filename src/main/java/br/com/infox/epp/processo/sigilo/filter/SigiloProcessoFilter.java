package br.com.infox.epp.processo.sigilo.filter;

public interface SigiloProcessoFilter {
    String TYPE_INT = "int";

    String PARAM_ID_USUARIO = "idUsuario";

    /**
     * De acordo com a documentação do Hibernate
     * (https://docs.jboss.org/hibernate/orm/4.2/manual/en-US/html/ch19.html) é
     * melhor que o parâmetro venha do lado esquerdo do operador
     **/
    String FILTER_SIGILO_PROCESSO = "sigiloProcesso";
    String CONDITION_FILTER_SIGILO_PROCESSO = "(not exists (select 1 from tb_sigilo_processo sp where "
            + "sp.id_processo = id_processo and sp.in_ativo = '1') "
            + "or exists (select 1 from tb_sigilo_processo_permissao spp where "
            + "spp.in_ativo = '1' and :"
            + PARAM_ID_USUARIO
            + " = spp.id_usuario_login and "
            + "spp.id_sigilo_processo = (select sp.id_sigilo_processo from tb_sigilo_processo sp where "
            + "sp.id_processo = id_processo and sp.in_ativo = '1'))" + ")";
}
