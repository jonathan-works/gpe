package br.com.infox.jsf.events;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import br.com.infox.core.messages.InfoxMessagesLoader;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.classesautomaticas.CriacaoHistoricoMetadado;
import br.com.infox.epp.classesautomaticas.EppSystemEventListenerExtended;
import br.com.infox.epp.classesautomaticas.MigraTaskExpirationToTimer;
import br.com.infox.epp.processo.metadado.system.MetadadoLabelLoader;

public class EppSystemEventListener implements SystemEventListener {
	
    @Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
	    InfoxMessagesLoader infoxMessagesLoader = BeanManager.INSTANCE.getReference(InfoxMessagesLoader.class);
	    MetadadoLabelLoader metadadoLabelLoader = BeanManager.INSTANCE.getReference(MetadadoLabelLoader.class);
	    EppSystemEventListenerExtended startupExtender = BeanManager.INSTANCE.getReference(EppSystemEventListenerExtended.class);
	    try {
	        infoxMessagesLoader.loadMessagesProperties();
	        metadadoLabelLoader.loadMetadadosMessagesProperties();
		} catch (Exception e) {
	        throw new AbortProcessingException(e);
	    }
	    
	    try {
			startupExtender.runProcess();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AbortProcessingException("Erro ao gerar um processo de inicializacao extendido", e);
		}
	    
	    migraTaskExpirationToTimer();
	    criarHistoricoDeMetadado();
	}

    private void migraTaskExpirationToTimer() {
        try {
	        MigraTaskExpirationToTimer.instance().init();
	    } catch (Exception e) {
            throw new AbortProcessingException(e);
        }
    }

	private void criarHistoricoDeMetadado() {
	    try{
	        CriacaoHistoricoMetadado.instance().init();
	    } catch (Exception e) {
            throw new AbortProcessingException(e);
        }
	}
	
	@Override
	public boolean isListenerForSource(Object source) {
		return source instanceof Application;
	}

}
