package br.com.infox.epp.access.component.tree;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoSubTreeHandler.NAME)
@AutoCreate
public class LocalizacaoSubTreeHandler extends AbstractTreeHandler<Localizacao> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "localizacaoSubTree";
    public static final String EVENT_SELECTED = "evtSelectSubLocalizacao";
    private static final Annotation TREE_SELECT_EVENT = new AnnotationLiteral<LocalizacaoSubTreeSelectEvent>() {private static final long serialVersionUID = 1L;};
    
    private int idLocalizacaoPai = -1;
    
    
    @Override
    protected String getQueryRoots() {
        return "select n from Localizacao n where n.idLocalizacao = " + idLocalizacaoPai + " order by n.localizacao";
    }

    @Override
    protected String getQueryChildren() {
        return "select n from Localizacao n where localizacaoPai = :"
                + EntityNode.PARENT_NODE;
    }

    @Override
    protected String getEventSelected() {
        return EVENT_SELECTED;
    }
    
    @Override
    protected Annotation getTreeItemSelect() {
        return TREE_SELECT_EVENT;
    }

    @Override
    protected Localizacao getEntityToIgnore() {
        return ((LocalizacaoCrudAction) Component.getInstance(LocalizacaoCrudAction.NAME)).getInstance();
    }
    
    public Integer getIdLocalizacaoPai() {
		return idLocalizacaoPai;
	}
    
    public void setIdLocalizacaoPai(Integer idLocalizacaoPai) {
		this.idLocalizacaoPai = idLocalizacaoPai;
	}
    
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public @interface LocalizacaoSubTreeSelectEvent {}
    
    
}
