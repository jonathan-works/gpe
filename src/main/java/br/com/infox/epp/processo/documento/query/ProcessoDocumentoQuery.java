package br.com.infox.epp.processo.documento.query;

public interface ProcessoDocumentoQuery {
    
    String PARAM_PROCESSO = "processo";
    String PARAM_TIPO_PROCESSO = "tipoNumeracao";
    
    String NEXT_SEQUENCIAL = "getNextSequencial";
    String NEXT_SEQUENCIAL_QUERY = "select max(pd.numeroDocumento) from ProcessoDocumento pd "
            + "inner join pd.tipoProcessoDocumento tpd where pd.processo = :" + PARAM_PROCESSO +
            " and tpd.numera=true and tpd.tipoNumeracao=:" + PARAM_TIPO_PROCESSO +
            " group by pd.processo";
    
    String ID_JDBPM_TASK_PARAM = "idJbpmTask";
    String USUARIO_PARAM = "usuario";
    String LIST_ANEXOS_PUBLICOS = "listAnexosPublicos";
    String LIST_ANEXOS_PUBLICOS_QUERY = "select o from ProcessoDocumento o inner join o.tipoProcessoDocumento tpd "
            + "where o.idJbpmTask = :" + ID_JDBPM_TASK_PARAM +" and (tpd.visibilidade='A' or tpd.visibilidade='E') and "
            + "not exists(select s from SigiloDocumento s where s.ativo = true and s.documento = o)";
    String LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO = "listAnexosPublicosUsuarioLogado";
    String LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO_QUERY = "select o from ProcessoDocumento o inner join o.tipoProcessoDocumento tpd "
            + "where o.idJbpmTask = :" + ID_JDBPM_TASK_PARAM +" and (tpd.visibilidade='A' or tpd.visibilidade='E') and "
            + "(not exists(select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) or "
            + "exists (select 1 from SigiloDocumentoPermissao sp where sp.usuario = :" + USUARIO_PARAM
            + " and sp.ativo = true and "
            + "sp.sigiloDocumento = (select s from SigiloDocumento s where s.ativo = true and s.documento = o)"
            + ")"
            + ")";
	String DATA_INCLUSAO = "dataInclusao";

}
