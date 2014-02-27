package br.com.infox.epp.imagem.component;

import org.jboss.seam.annotations.Name;

import br.com.infox.jboss.util.ComponentUtil;

@Name(HelpFileUploader.NAME)
public class HelpFileUploader extends AbstractImageUploader {
    public static final String NAME = "helpFileUploader";

    public static final HelpFileUploader instance() {
        return ComponentUtil.getComponent(NAME);
    }

    public static final String HELP_IMAGE_RELATIVE_PATH = "/img/help/";

    @Override
    public String getImagesRelativePath() {
        return HelpFileUploader.HELP_IMAGE_RELATIVE_PATH;
    }

}
