package br.com.infox.assinatura.assinador;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import br.com.infox.assinatura.PoliticaAssinatura;
import br.com.infox.assinatura.PoliticaAssinaturaFactory;
import br.com.infox.assinatura.assinador.impl.AssinadorFactory;
import br.com.infox.assinatura.assinador.signable.SimpleSignableIO;
import br.com.infox.assinatura.icpBrasil.politicas.PoliticaAssinaturaICPBrasilCMSv2_1;
import lombok.AllArgsConstructor;

@RunWith(Parameterized.class)
@AllArgsConstructor
public class TesteAssinaturaCMS {
	private static final String BASE_PATH = "certificados";
	private static final String PATH_EXPIRADOS = "expirados";
	private static final String PATH_VALIDOS = "validos";
	private static final String PATH_INVALIDOS = "invalidos";

	@BeforeClass
	public static void beforeTest() {
		System.setProperty("log4j.configurationFile",
				System.getProperty("log4j.configurationFile", "META-INF/log4j.xml"));
	}

	static InputStream certificadoValido(String name) {
		String filePath = String.format("%s/%s/%s", BASE_PATH, PATH_VALIDOS, name);
		return getInputStreamFromFilePath(filePath);
	}

	static InputStream certificadoInvalido(String name) {
		String filePath = String.format("%s/%s/%s", BASE_PATH, PATH_INVALIDOS, name);
		return getInputStreamFromFilePath(filePath);
	}

	static InputStream certificadoExpirado(String name) {
		String filePath = String.format("%s/%s/%s", BASE_PATH, PATH_EXPIRADOS, name);
		return getInputStreamFromFilePath(filePath);
	}

	private static InputStream getInputStreamFromFilePath(String filePath) {
		System.out.println(getClassLoader().getResource(filePath).getFile());
		return getClassLoader().getResourceAsStream(filePath);
	}

	private static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	private void assertAssinatura(String errorMessage, boolean expected, byte[] data, byte[] signature) {
		PoliticaAssinaturaICPBrasilCMSv2_1 verificador = new PoliticaAssinaturaICPBrasilCMSv2_1();
		try {
			verificador.validate(data, signature);
			if (expected) {
				return;
			}
		} catch (Exception e) {
			if (!expected) {
				return;
			}
		}
		Assert.fail(errorMessage);
	}

	private byte[] scrambleData(byte[] data) {
		return (new String(data, StandardCharsets.UTF_8) + "fas").getBytes(StandardCharsets.UTF_8);
	}
	private PrivateKey generatePrivateKey() {
        PrivateKey key;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            key = keyPair.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
            key=null;
        }
        return key;
    }
	@Parameterized.Parameters(name = "{index}: Certificado[{0}]")
	public static Iterable<Object[]> data() {
		List<Object[]> testCases = new ArrayList<>();
		byte[] signableContent = "content".getBytes(StandardCharsets.UTF_8);
		String name = "certChain.crt";
		testCases.add(new Object[] { name, signableContent });
		return Collections.unmodifiableList(testCases);
	}

	private String nomeDoCertificado;
	private byte[] data;

	private Certificate[] loadCertificateChain(InputStream inputStream) {
		try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
			return (Certificate[]) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Test
	public void testaAssinaturaDeveSerValidaSemModificacaoConteudo() {
		PoliticaAssinatura politica = resolvePolitica();
		
		Assinador assinador = AssinadorFactory.getDefault().setPoliticaAssinatura(politica)
				.setPrivateKey(generatePrivateKey())
				.setCertificateChain(loadCertificateChain(certificadoExpirado(nomeDoCertificado)));

		SimpleSignableIO dataIO = new SimpleSignableIO(data, false, false).signWith(assinador);
		assertAssinatura("Assinatura " + nomeDoCertificado + " foi inválida", true, data, dataIO.getSignature());
	}

	@Test
	public void testaAssinaturaDeveSerInvalidaComModificacaoConteudo() {
		PoliticaAssinatura politica = resolvePolitica();

		Assinador assinador = AssinadorFactory.getDefault().setPoliticaAssinatura(politica)
				.setPrivateKey(generatePrivateKey())
				.setCertificateChain(loadCertificateChain(certificadoExpirado(nomeDoCertificado)));

		SimpleSignableIO dataIO = new SimpleSignableIO(data, false, false).signWith(assinador);
		assertAssinatura("Assinatura " + nomeDoCertificado + " foi válida", false, scrambleData(data),
				dataIO.getSignature());
	}

	private PoliticaAssinatura resolvePolitica() {
		return PoliticaAssinaturaFactory.getDefault()
				.fromOID(PoliticaAssinatura.AD_RB_CMS_V_2_1);
	}

	@Test
	public void testaAssinaturaSobreHashDeveSerValidaSemModificacaoConteudo() {
		PoliticaAssinatura politica = resolvePolitica();
		byte[] data = politica.getSignatureAlgorithm().getDigestAlgorithm().digest(this.data);

		Assinador assinador = AssinadorFactory.getDefault().setPoliticaAssinatura(politica)
				.setPrivateKey(generatePrivateKey())
				.setCertificateChain(loadCertificateChain(certificadoExpirado(nomeDoCertificado)));

		SimpleSignableIO dataIO = new SimpleSignableIO(data, true, false).signWith(assinador);
		assertAssinatura("Assinatura do hash " + nomeDoCertificado + " foi inválida", true, this.data,
				dataIO.getSignature());

	}

	@Test
	public void testaAssinaturaSobreHashDeveSerInvalidaComModificacaoConteudo() {
		PoliticaAssinatura politica = resolvePolitica();
		byte[] data = politica.getSignatureAlgorithm().getDigestAlgorithm().digest(this.data);

		Assinador assinador = AssinadorFactory.getDefault().setPoliticaAssinatura(politica)
				.setPrivateKey(generatePrivateKey())
				.setCertificateChain(loadCertificateChain(certificadoExpirado(nomeDoCertificado)));

		SimpleSignableIO dataIO = new SimpleSignableIO(data, true, false).signWith(assinador);
		assertAssinatura("Assinatura do hash " + nomeDoCertificado + " foi válida", false, scrambleData(this.data),
				dataIO.getSignature());
	}

}
