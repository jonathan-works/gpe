package br.com.infox.epp.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.dao.ClassificacaoDocumentoPapelDAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;

@AutoCreate
@Name(ClassificacaoDocumentoPapelManager.NAME)
public class ClassificacaoDocumentoPapelManager extends Manager<ClassificacaoDocumentoPapelDAO, ClassificacaoDocumentoPapel> {
	
    private static final long serialVersionUID = 4455754174682600299L;
    public static final String NAME = "classificacaoDocumentoPapelManager";
    
    public boolean papelPodeAssinarClassificacao(Papel papel, ClassificacaoDocumento classificacao) {
    	return getDao().papelPodeAssinarClassificacao(papel, classificacao);
    }
}
