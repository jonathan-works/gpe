package br.com.infox.epp.imagem.component;

import org.jboss.seam.annotations.Name;

@Name(ImageFileUploader.NAME)
public class ImageFileUploader extends AbstractImageUploader {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "imageFileUploader";
    public static final String IMAGE_RELATIVE_PATH = "/img/imageFile/";

    @Override
    public String getImagesRelativePath() {
        return ImageFileUploader.IMAGE_RELATIVE_PATH;
    }

}
