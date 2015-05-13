package br.com.infox.cdi.producer;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;

import br.com.infox.epp.bpm.engine.service.EngineService;

public class EngineServiceProducer {
	
	@Produces
	@Resource(lookup = "java:/CamundaEngineService")
	private EngineService engineService;

}
