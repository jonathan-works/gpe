package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.Name;

import br.com.infox.ibpm.entity.TwitterTemplate;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(TwitterTemplateHome.NAME)
public class TwitterTemplateHome extends AbstractHome<TwitterTemplate>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "twitterTemplateHome";

	public static final TwitterTemplateHome instance(){
		return ComponentUtil.getComponent(NAME);
	}
	
}
