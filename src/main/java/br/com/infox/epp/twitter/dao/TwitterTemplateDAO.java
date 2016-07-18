package br.com.infox.epp.twitter.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.twitter.entity.TwitterTemplate;

@Stateless
@AutoCreate
@Name(TwitterTemplateDAO.NAME)
public class TwitterTemplateDAO extends DAO<TwitterTemplate> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "twitterTemplateDAO";

}
