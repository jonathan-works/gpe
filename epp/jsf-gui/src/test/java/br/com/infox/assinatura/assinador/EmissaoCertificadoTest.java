package br.com.infox.assinatura.assinador;

import static org.junit.Assert.assertEquals;

import java.security.cert.X509Certificate;
import java.util.Date;

import org.junit.Test;

import br.com.infox.certificado.CertificadoGenerico;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.epp.certificadoeletronico.builder.CertBuilder;
import br.com.infox.epp.certificadoeletronico.builder.CertUtil;
import br.com.infox.epp.certificadoeletronico.builder.CertificadoDTO;
import br.com.infox.epp.certificadoeletronico.builder.RootCertBuilder;

public class EmissaoCertificadoTest {

	@Test
	public void testeConteudoCertificado() throws CertificadoException {
		CertificadoDTO raiz = new RootCertBuilder("CN=Infox, O=Infox, C=BR, ST=SE").gerar();
		String paramNome = "Usuário anônimo".toUpperCase();
		String paramCPF = "47416657040";
		Date paramDataNascimento = new Date(1970, 9, 15);
		CertificadoDTO certGerado = new CertBuilder(new CertBuilder.CertBuilderDTO(raiz.getCert(), raiz.getKey(),
				paramNome, paramCPF, paramDataNascimento, "hashdesenha")).gerar();

		X509Certificate[] certChain = { CertUtil.getCertificate(certGerado.getCert()),
				CertUtil.getCertificate(raiz.getCert())};
		CertificadoGenerico certificadoGenerico = new CertificadoGenerico(certChain);
		String nome = certificadoGenerico.getNome();
		String cpf = certificadoGenerico.getCPF();
		Date dataNascimento = certificadoGenerico.getDataNascimento();
		assertEquals(paramCPF, cpf);
		assertEquals(paramNome, nome);
		assertEquals(paramDataNascimento, dataNascimento);
	}

}
