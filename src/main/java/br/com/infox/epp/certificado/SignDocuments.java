package br.com.infox.epp.certificado;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.core.util.StringUtil;

public class SignDocuments {
	
	
	private List<String> documentsMD5; 
	
	public SignDocuments(List<SignableDocument> documents) {
		setDocuments(documents);
	}


	public void setDocuments(List<SignableDocument> documents) {
		documentsMD5 = new ArrayList<String>();
		for (SignableDocument documento : documents) {
			this.documentsMD5.add(documento.getMD5());
		}
	}
	
	
	public String getDocumentsMD5(){
		return StringUtil.concatList( documentsMD5 ,",");
	}

}
