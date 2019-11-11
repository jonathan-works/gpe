package br.com.infox.core.log;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.cdi.util.Beans;

@Startup
@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class LogErroThreadExecutor implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @PostConstruct
    private void init() {
        Thread thread = new Thread(fileExecutor);
        thread.start();
    }
    
    
    private Runnable fileExecutor = new Runnable() {
        @Override
        public void run() {
            sleep(60);
            while (true) {
                EntityManagerProducer.clear();
                LogErrorService logErrorService = Beans.getReference(LogErrorService.class);
                try {
                    logErrorService.atualizarRegistroLogErro();
                } catch (IOException e) {
                }
                logErrorService.sendAllPendentesEnvio();
                sleep(300);
                EntityManagerProducer.clear();
            }
        }
        
        private void sleep(int seconds) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
            } 
        }
    };

}
