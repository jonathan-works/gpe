package br.com.infox.epp.service.startup;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.epp.manager.ImagemBinManager;
import br.com.infox.ibpm.component.HelpFileHome;
import br.com.infox.ibpm.component.ImageFileHome;

@Name(ImageFileStarter.NAME)
@Scope(ScopeType.APPLICATION)
@Startup
public class ImageFileStarter {
    
    public static final String NAME = "imageFileStarter";
    
    @In private ImagemBinManager imagemBinManager;

    public ImageFileStarter() {
    }

    @Create
    @Transactional
    public void init() {
        imagemBinManager.createImageFiles(ImageFileHome.IMAGE_RELATIVE_PATH);
        imagemBinManager.createImageFiles(HelpFileHome.HELP_IMAGE_RELATIVE_PATH);
    }

}
