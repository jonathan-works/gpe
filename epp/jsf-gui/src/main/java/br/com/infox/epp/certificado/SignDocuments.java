package br.com.infox.epp.certificado;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.core.util.StringUtil;

public class SignDocuments {

    private List<String> documentsData;

    public SignDocuments(List<SignableDocument> documents) {
        setDocuments(documents);
    }

    public void setDocuments(List<SignableDocument> documents) {
        documentsData = new ArrayList<String>();
        for (SignableDocument documento : documents) {
            this.documentsData.add(documento.getUuid().toString() + ":" + documento.getMD5());
        }
    }

    public String getDocumentData() {
        return StringUtil.concatList(documentsData, ",");
    }

}
