package br.com.infox.epp.twitter.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.twitter.entity.TwitterTemplate;

@Name(TwitterTemplateManager.NAME)
@AutoCreate
public class TwitterTemplateManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    private static final Class<TwitterTemplate> CLASS = TwitterTemplate.class;
    public static final String NAME = "twitterTemplateManager";
    
    public TwitterTemplate find(Integer idTwitterTemplate) {
        return super.find(CLASS, idTwitterTemplate);
    }

}
