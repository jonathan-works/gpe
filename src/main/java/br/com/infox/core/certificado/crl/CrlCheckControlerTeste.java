package br.com.infox.core.certificado.crl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.certificado.Certificado;
import br.com.infox.core.certificado.CertificadoException;
import br.com.itx.util.EntityUtil;

@BypassInterceptors
@Name("crlCheckControlerTeste")
@Scope(ScopeType.EVENT)
public class CrlCheckControlerTeste {

	private CrlCheckControler crlCheckControler;
	private List<UsuarioLogin> users;
	private int numeroVerificacoes = 0;
	private static SimpleDateFormat sf = new SimpleDateFormat("[yyyy-MM-dd mm:ss:SSS]: ");
	private static StringBuffer sbLogErros = new StringBuffer();
	
	public static void main(String[] args) {
		System.out.println(sf.format(new Date()));
	}
	
	public void teste() {
		System.out.println("Iniciando a o teste de stress.");
		while(true) {
			excecutaTeste();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void excecutaTeste() {
		if (users == null) {
			final String hql = "select o from UsuarioLogin o where o.certChain is not null and o.certChain != ''";
			final Query query = EntityUtil.getEntityManager().createQuery(hql);
			users = query.getResultList();
			crlCheckControler = CrlCheckControler.instance();
		}
		
		
		int i = 0;
		for (final UsuarioLogin usuarioLogin : users) {
			numeroVerificacoes++;
			try {
				final Certificado c = new Certificado(usuarioLogin.getCertChain());
				final CheckJob job = new CheckJob();
				job.c = c;
				final Thread t = new Thread(job , "teste_cert_" + i);
				t.start();
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (CertificadoException e) {
				e.printStackTrace();
			} 
			if (numeroVerificacoes % 1000 == 0) {
				System.out.println("Execuatadas " + numeroVerificacoes + " verificações");
			}
		}
	}
	
	public class CheckJob implements Runnable {
		
		private Certificado c;

		public void run() {
			
			try {
				crlCheckControler.isCertificadoRevogado(c, null);
			} catch (CrlCheckException e) {
				sbLogErros.append(sf.format(new Date()));
				String erro = e.getMessage();
				sbLogErros.append(erro).append('\n');
				if (!erro.startsWith("Time out")) {
					System.out.println(erro);
				}
			}
			
		}
		
	}
	
}
