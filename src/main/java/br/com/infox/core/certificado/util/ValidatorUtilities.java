package br.com.infox.core.certificado.util;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;

import br.com.itx.util.FileUtil;

public final class ValidatorUtilities {
	
	private ValidatorUtilities() { }

    private static DERObject getObject(
            String oid,
            byte[] ext)
    {
    	ASN1InputStream aInOcts = null;
    	ASN1InputStream aIn = null;
        try {
        	aInOcts = new ASN1InputStream(ext);
            ASN1OctetString octs = (ASN1OctetString) aInOcts.readObject();

            aIn = new ASN1InputStream(octs.getOctets());
            return aIn.readObject();
        } catch (Exception e) {
            throw new RuntimeException("exception processing extension " + oid, e);
        } finally {
        	FileUtil.close(aInOcts);
        	FileUtil.close(aIn);
        }
    }	
    
    /**
     * Extract the value of the given extension, if it exists.
     * 
     * @param ext
     *            The extension object.
     * @param oid
     *            The object identifier to obtain.
     * @throws AnnotatedException
     *             if the extension cannot be read.
     */
    public static DERObject getExtensionValue(
        java.security.cert.X509Extension    ext,
        String                              oid)
    {
        byte[]  bytes = ext.getExtensionValue(oid);
        if (bytes == null)
        {
            return null;
        }

        return getObject(oid, bytes);
    }
    
    public static List<String> getCRLDistUrls(X509Certificate certificate) {
    	DERObject derObject = getExtensionValue(certificate, X509Extensions.CRLDistributionPoints.getId());
		if (derObject != null)
		{
			CRLDistPoint crlDistPoint = CRLDistPoint.getInstance(derObject);
			return getCRLDistUrls(crlDistPoint);
		}
		return Collections.emptyList();
    }
    
    public static List<String> getCRLDistUrls(CRLDistPoint crlDistPoints)
    {
    	List<String> urls = new ArrayList<String>();
        
        if (crlDistPoints != null)
        {
            DistributionPoint[] distPoints = crlDistPoints.getDistributionPoints();
            for (int i = 0; i < distPoints.length; i++)
            {
                DistributionPointName dpName = distPoints[i].getDistributionPoint();
                if (dpName.getType() == DistributionPointName.FULL_NAME)
                {
                    GeneralName[] generalNames = GeneralNames.getInstance(dpName.getName()).getNames();
                    for (int j = 0; j < generalNames.length; j++)
                    {
                        if (generalNames[j].getTagNo() == GeneralName.uniformResourceIdentifier)
                        {
                            String url = ((DERIA5String) generalNames[j].getName()).getString();
                            urls.add(url);
                        }
                    }
                }
            }
        }
        return urls;
    }    
	
}