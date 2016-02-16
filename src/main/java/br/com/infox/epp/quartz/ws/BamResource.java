package br.com.infox.epp.quartz.ws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import br.com.infox.epp.tarefa.type.PrazoEnum;

public interface BamResource {
    
    public static final String TAREFA_TIMER_PROCESSOR = "/tarefaTimerProcessor";
    public static final String PROCESSO_TIMER_PROCESSOR = "/processoTimerProcessor";
    
    @POST
    @Path(TAREFA_TIMER_PROCESSOR)
    public void tarefaTimerProcessor(@QueryParam("parameterName") String parameterName, @QueryParam("prazo") PrazoEnum prazo);
  
    @POST
    @Path(PROCESSO_TIMER_PROCESSOR)
    public void processoTimerProcessor(@QueryParam("parameterName") String parameterName, @QueryParam("prazo") PrazoEnum prazo);

}
