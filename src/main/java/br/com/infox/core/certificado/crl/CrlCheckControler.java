package br.com.infox.core.certificado.crl;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import br.com.infox.core.certificado.Certificado;
import br.com.infox.core.certificado.crl.jobs.CrlCertTestJob;
import br.com.infox.core.certificado.crl.jobs.CrlCertTestJobActionListner;
import br.com.infox.core.certificado.util.ValidatorUtilities;
import br.com.itx.component.MeasureTime;
import br.com.itx.util.ComponentUtil;

/**
 * Classe que controla a validação dos certificados juntos as listas de revogação 
 * fornecidas por suas autoridades certificadoras (o próprio certificado possui o link http para o download do arquivo crl).
 * O componente possui um map para fazer cache, assim depois que um certificado é testado, a lista de revogação da autoridade que o emitiu
 * fica neste map, de modo que o próxima verificação de certificado da mesma autoridade seja muito mais rápida, 
 * pois não será necessário donwload.
 * 
 * Como cada lista de revogação possui uma validade, uma thread verifica de tempos em tempos e atualiza as listas que estiverem expiradas.
 * 
 * Como a verificação pode demorar muito por causa do donwload o método aguarda um pouco pela resposta de verificação e
 * caso ultrapasse um tempo definido, é retornado warning de timeout, mas a Thread de verificação continua em background e aciona um listner
 * depois de finalizada a operação. 
 * Depois da verificação um listner é executado e caso o certificado esteja na lista de revogados a sessão do usuário é finalizada.
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
	
	private static int threadCount = 0;
	
	static {
		mapCrlCertObj = new HashMap<String, CrlCertObj>();
	}
	
	/**
	 * Metodo privado que a partir do certificado retorna o CrlCertObj de sua autoridade do map.
	 * Caso não exista um CrlCertObj de sua autoridade, ele é criado e adicionado no map.
	 * 
	 * Metodo é sincronizado para evitar a criação de 2 CrlCertObj equivalentes.
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
	 * Metodo que verifica se um certificado é valido. Ele inicia a verificação em outra Thread e espera
	 * um tempo pela resposta, caso não venha nesse tempo a thread segue em background e no final da operação executa o
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
	 * Metodo que inicia a Thread que é responsável iniciar a atualização das listas de revogação que estiverem expiradas.
	 */
	@Observer({"org.jboss.seam.postInitialization","org.jboss.seam.postReInitialization"})
	public void removeInvalidos() {
		Runnable runnable = new CrlCheckInvalidVerifier(INTERVALO_UPDATE, mapCrlCertObj);
		Thread t = new Thread(runnable, "CrlCheckControler.removeInvalidos");
		t.start();
	}
}