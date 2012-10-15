package br.com.infox.core.certificado;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import br.com.infox.core.certificado.util.DigitalSignatureUtils;

public class DadosCertificado {

	public static final String PJ_NOME = "PJ_NOME";
	public static final String PJ_INSS = "PJ_INSS";
	public static final String PJ_CNPJ = "PJ_CNPJ";
	public static final String PJ_RESPONSAVEL = "PJ_RESPONSAVEL";
	public static final String MUNICIPIO = "MUNICIPIO";
	public static final String SECAO = "SECAO";
	public static final String ZONA_ELEITORAL = "ZONA_ELEITORAL";
	public static final String TITULO_DE_ELEITOR = "TITULO_DE_ELEITOR";
	public static final String INSS = "INSS";
	public static final String ORGAO_EXPEDIDOR = "ORGAO_EXPEDIDOR";
	public static final String RG = "RG";
	public static final String NIS = "NIS";
	public static final String CPF = "CPF";
	public static final String DATA_NASCIMENTO = "DT_NASCIMENTO";
	public static final String UF = "UF"; 
	
	private static final DERObjectIdentifier OID_PF_DADOS_TITULAR = new DERObjectIdentifier("2.16.76.1.3.1");  
	private static final DERObjectIdentifier OID_PJ_RESPONSAVEL = new DERObjectIdentifier("2.16.76.1.3.2");  
	private static final DERObjectIdentifier OID_PJ_CNPJ = new DERObjectIdentifier("2.16.76.1.3.3");  
	private static final DERObjectIdentifier OID_PJ_DADOS_RESPONSAVEL = new DERObjectIdentifier("2.16.76.1.3.4");  
	private static final DERObjectIdentifier OID_PF_ELEITORAL = new DERObjectIdentifier("2.16.76.1.3.5");  
	private static final DERObjectIdentifier OID_PF_INSS = new DERObjectIdentifier("2.16.76.1.3.6");  
	private static final DERObjectIdentifier OID_PJ_INSS = new DERObjectIdentifier("2.16.76.1.3.7");  
	private static final DERObjectIdentifier OID_PJ_NOME_EMPRESARIAL = new DERObjectIdentifier("2.16.76.1.3.8");
	
	private Map<String, String> dados;
	
	private DadosCertificado() { }
	
	public static DadosCertificado parse(Certificado certificado) throws CertificadoException {
		return parse(certificado.getMainCertificate());
	}	
	
	public static DadosCertificado parse(String certBase64) throws CertificadoException {
		return parse(DigitalSignatureUtils.loadCertFromBase64String(certBase64)[0]);
	}
	
	public Set<Entry<String, String>> getEntrySetDados() {
		return dados.entrySet();
	}
	
	public static DadosCertificado parse(X509Certificate cert) throws CertificadoException {  
		DadosCertificado dadosCertificado = new DadosCertificado();
		dadosCertificado.dados = new HashMap<String, String>();
		try {  

			Collection<?> col = X509ExtensionUtil.getSubjectAlternativeNames(cert);

			for (Object obj : col) {  

				if (obj instanceof ArrayList<?>) {  

					ArrayList<?> lst = (ArrayList<?>) obj;  

					Object value = lst.get(1);  

					if (value instanceof DERSequence) {  

						/** 
						 * DER Sequence 
						 *      ObjectIdentifier 
						 *      Tagged 
						 *          DER Octet String  
						 */  
						DERSequence seq = (DERSequence) value;  

						DERObjectIdentifier oid = (DERObjectIdentifier) seq.getObjectAt(0);  
						DERTaggedObject tagged = (DERTaggedObject) seq.getObjectAt(1);  
						String info = null;  

						DERObject derObj = tagged.getObject();  



						if (derObj instanceof DEROctetString) {  
							DEROctetString octet = (DEROctetString) derObj;  
							info = new String(octet.getOctets());  
						} else if (derObj instanceof DERPrintableString) {  
							DERPrintableString octet = (DERPrintableString) derObj;  
							info = new String(octet.getOctets());  
						} else if (derObj instanceof DERUTF8String) {  
							DERUTF8String str = (DERUTF8String) derObj;  
							info = str.getString();  
						}  

						if (info != null && !info.isEmpty()) {  
							if (oid.equals(DadosCertificado.OID_PF_DADOS_TITULAR) || oid.equals(DadosCertificado.OID_PJ_DADOS_RESPONSAVEL)) {  
								String nascimento = info.substring(0, 8);  
								dadosCertificado.dados.put(DATA_NASCIMENTO, nascimento);
								String cpf = info.substring(8, 19);  
								dadosCertificado.dados.put(CPF, cpf);
								String nis = info.substring(19, 30);  
								dadosCertificado.dados.put(NIS, nis);
								String rg = info.substring(30, 45);  
								dadosCertificado.dados.put(RG, rg);
								if (!rg.equals("000000000000000")) {  
									String ufExp = info.substring(45, 50);  
									dadosCertificado.dados.put(ORGAO_EXPEDIDOR, ufExp);
								}  
							} else if (oid.equals(DadosCertificado.OID_PF_INSS)) {  
								String inss = info.substring(0, 12);  
								dadosCertificado.dados.put(INSS, inss);
							} else if (oid.equals(DadosCertificado.OID_PF_ELEITORAL)) {  
								String titulo = info.substring(0, 12);  
								dadosCertificado.dados.put(TITULO_DE_ELEITOR, titulo);
								String zona = info.substring(12, 15);  
								dadosCertificado.dados.put(ZONA_ELEITORAL, zona);
								String secao = info.substring(15, 19);  
								dadosCertificado.dados.put(SECAO, secao);
								if (!titulo.equals("000000000000")) {  
									String municipio = info.substring(19);  
									dadosCertificado.dados.put(MUNICIPIO, municipio);
								}  
							} else if (oid.equals(DadosCertificado.OID_PJ_RESPONSAVEL)) {  
								dadosCertificado.dados.put(PJ_RESPONSAVEL, info);
							} else if (oid.equals(DadosCertificado.OID_PJ_CNPJ)) {  
								dadosCertificado.dados.put(PJ_CNPJ, info);
							} else if (oid.equals(DadosCertificado.OID_PJ_INSS)) {  
								dadosCertificado.dados.put(PJ_INSS, info);
							} else if (oid.equals(DadosCertificado.OID_PJ_NOME_EMPRESARIAL)) {  
								dadosCertificado.dados.put(PJ_NOME, info);
							} else {
								System.out.println(oid + " - " + info);
							}
						}  
					} 
				}  

			}  
			for (Entry<String, String> entry : dadosCertificado.dados.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		} catch (Exception e) {  
			e.printStackTrace();
			throw new CertificadoException(e.getMessage());
		} 
		return dadosCertificado;
	}  	
	
	public String getValor(String campo) {
		return dados.get(campo);
	}
	
	public String toStringDebug(boolean separadorHtml) {
		StringBuilder sb = new StringBuilder();
		Set<Entry<String,String>> entrySet = dados.entrySet();
		for (Entry<String, String> entry : entrySet) {
			sb.append(entry.getKey()).append(": ");
			sb.append(entry.getValue()).append(separadorHtml ? "<br/>" : "\n");
		}
		return sb.toString();
	}
}