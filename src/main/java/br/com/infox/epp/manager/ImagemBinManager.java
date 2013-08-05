package br.com.infox.epp.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ImagemBinDAO;
import br.com.infox.epp.entity.ImagemBin;
import br.com.infox.util.ImageUtil;

@Name(ImagemBinManager.NAME)
@AutoCreate
public class ImagemBinManager extends GenericManager {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(ImagemBinManager.class); 
	public static final String NAME = "imagemBinManager";
	
	@In
    public ImagemBinDAO imagemBinDAO;
	@In 
	public ImageUtil imageUtil;

    public void persistImageBin(ImagemBin imagemBin, String imagesRelativePath) throws IOException {
        String imagesDir = getImagesDir(imagesRelativePath);
    	imagemBinDAO.persistImageBin(imagemBin,new File(imagesDir, imagemBin.getNomeArquivo()));
    }

    public String getImagesDir(String imagesRelativePath) {
        return imageUtil.getRealPath() + imagesRelativePath;
    }

    public String getImagesPath(String imagesRelativePath) {
        return imageUtil.getContextPath() + imagesRelativePath;
    }

    private void createDir(String imagesDir) {
		File dir = new File(imagesDir);
		if (!dir.exists()) {
			boolean result = dir.mkdirs();
			LOG.warn(MessageFormat.format("Diretorio {0} criado? {1}", dir, result));
		}
    }

    private String getNewFileConflict(String nome) {
        int localPonto = nome.lastIndexOf('.');
        String ext = nome.substring(localPonto);
        String pre = nome.substring(0, localPonto);
        return pre + "_" + ext;
    }
    
    public void saveFile(byte[] bytesOrigem, File fileDestino) throws IOException {
        if (fileDestino.exists()) {
            if (fileDestino.length() != bytesOrigem.length) {
                fileDestino = new File(getNewFileConflict(fileDestino.getAbsolutePath()));
            } else {
                throw new IOException(MessageFormat.format("Arquivo já existente: {0}{1}", fileDestino.getAbsolutePath(),fileDestino.getName()));
            }
        }
        imagemBinDAO.saveFile(bytesOrigem, fileDestino);
        LOG.info(MessageFormat.format("Arquivo instanciado com sucesso: {0}{1}", fileDestino.getAbsolutePath(),fileDestino.getName()));
    }
    
    public List<String> getImages(String imagesRelativePath) {
        String imagesDir = getImagesDir(imagesRelativePath);
        createDir(imagesDir);
    	
    	List<String> files = new ArrayList<String>();
        
    	File dir = new File(imagesDir);
        if (!dir.canRead()) {
            return null;
        }
        String[] filesImg = dir.list(new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpg") ||
                        name.endsWith(".png") ||
                        name.endsWith(".gif"));
            }
            
        });
        String imagesPath = getImagesPath(imagesRelativePath);
        for (int j = 0; j < filesImg.length; j++) {
            filesImg[j] = imagesPath + "/" + filesImg[j];
            files.add(filesImg[j]);
        }
    
    	return files;
    }

    public void createImageFiles(String relativeFilePath) {
        List<ImagemBin> list = imagemBinDAO.getTodasAsImagens();
        
        for (ImagemBin imagemBin : list) {
            String imagesDir = getImagesDir(relativeFilePath);
            File fileDestino = new File(imagesDir, imagemBin.getNomeArquivo());
            
            if (fileDestino.exists()) {
                continue;
            }
            try {
                saveFile(imagemBin.getImagem(), fileDestino);
            } catch (IOException e) {
                LOG.warn(MessageFormat.format("Erro ao adicionar arquivo: {0}{1}", fileDestino.getAbsolutePath(),fileDestino.getName()));
            }
        }
    }
	
}
