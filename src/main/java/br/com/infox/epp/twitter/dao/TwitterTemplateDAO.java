package br.com.infox.epp.twitter.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.twitter.entity.TwitterTemplate;

@Name(TwitterTemplateDAO.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
public class TwitterTemplateDAO extends DAO<TwitterTemplate> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "twitterTemplateDAO";

}
