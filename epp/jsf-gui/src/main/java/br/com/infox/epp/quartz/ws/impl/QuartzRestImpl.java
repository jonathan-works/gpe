package br.com.infox.epp.quartz.ws.impl;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.epp.quartz.ws.QuartzResource;
import br.com.infox.epp.quartz.ws.QuartzRest;

public class QuartzRestImpl implements QuartzRest {
    
    @Inject
    private RequestInternalPageService requestInternalPageService;
    @Inject
    private QuartzResourceImpl quartzResourceImpl;

    @Override
    public QuartzResource getQuartzResource(String key) {
        if (!requestInternalPageService.isValid(key)) {
            throw new WebApplicationException(401);
        }
        return quartzResourceImpl;
    }
    
}
