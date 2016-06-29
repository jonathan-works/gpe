package br.com.infox.ibpm.exec;

import static br.com.infox.core.token.TokenRequester.BPM_EXECUTION_INFO;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.ObjectUtils;

import br.com.infox.core.token.AccessTokenAuthentication;

@Path("/bpm-execution-info")
public class BpmExecutionInfoRestService {

	@Inject
	private BpmExecutionInfoService bpmnManager;

	@GET
	@Path("/{fluxoName}")
	@Produces(MediaType.APPLICATION_JSON)
	@AccessTokenAuthentication(BPM_EXECUTION_INFO)
	public Map<String,Long> getBpmn(@PathParam("fluxoName") String fluxoName) {
		return ObjectUtils.defaultIfNull(bpmnManager.getTokensCount(fluxoName), new HashMap<String,Long>());
	}

}
