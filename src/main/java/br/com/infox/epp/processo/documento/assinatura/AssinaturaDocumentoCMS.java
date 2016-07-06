package br.com.infox.epp.processo.documento.assinatura;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.persistence.Transient;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;


public class AssinaturaDocumentoCMS {
    private byte[] dadosAssinados;
    private String signature;
    
    @Transient
    private CMSSignedData signedData;
    
    public AssinaturaDocumentoCMS(byte[] dadosAssinados, String signature) {
        this.dadosAssinados = dadosAssinados;
        this.signature = signature;
    }
    
    public byte[] getDadosAssinados() {
        return dadosAssinados;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public CMSSignedData getSignedData() {
        if (signedData == null){
            String signature = getSignature();
            byte[] decodeBase64 = Base64.decodeBase64(signature);
            try {
                signedData = new CMSSignedData(new CMSProcessableByteArray(getDadosAssinados()), new ByteArrayInputStream(decodeBase64));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return signedData;
    }
    
    public SignerInformation getSignerInformation(){
        SignerInformationStore signerInformationStore = getSignedData().getSignerInfos();
        return (SignerInformation) signerInformationStore.getSigners().iterator().next();
    }
    
    public Map<String,Object> getSignedAttributes(){
        AttributeTable signedAttributesTable = getSignerInformation().getSignedAttributes();
        for (Object object : signedAttributesTable.toHashtable().entrySet()) {
            System.out.println(object);
        }
        return null;
    }
    
    public Date getCMSSigningTime(){
        try {
            Attribute signingTime = getSignerInformation().getSignedAttributes().get(CMSAttributes.signingTime);
            ASN1Encodable[] signingTimeEncodables = signingTime.getAttrValues().toArray();
            ASN1UTCTime signingTimeValue = (ASN1UTCTime) new DERSet(signingTimeEncodables).getObjectAt(0);
            return signingTimeValue.getAdjustedDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
