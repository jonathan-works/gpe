package br.com.infox.epp.processo.documento.assinatura;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Name(AssinaturaDocumentoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class AssinaturaDocumentoList implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "assinaturaDocumentoList";
    private List<AssinaturaDocumento> resultList;
    
    @Create
    public void init() {
        populate();
    }
    
    public List<AssinaturaDocumento> getResultList() {
        return resultList;
    }

    private void populate() {
        resultList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AssinaturaDocumento assinaturaDocumento = new AssinaturaDocumento();
            assinaturaDocumento.setIdAssinatura(i);
            assinaturaDocumento.setCertChain("c");
            assinaturaDocumento.setSignature("a");
            assinaturaDocumento.setUsuario(null);
            assinaturaDocumento.setProcessoDocumentoBin(null);
            assinaturaDocumento.setDataAssinatura(new GregorianCalendar(2014, 7, 15+i).getTime());
            assinaturaDocumento.setMd5Documento("b");
            assinaturaDocumento.setNomeUsuario("Usuário "+i);
            assinaturaDocumento.setNomeLocalizacao("Localização "+((i*i*i)%7));
            assinaturaDocumento.setNomePapel("Perfil "+((i*i*i*i*i)%11));
            resultList.add(assinaturaDocumento);
        }
    }
    
}
