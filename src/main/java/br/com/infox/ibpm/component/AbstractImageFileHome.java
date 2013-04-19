package br.com.infox.ibpm.component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import br.com.infox.epp.entity.ImagemBin;
import br.com.infox.epp.home.ImagemBinHome;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.home.Authenticator;
import br.com.itx.component.Util;
import br.com.itx.util.ArrayUtil;
import br.com.itx.util.Crypto;
import br.com.itx.util.FileUtil;

public abstract class AbstractImageFileHome {
	private static final LogProvider LOG = Logging.getLogProvider(AbstractImageFileHome.class);
	private static boolean updated = false;
		
	private String fileName;
	private Integer fileSize;
	private byte[] data;
	
	private String getUserImageDir() {
		UsuarioLocalizacao usuarioLoc = (UsuarioLocalizacao) 
			Contexts.getSessionContext().get(Authenticator.USUARIO_LOCALIZACAO_ATUAL);
		if (usuarioLoc != null) {
			String locId = Integer.toString(usuarioLoc.getLocalizacao().getIdLocalizacao());
			return "localizacao" + "/" + locId;
		} 
		LOG.warn("Diretório de imagens do usuário: null");
		return null;
	}
	
	public abstract String getBaseImagesPath();
	
	public String[] getImagesDir() {
		String path = new Util().getContextRealPath() + getBaseImagesPath();
		String userImageDir = getUserImageDir();
		if (userImageDir != null) {
			return new String[]{path, path + userImageDir};
		} 
		return new String[]{path};
	}
	
	public String[] getImagesPath() {
		String path = new Util().getContextPath() + getBaseImagesPath();
		String userImageDir = getUserImageDir();
		if (userImageDir != null) {
			return new String[]{path, path + userImageDir};
		} 
		return new String[]{path};
	}
	
	public String getImagePath() {
		String[] imagesPath = getImagesPath();
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
	
	private void loadFiles() {
		if (AbstractImageFileHome.updated)	{
			return;
		}
		
		ImagemBinHome imagemBinHome = ImagemBinHome.instance();
		EntityManager manager = imagemBinHome.getEntityManager();
		String hql = "select o from ImagemBin o";
		List<ImagemBin> list = manager.createQuery(hql).getResultList();
		
		for (ImagemBin imagemBin : list) {
			String[] imagesDir = getImagesDir();
			String imageDir = imagesDir[imagesDir.length - 1];
			File fileDestino = new File(imageDir, imagemBin.getNomeArquivo());
			
			if (fileDestino.exists()) {
				continue;
			}
			try {
				saveFile(imagemBin.getImagem(), fileDestino);
			} catch (IOException e) {
				LOG.warn(MessageFormat.format("Erro ao adicionar arquivo: {0}", e.getMessage()));
			}
		}
		AbstractImageFileHome.updated = true;
	}
	
	public void listener(UploadEvent e)	{
		UploadItem ui = e.getUploadItem();
		this.data = ui.getData();
		this.fileName = ui.getFileName();
		this.fileSize = ui.getFileSize();
		
		ImagemBinHome homeInstance = ImagemBinHome.instance();
		ImagemBin instance = homeInstance.getInstance();
		instance.setExtensao(getFileType());
		instance.setImagem(getData());
		instance.setMd5Imagem(getMD5());
		instance.setNomeArquivo(getFileName());
		instance.setTamanho(getFileSize());
		instance.setDataInclusao(new Date());
		
		AbstractImageFileHome.updated = !"PERSISTED".equalsIgnoreCase(homeInstance.persist());
	}
	
	/**
	 * Metodo que recebe um array de bytes e um File indicando o destino e salva
	 * os bytes no arquivo de destino.
	 * @throws IOException
	 */
	public void saveFile(byte[] bytesOrigem, File fileDestino) throws IOException {
		if (fileDestino.exists()) {
			if (fileDestino.length() != bytesOrigem.length) {
				fileDestino = new File(getNewFileConflict(
						fileDestino.getAbsolutePath()));
			} else {
				String msg = "Arquivo já existente.";
				LOG.error(msg);
				throw new IOException(msg);
			}
		}

		fileDestino.createNewFile();
		OutputStream out = null;
		try {
			out = new FileOutputStream(fileDestino);
			out.write(bytesOrigem);
			out.flush();
		} finally {
			FileUtil.close(out);
		}
		LOG.info("Upload feito com sucesso: "
				+ getUserImageDir() + "/" + fileDestino.getName());
	}
	
	private String getNewFileConflict(String nome) {
		int localPonto = nome.lastIndexOf(".");
		String ext = nome.substring(localPonto);
		String pre = nome.substring(0, localPonto);
		return pre + "_" + ext;
	}
	
	
	public List<String> getImages() {
		createDir();
		if (!AbstractImageFileHome.updated)	{
			loadFiles();
		}
		
		List<String> files = new ArrayList<String>();
		String[] imagesDir = getImagesDir();
		for (int i = 0; i < imagesDir.length; i++) {
			File dir = new File(imagesDir[i]);
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
			for (int j = 0; j < filesImg.length; j++) {
				filesImg[j] = getImagesPath()[i] + "/" + filesImg[j];
				files.add(filesImg[j]);
			}
		}

		return files;
	}
	
	private void createDir() {
		String[] imagesDir=getImagesDir();
		for (int i = 0; i < imagesDir.length; i++) {
			File dir = new File(imagesDir[i]);
			if (!dir.exists()) {
				boolean result = dir.mkdirs();
				LOG.warn(MessageFormat.format("Diretorio {0} criado? {1}", dir, result));
			}
		}		
	}
	
}