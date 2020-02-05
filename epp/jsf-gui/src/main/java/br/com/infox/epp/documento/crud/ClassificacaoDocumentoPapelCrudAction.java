package br.com.infox.epp.documento.crud;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;
import edu.emory.mathcs.backport.java.util.Arrays;
import lombok.Getter;

@Named
@ViewScoped
public class ClassificacaoDocumentoPapelCrudAction extends AbstractCrudAction<ClassificacaoDocumentoPapel, ClassificacaoDocumentoPapelManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
    @Inject
    private PapelManager papelManager;

    @Getter
    private List<TipoMeioAssinaturaEnum> tiposMeioAssinatura;

    private ClassificacaoDocumento classificacaoDocumento;


    @SuppressWarnings("unchecked")
    @PostConstruct
    private void inicialize() {
        this.tiposMeioAssinatura = Arrays.asList(TipoMeioAssinaturaEnum.values());
    }

    public void editar(ClassificacaoDocumentoPapel classificacaoDocumentoPapel) {
        setId(classificacaoDocumentoPapel.getId(), false);
    }

    @Override
    public void newInstance() {
        super.newInstance();
        getInstance().setClassificacaoDocumento(classificacaoDocumento);
    }

    @Override
    protected void afterSave(String ret) {
        super.afterSave(ret);
        newInstance();
        getInstance().setClassificacaoDocumento(classificacaoDocumento);
    }

    @Override
    public String remove(ClassificacaoDocumentoPapel obj) {
        String remove = super.remove(obj);
        newInstance();
        return remove;
    }

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        newInstance();
        getInstance().setClassificacaoDocumento(classificacaoDocumento);
        this.classificacaoDocumento = classificacaoDocumento;
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

    @Override
    protected ClassificacaoDocumentoPapelManager getManager() {
        return classificacaoDocumentoPapelManager;
    }
}
