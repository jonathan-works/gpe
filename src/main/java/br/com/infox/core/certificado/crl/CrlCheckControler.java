package br.com.infox.core.certificado.crl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.certificado.Certificado;
import br.com.infox.core.certificado.crl.jobs.CrlCertTestJob;
import br.com.infox.core.certificado.crl.jobs.CrlCertTestJobActionListner;
import br.com.infox.core.certificado.util.ValidatorUtilities;
import br.com.itx.component.MeasureTime;
import br.com.itx.util.ComponentUtil;

/**
 * Classe que controla a valida��o dos certificados juntos as listas de revoga��o 
 * fornecidas por suas autoridades certificadoras (o pr�prio certificado possui o link http para o download do arquivo crl).
 * O componente possui um map para fazer cache, assim depois que um certificado � testado, a lista de revoga��o da autoridade que o emitiu
 * fica neste map, de modo que o pr�xima verifica��o de certificado da mesma autoridade seja muito mais r�pida, 
 * pois n�o ser� necess�rio donwload.
 * 
 * Como cada lista de revoga��o possui uma validade, uma thread verifica de tempos em tempos e atualiza as listas que estiverem expiradas.
 * 
 * Como a verifica��o pode demorar muito por causa do donwload o m�todo aguarda um pouco pela resposta de verifica��o e
 * caso ultrapasse um tempo definido, � retornado warning de timeout, mas a Thread de verifica��o continua em background e aciona um listner
 * depois de finalizada a opera��o. 
 * Depois da verifica��o um listner � executado e caso o certificado esteja na lista de revogados a sess�o do usu�rio � finalizada.
 * 
 * @author Rodrigo Menezes
 *
 */
@BypassInterceptors
@Name(CrlCheckControler.NAME)
@Scope(ScopeType.APPLICATION)
public class CrlCheckControler {
	
	public static final String NAME = "crlCheckControler";

	private static Map<String, CrlCertObj> mapCrlCertObj;
	
	private static final int INTERVALO_UPDATE = 10 * 60 * 1000; 
	
	private static final LogProvider log = Logging.getLogProvider(CrlCheckControler.class);
	
	private static int threadCount = 0;
	
	static {
		mapCrlCertObj = new HashMap<String, CrlCertObj>();
	}
	
	/**
	 * Metodo privado que a partir do certificado retorna o CrlCertObj de sua autoridade do map.
	 * Caso n�o exista um CrlCertObj de sua autoridade, ele � criado e adicionado no map.
	 * 
	 * Metodo � sincronizado para evitar a cria��o de 2 CrlCertObj equivalentes.
	 * @param certificado
	 * @return
	 */
	private synchronized CrlCertObj getCrlCertObj(Certificado certificado) {
		X509Certificate certificate = certificado.getMainCertificate();
		String idCertificadora = certificado.getNomeCertificadora();
		CrlCertObj revocateList = getMapCrlCertObj().get(idCertificadora);
		if (revocateList == null) {
			List<String> urls = ValidatorUtilities.getCRLDistUrls(certificate);
			revocateList = new CrlCertObj(idCertificadora, urls);
			getMapCrlCertObj().put(idCertificadora, revocateList);
		}
		return revocateList;
	}
	
	/**
	 * Metodo que verifica se um certificado � valido. Ele inicia a verifica��o em outra Thread e espera
	 * um tempo pela resposta, caso n�o venha nesse tempo a thread segue em background e no final da opera��o executa o
	 * actionListner que foi passado.
	 * @param certificado
	 * @param actionListner
	 * @return
	 * @throws CrlCheckException
	 */
	public boolean isCertificadoRevogado(Certificado certificado, CrlCertTestJobActionListner actionListner) throws CrlCheckException {
		int timeout = 10 * 1000;
		
		CrlCertTestJob certTest = new CrlCertTestJob(certificado, getCrlCertObj(certificado));
		
		certTest.setJobActionListner(actionListner);
		
		Thread t = new Thread(certTest, "crlCertTestJob_" + threadCount++);
		t.start();
		
		MeasureTime mt = new MeasureTime(true);
		
		while (t.isAlive()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) { 
				e1.printStackTrace();
			}			
			if (mt.getTime() >= timeout) {
				throw new CrlCheckException("Time out: " + timeout + " ms.");
			}
		}
		
		return certTest.isRevoked();
	}	
	
	public synchronized Map<String, CrlCertObj> getMapCrlCertObj() {
		if (mapCrlCertObj == null) {
			mapCrlCertObj = new HashMap<String, CrlCertObj>();
		}
		return mapCrlCertObj;
	}	

	public static CrlCheckControler instance() {
		return ComponentUtil.getComponent(NAME);	
	}
	
	/**
	 * Metodo que inicia a Thread que � respons�vel iniciar a atualiza��o das listas de revoga��o que estiverem expiradas.
	 */
	@Observer({"org.jboss.seam.postInitialization","org.jboss.seam.postReInitialization"})
	public void removeInvalidos() {
		Runnable runnable = new Runnable() {
			
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(INTERVALO_UPDATE);
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
							log.info("A CRL " + entry.getKey() + " expira em " + crlCertObj.getDataExpiracao());
						}
					}
					for (String key : keysToUpdate) {
						log.info("Atualizando a Crl: " + key);
						CrlCertUpdatetJob updatetJob = new CrlCertUpdatetJob(mapCrlCertObj.get(key));
						Thread t = new Thread(updatetJob, "CrlCertUpdatetJob: " + key);
						t.start();
					}
				}
			}
		};
		
		Thread t = new Thread(runnable, "CrlCheckControler.removeInvalidos");
		t.start();
	}
	

	
	public class CrlCertUpdatetJob implements Runnable {

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
				log.warn(e.getMessage(), e);
			}
		}
		
	}
}