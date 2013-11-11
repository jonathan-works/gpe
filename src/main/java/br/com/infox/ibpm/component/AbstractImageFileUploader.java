package br.com.infox.ibpm.component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.epp.entity.ImagemBin;
import br.com.infox.epp.manager.ImagemBinManager;
import br.com.itx.util.ArrayUtil;
import br.com.itx.util.Crypto;

@Scope(ScopeType.CONVERSATION)
public abstract class AbstractImageFileUploader implements FileUploadListener {
	public static final LogProvider LOG = Logging.getLogProvider(AbstractImageFileUploader.class);
		
	private String fileName;
	private Integer fileSize;
	private byte[] data;
	
	@In
    private ImagemBinManager imagemBinManager;
    
    public abstract String getImagesRelativePath();
    
    public String[] getImagesDir() {
        return imagemBinManager.getImagesDir(getImagesRelativePath());
    }
    
    public String[] getImagesPath() {
        return imagemBinManager.getImagesPath(getImagesRelativePath());
    }
    
    public String getImagePath() {
        String[] imagesPath = imagemBinManager.getDBPath(getImagesRelativePath());
        return imagesPath[imagesPath.length - 1];
    }	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileType() {
		String ret = "";
		if (fileName != null) {
			ret = fileName.substring(fileName.lastIndexOf('.')+1);
		}
		return ret;
	}
	
	public Integer getFileSize() {
		return fileSize;
	}
	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public String getMD5() {
		return Crypto.encodeMD5(data);
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = ArrayUtil.copyOf(data);
	}
	
	@Override
	public void processFileUpload(FileUploadEvent evt)	{
		UploadedFile ui = evt.getUploadedFile();
		this.data = ui.getData();
		this.fileName = ui.getName();
		this.fileSize = Long.valueOf(ui.getSize()).intValue();
		
		final ImagemBin instance = createImageInstance();
		try {
            imagemBinManager.persistImageBin(instance);
            imagemBinManager.saveFile(instance, getImagesRelativePath());
        } catch (IOException e) {
            LOG.error("Falha ao gravar no sistema de arquivos.",e);
        }
	}

    private ImagemBin createImageInstance() {
        final ImagemBin instance = new ImagemBin();
        instance.setImagem(this.data);
        instance.setNomeArquivo(this.fileName);
        instance.setTamanho(this.fileSize);
        instance.setExtensao(getFileType());
        instance.setMd5Imagem(getMD5());
        instance.setDataInclusao(new Date());
        instance.setFilePath(getImagePath());
        return instance;
    }

    public List<String> getImages() {
		return imagemBinManager.getImages(getImagesRelativePath());
	}
	
}