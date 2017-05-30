package br.com.infox.epp.twitter.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.twitter.entity.TwitterTemplate;
import br.com.infox.epp.twitter.manager.TwitterTemplateManager;

@Name(TweetTemplateCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class TweetTemplateCrudAction extends AbstractCrudAction<TwitterTemplate, TwitterTemplateManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tweetTemplateCrudAction";

}
