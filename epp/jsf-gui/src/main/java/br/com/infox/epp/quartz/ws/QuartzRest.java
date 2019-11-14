package br.com.infox.epp.quartz.ws;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path(QuartzRest.PATH)
public interface QuartzRest {
    
    public static final String PATH = "/quartz";
    
    @Path("/resource")
    public QuartzResource getQuartzResource(@HeaderParam("key") String key);
    

}
