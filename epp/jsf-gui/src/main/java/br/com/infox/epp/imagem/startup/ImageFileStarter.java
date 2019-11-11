package br.com.infox.epp.imagem.startup;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import br.com.infox.epp.imagem.manager.ImagemBinManager;

@Name(ImageFileStarter.NAME)
@Scope(ScopeType.APPLICATION)
@Startup
public class ImageFileStarter {

    public static final String NAME = "imageFileStarter";

    @In
    private ImagemBinManager imagemBinManager;

    public ImageFileStarter() {
    }

    @Create
    public void init() {
        imagemBinManager.createImageFiles();
    }

}
