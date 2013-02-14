package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.TwitterTemplate;
import br.com.infox.list.TwitterTemplateList;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(TwitterTemplateHome.NAME)
public class TwitterTemplateHome extends AbstractHome<TwitterTemplate>{

	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/ModeloDocumento/TwitterTemplate/modeloTwitterTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "ModelosDocumento.xls";
	public static final String NAME = "twitterTemplateHome";
    
	public static final TwitterTemplateHome instance(){
		return ComponentUtil.getComponent(NAME);
	}
	
   @Override
    public EntityList<TwitterTemplate> getBeanList() {
        return TwitterTemplateList.instance();
    }
    
    @Override
    public String getTemplate() {
        return TEMPLATE;
    }
    
    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
	
}
