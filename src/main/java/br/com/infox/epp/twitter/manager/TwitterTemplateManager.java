package br.com.infox.epp.twitter.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.twitter.dao.TwitterTemplateDAO;
import br.com.infox.epp.twitter.entity.TwitterTemplate;

@Name(TwitterTemplateManager.NAME)
@AutoCreate
public class TwitterTemplateManager extends Manager<TwitterTemplateDAO, TwitterTemplate> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "twitterTemplateManager";
}
