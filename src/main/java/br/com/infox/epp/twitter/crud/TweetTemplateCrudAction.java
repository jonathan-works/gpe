package br.com.infox.epp.twitter.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.twitter.entity.TwitterTemplate;

@Name(TweetTemplateCrudAction.NAME)
public class TweetTemplateCrudAction extends AbstractCrudAction<TwitterTemplate> {
    
    public static final String NAME = "tweetTemplateCrudAction";

}
