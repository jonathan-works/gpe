/**
 * 
 */
package br.com.infox.epp.certificado.manager;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.certificado.dao.CertificateSignatureGroupDAO;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.util.time.DateRange;

/**
 * @author erikliberal
 *
 */
@AutoCreate
@Name(CertificateSignatureGroupManager.NAME)
public class CertificateSignatureGroupManager extends Manager<CertificateSignatureGroupDAO, CertificateSignatureGroup> {

    private static final int TOKEN_LIFESPAN = 8;
    private static final long serialVersionUID = 1L;
    public static final String NAME = "certificateSignatureGroupManager";

    private boolean isTokenExpired(CertificateSignatureGroup group) {
        return new DateRange(group.getDataCriacao(), new Date()).get(DateRange.MINUTES) > TOKEN_LIFESPAN;
    }

    public CertificateSignatureGroup getByToken(String token) throws CertificateException {
        CertificateSignatureGroup group = getDao().getByToken(token);
        if (group == null) {
            throw new CertificateException("certificate.token.invalid");
        }
        if (isTokenExpired(group)) {
            throw new CertificateException("certificate.token.expired");
        }
        return group;
    }

    @Override
    public CertificateSignatureGroup persist(CertificateSignatureGroup o) throws DAOException {
        o.setDataCriacao(new Date());
        o.setStatus(CertificateSignatureGroupStatus.W);
        return super.persist(o);
    }

    /**
     * @param uuid
     * @return
     * @throws DAOException
     */
    public CertificateSignatureGroup createForToken(String uuid) throws DAOException {
        CertificateSignatureGroup certificateSignatureGroup = new CertificateSignatureGroup();
        certificateSignatureGroup.setToken(uuid);
        return persist(certificateSignatureGroup);
    }

    /**
     * @param token
     * @param bundle
     * @throws CertificateException
     * @throws DAOException 
     */
    public String updateBundleBean(CertificateSignatureBundleBean bundle) throws CertificateException, DAOException {
        String resultMessage = "";
        if (bundle == null){
            return "certificate.token.invalid";
        }
        CertificateSignatureGroup group = getDao().getByToken(bundle.getToken());
        if (group == null) {
            return "certificate.token.invalid";
            
        }
        CertificateSignatureGroupStatus groupStatus;
        if (isTokenExpired(group)) {
            resultMessage = "certificate.token.expired";
            groupStatus = CertificateSignatureGroupStatus.X;
        } else {
            CertificateSignatureBundleStatus status = bundle.getStatus();
            if (CertificateSignatureBundleStatus.ERROR.equals(status)
                    || CertificateSignatureBundleStatus.UNKNOWN.equals(status)) {
                resultMessage = "certificate.signature.failed";
                groupStatus = CertificateSignatureGroupStatus.E;
            } else {
                List<CertificateSignature> list = new ArrayList<>();
                for (CertificateSignatureBean bean : bundle.getSignatureBeanList()) {
                    CertificateSignature signature = new CertificateSignature();
                    signature.setCertificateChain(bean.getCertChain());
                    signature.setSignature(bean.getSignature());
                    signature.setSignedData(bean.getDocumentMD5());
                    signature.setUuid(bean.getDocumentUuid());
                    signature.setCertificateSignatureGroup(group);
                    list.add(signature);
                }
                group.setCertificateSignatureList(list);
                groupStatus = CertificateSignatureGroupStatus.S;
            }
        }
        group.setStatus(groupStatus);
        update(group);
        return resultMessage;
    }

}
