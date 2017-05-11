package br.com.infox.epp.documento.crud;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;

@Name(ClassificacaoDocumentoPapelCrudAction.NAME)
public class ClassificacaoDocumentoPapelCrudAction extends AbstractCrudAction<ClassificacaoDocumentoPapel, ClassificacaoDocumentoPapelManager> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "classificacaoDocumentoPapelCrudAction";

    private PapelManager papelManager = BeanManager.INSTANCE.getReference(PapelManager.class);

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        newInstance();
        getInstance().setClassificacaoDocumento(classificacaoDocumento);
    }

    public List<Papel> papelItems() {
    	List<Papel> papeis = papelManager.getPapeisNaoAssociadosAClassificacaoDocumento(getInstance().getClassificacaoDocumento());
        Collections.sort( papeis, new Comparator<Papel>(){
        	@Override
        	public int compare(Papel p1, Papel p2){
        		return p1.getNome().compareTo(p2.getNome());
        	}
        });
        return papeis;
    }

}
