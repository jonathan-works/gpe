package br.com.infox.epp.quartz.ws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

public interface QuartzResource {
    
    public static final String PROCESS_BLOQUEIO_USUARIO = "/processBloqueioUsuario";
    public static final String TASK_EXPIRATION_PROCESSOR = "/taskExpirationProcessor";
    public static final String CONTAGEM_PRAZO_PROCESSOR = "/contagemPrazoProcessor";
    public static final String AUTOMATIC_NODE_RETRY_PROCESSOR = "/automaticNodeRetryProcessor";
    public static final String CALENDARIO_EVENTOS_SYNC_PROCESSOR = "/calendarioEventosSyncProcessor";
    public static final String BAM_RESOURCE = "/bam";
    
    @POST
    @Path(PROCESS_BLOQUEIO_USUARIO)
    public void processBloqueioUsuario();
    
    @POST
    @Path(TASK_EXPIRATION_PROCESSOR)
    public void taskExpirationProcessor();
    
    @Path(BAM_RESOURCE)
    public BamResource getBamResource();
    
    @POST
    @Path(CONTAGEM_PRAZO_PROCESSOR)
    public void processContagemPrazoComunicacao();
    
    @POST
    @Path(AUTOMATIC_NODE_RETRY_PROCESSOR)
    public void retryAutomaticNodes();
    
    @POST
    @Path(CALENDARIO_EVENTOS_SYNC_PROCESSOR)
    public void processUpdateCalendarioSync();

}
