package br.com.infox.epp.assinador;

import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertPathValidatorException.Reason;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.seam.util.Strings;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.CertificadoDadosPessoaFisica;
import br.com.infox.certificado.CertificadoFactory;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.epp.assinador.assinavel.TipoSignedData;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException.Motivo;
import br.com.infox.epp.system.util.ParametroUtil;

public class ValidadorAssinaturaPadrao implements ValidadorAssinatura, ValidadorUsuarioCertificado {

	@Inject
	private CertificateValidator certificateValidator;
	@Inject
	private CMSAdapter cmsAdapter;
	@Inject
	private PessoaFisicaManager pessoaFisicaManager;

	@Inject
	private Logger logger;

    private PessoaFisica getPessoaFisicaFromCertChain(String certChain) throws CertificadoException {
        Certificado c = CertificadoFactory.createCertificado(certChain);
        String cpf = new StringBuilder(((CertificadoDadosPessoaFisica) c).getCPF()).insert(9, '-').insert(6, '.').insert(3, '.').toString();
        if (cpf != null) {
            return pessoaFisicaManager.getByCpf(cpf);
        }
        return null;
    }
    
	@Override
    public void verificaCertificadoUsuarioLogado(String certChainBase64Encoded, PessoaFisica pessoaFisica) throws AssinaturaException {
		try {
			if (Strings.isEmpty(certChainBase64Encoded)) {
				throw new AssinaturaException(Motivo.SEM_CERTIFICADO);
			}
			if (pessoaFisica == null) {
				throw new AssinaturaException(Motivo.USUARIO_SEM_PESSOA_FISICA);
			}
			if (ParametroUtil.isValidaCpfAssinatura()) {
			    if (Strings.isEmpty(pessoaFisica.getCertChain())) {
				final Certificado certificado = CertificadoFactory.createCertificado(certChainBase64Encoded); 
				if (!(certificado instanceof CertificadoDadosPessoaFisica)) {
					throw new CertificadoException("Este certificado não é de pessoa física");
				}
				final String cpfCertificado = ((CertificadoDadosPessoaFisica) certificado).getCPF();
				if (cpfCertificado.equals(pessoaFisica.getCpf().replace(".", "").replace("-", ""))) {
					pessoaFisica.setCertChain(certChainBase64Encoded);
				} else {
					throw new AssinaturaException(Motivo.CADASTRO_USUARIO_NAO_ASSINADO);
				}
			    }
    			    PessoaFisica pessoaFisicaCertificado = getPessoaFisicaFromCertChain(certChainBase64Encoded);
    			    if (!pessoaFisica.equals(pessoaFisicaCertificado)) {
    				throw new AssinaturaException(Motivo.CPF_CERTIFICADO_DIFERENTE_USUARIO);
    			    }
    			    if (!pessoaFisicaCertificado.checkCertChain(certChainBase64Encoded)) {
    			        throw new AssinaturaException(Motivo.CERTIFICADO_USUARIO_DIFERENTE_CADASTRO);
    			    }
			}
		}
		catch(CertificadoException e) {
			throw new AssinaturaException(e);
		}
    }
    
	public void validarCertificado(List<X509Certificate> x509Certificates) throws CertificadoException {
		try {
			certificateValidator.validarCertificado(x509Certificates);
		} catch (CertPathValidatorException e) {
			Reason reason = e.getReason();
			if (reason == BasicReason.EXPIRED) {
				logger.log(Level.WARNING, "Certificado expirado", e);
				if (ParametroUtil.isProducao()) {
				    logger.log(Level.SEVERE, "Certificado expirado", e);
					throw new CertificadoException("Certificado expirado. ");
				}
			} else if (reason == BasicReason.NOT_YET_VALID) {
				logger.log(Level.WARNING, "Certificado ainda não válido", e);
				if (ParametroUtil.isProducao()) {
				    logger.log(Level.SEVERE, "Certificado ainda não válido", e);
					throw new CertificadoException("O certificado ainda não está válido. ");
				}
			} else {
				logger.log(Level.WARNING, "Erro ao verificar a validade do certificado", e);
				if (ParametroUtil.isProducao()) {
				    logger.log(Level.SEVERE, "Erro ao verificar a validade do certificado", e);
					throw new CertificadoException(e.getMessage());
				}
			}
		}
	}

	@Override
	public void validarAssinatura(byte[] signedData, TipoSignedData tipoSignedData, byte[] signature) throws AssinaturaException {
		DadosAssinaturaLegada dadosAssinaturaLegada = cmsAdapter.convert(signature);
		try {
			validarCertificado(dadosAssinaturaLegada.getCertChain());
			cmsAdapter.validarAssinatura(signedData, tipoSignedData, signature);
		} catch (CertificadoException e) {
			throw new AssinaturaException(e);
		}
	}

	@Override
	public void validarAssinatura(byte[] signedData, TipoSignedData tipoSignedData, byte[] signature, PessoaFisica pessoaFisica) throws AssinaturaException {
		DadosAssinaturaLegada dadosAssinaturaLegada = cmsAdapter.convert(signature);
		String certChainBase64 = dadosAssinaturaLegada.getCertChainBase64();
		verificaCertificadoUsuarioLogado(certChainBase64, pessoaFisica);
		validarAssinatura(signedData, tipoSignedData, signature);
	}

}
