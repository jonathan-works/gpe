package br.com.infox.epp.classesautomaticas;

import java.util.Iterator;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public class EppSystemEventListenerExtended {

	@Inject
	@Any
	private Instance<EppStartupListenerExtended> implementationList;
	
	public void runProcess() throws Exception {
		Iterator<EppStartupListenerExtended> list = implementationList.iterator();
	    while(list.hasNext()){
	    	EppStartupListenerExtended item = list.next();
	    	item.processInit();
	    }
	}
	
}
