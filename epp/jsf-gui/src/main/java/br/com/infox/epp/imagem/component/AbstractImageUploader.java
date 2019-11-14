package br.com.infox.epp.imagem.component;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.imagem.entity.ImagemBin;
import br.com.infox.epp.imagem.manager.ImagemBinManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Scope(ScopeType.CONVERSATION)
public abstract class AbstractImageUploader implements FileUploadListener, Serializable {
    
	private static final long serialVersionUID = 1L;
	public static final LogProvider LOG = Logging.getLogProvider(AbstractImageUploader.class);

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
            ret = fileName.substring(fileName.lastIndexOf('.') + 1);
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
        return MD5Encoder.encode(data);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = ArrayUtil.copyOf(data);
    }

    @Override
    public void processFileUpload(FileUploadEvent evt) {
        UploadedFile ui = evt.getUploadedFile();
        this.data = ui.getData();
        this.fileName = ui.getName();
        this.fileSize = Long.valueOf(ui.getSize()).intValue();

        final ImagemBin instance = createImageInstance();
        try {
            imagemBinManager.persistImageBin(instance);
            imagemBinManager.saveFile(instance, getImagesRelativePath());
        } catch (IOException | DAOException e) {
            LOG.error("Falha ao gravar no sistema de arquivos.", e);
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
