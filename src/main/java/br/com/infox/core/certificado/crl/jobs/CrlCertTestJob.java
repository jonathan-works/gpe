package br.com.infox.core.certificado.crl.jobs;
import java.text.MessageFormat;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.certificado.Certificado;
import br.com.infox.core.certificado.crl.CrlCertObj;
import br.com.infox.core.certificado.crl.CrlCheckException;
import br.com.itx.component.MeasureTime;

	/**
	 * Classe Runnable responsavel por fazer a verificação de revogação do certificado utilizando o objeto CrlCertObj.
	 * 
	 * @author rodrigo
	 *
	 */
	public class CrlCertTestJob implements Runnable {
		
		private static final LogProvider log = Logging.getLogProvider(CrlCertTestJob.class);

		private boolean revoked;
		private Certificado certificado;
		private CrlCertObj crlThreadCheck;
		
		private CrlCertTestJobActionListner jobActionListner;
		
		public void setJobActionListner(CrlCertTestJobActionListner jobActionListner) {
			this.jobActionListner = jobActionListner;
		}

		public CrlCertTestJob(Certificado certificado, CrlCertObj crlThreadCheck) {
			this.certificado = certificado;
			this.crlThreadCheck = crlThreadCheck;
		}

		public boolean isRevoked() {
			return revoked;
		}

		@Override
		public void run() {
			try {
				MeasureTime mt = new MeasureTime(true);
				revoked = crlThreadCheck.isCertificadoRevogado(certificado.getMainCertificate());
				log.info(MessageFormat.format(
						"Verificada a revogação do certificado {0}. ({1} ms)",
						certificado.getCn(), mt.getTime()));
				if (jobActionListner != null) {
					jobActionListner.execute(revoked);
				}
			} catch (CrlCheckException e) {
				throw new RuntimeException(e);
			}			

		}

	}