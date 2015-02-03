/**
 * 
 */
package br.com.infox.epp.certificado.enums;

import br.com.infox.core.type.Displayable;

/**
 * @author erikliberal
 *
 */
public enum CertificateSignatureGroupStatus implements Displayable {
    E("certificateSignatureGroup.error"), S("certificateSignatureGroup.success"), U("certificateSignatureGroup.unknown"), W(
            "certificateSignatureGroup.waiting"), X("certificateSignatureGroup.expired");

    private String label;

    CertificateSignatureGroupStatus(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
