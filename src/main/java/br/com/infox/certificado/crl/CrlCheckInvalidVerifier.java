package br.com.infox.certificado.crl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

final class CrlCheckInvalidVerifier implements Runnable {
    
    private final int intervaloUpdate;
    private Map<String, CrlCertObj> mapCrlCertObj;
    private static final LogProvider LOG = Logging.getLogProvider(CrlCheckInvalidVerifier.class);
    

    public CrlCheckInvalidVerifier(int intervaloUpdate, Map<String, CrlCertObj> mapCrlCertObj) {
        super();
        this.intervaloUpdate = intervaloUpdate;
        this.mapCrlCertObj = mapCrlCertObj;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(intervaloUpdate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                executaVerificacao();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void executaVerificacao() {
        synchronized (mapCrlCertObj) {
            Set<Entry<String,CrlCertObj>> entrySet = mapCrlCertObj.entrySet();
            List<String> keysToUpdate = new ArrayList<String>();
            for (Entry<String, CrlCertObj> entry : entrySet) {
                CrlCertObj crlCertObj = entry.getValue();
                if (crlCertObj.isExpirado()) {
                    keysToUpdate.add(entry.getKey());
                } else {
                    LOG.info("A CRL " + entry.getKey() + " expira em " + crlCertObj.getDataExpiracao());
                }
            }
            for (String key : keysToUpdate) {
                LOG.info("Atualizando a Crl: " + key);
                CrlCertUpdatetJob updatetJob = new CrlCertUpdatetJob(mapCrlCertObj.get(key));
                Thread t = new Thread(updatetJob, "CrlCertUpdatetJob: " + key);
                t.start();
            }
        }
    }

}
