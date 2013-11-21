package br.com.infox.certificado.crl;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

final class CrlCertUpdatetJob implements Runnable {

    private static final LogProvider LOG = Logging.getLogProvider(CrlCertUpdatetJob.class);
    private CrlCertObj crlCertObj;
	
	public CrlCertUpdatetJob(CrlCertObj crlCertObj) {
		super();
		this.crlCertObj = crlCertObj;
	}

	@Override
	public void run() {
		try {
			crlCertObj.atualizarX509crl();
		} catch (CrlCheckException e) {
			LOG.warn(e.getMessage(), e);
		}
	}
	
}