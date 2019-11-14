package br.com.infox.certificado;

import java.security.cert.CertificateException;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.manager.CertificateSignatureGroupManager;

@Stateless
@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(CertificateSignatures.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CertificateSignatures {
	
    private static final LogProvider LOG = Logging.getLogProvider(CertificateSignatures.class);
    public static final String NAME = "certificateSignatures";

    @In
    private CertificateSignatureGroupManager certificateSignatureGroupManager;

    /**
     * @param group
     * @return
     * @throws CertificateException
     */
    private CertificateSignatureBundleBean groupToBean(CertificateSignatureGroup group) {
        CertificateSignatureBundleBean bundle = new CertificateSignatureBundleBean();
        CertificateSignatureBundleStatus status;
        switch (group.getStatus()) {
            case S:
                status = CertificateSignatureBundleStatus.SUCCESS;
                List<CertificateSignatureBean> signatureBeanList = bundle.getSignatureBeanList();
                for (CertificateSignature signature : group.getCertificateSignatureList()) {
                    signatureBeanList.add(certificateSignatureToBean(signature));
                }
                break;
            case E:
            case X:
                status = CertificateSignatureBundleStatus.ERROR;
                break;
            case W:
            case U:
            default:
                status = CertificateSignatureBundleStatus.UNKNOWN;
                break;
        }
        bundle.setStatus(status);
        return bundle;
    }

    private CertificateSignatureBean certificateSignatureToBean(CertificateSignature signature) {
        CertificateSignatureBean bean = new CertificateSignatureBean();
        bean.setCertChain(signature.getCertificateChain());
        bean.setSignature(signature.getSignature());
        bean.setDocumentMD5(signature.getSignedData());
        bean.setDocumentUuid(signature.getUuid());
        return bean;
    }

    public CertificateSignatureBundleBean get(String token) {
        CertificateSignatureBundleBean bean;
        try {
            CertificateSignatureGroup group = certificateSignatureGroupManager.getByToken(token);
            if (group == null) {
                throw new CertificateException("certificate.token.invalid");
            }
            bean = groupToBean(group);
        } catch (CertificateException e) {
            bean = new CertificateSignatureBundleBean();
            bean.setStatus(CertificateSignatureBundleStatus.ERROR);
            LOG.error("CertificateSignatures-RF00001", e);
        }
        return bean;
    }

    public void put(String token, CertificateSignatureBundleBean bundle) {
        try {
            certificateSignatureGroupManager.updateBundleBean(bundle);
        } catch (CertificateException | DAOException e) {
            LOG.error("CertificateSignatures-RF00002", e);
        }
    }

}
