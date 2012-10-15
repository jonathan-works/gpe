/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.home.Authenticator;
import br.com.itx.component.Util;
import br.com.itx.util.FileUtil;


@Name("helpFileUpload")
@BypassInterceptors
public class HelpFileHome {

	private static final String IMAGES_DIR = "/img/help/";
	private static final LogProvider log = Logging.getLogProvider(ImageFileHome.class);

	private String getUserImageDir() {
		UsuarioLocalizacao usuarioLoc = (UsuarioLocalizacao) 
			Contexts.getSessionContext().get(Authenticator.USUARIO_LOCALIZACAO_ATUAL);
		if (usuarioLoc != null) {
			String locId = Integer.toString(usuarioLoc.getLocalizacao().getIdLocalizacao());
			return "localizacao" + "/" + locId;
		} 
		log.warn("Diretório de imagens do usuário: null");
		return null;
	}
	
	public String[] getImagesDir() {
		String path = new Util().getContextRealPath() + IMAGES_DIR;
		String userImageDir = getUserImageDir();
		if (userImageDir != null) {
			String[] images = {path, path + userImageDir};
			return images;
		} 
		String[] images = {path};
		return images;
	}

	public String[] getImagesPath() {
		String path = new Util().getContextPath() + IMAGES_DIR;
		String userImageDir = getUserImageDir();
		if (userImageDir != null) {
			String[] images = {path, path + userImageDir};
			return images;
		} 
		String[] images = {path};
		return images;
	}
	
	public String getImagePath() {
		String[] imagesPath = getImagesPath();
		return imagesPath[imagesPath.length - 1];
	}	

	public void listener(UploadEvent e) {
		System.out.println("Listener");
		UploadItem uit = e.getUploadItem();
		String[] imagesDir = getImagesDir();
		String imageDir = imagesDir[imagesDir.length - 1];
		File fileDestino = new File(imageDir, uit.getFileName());
		try {
			saveFile(uit.getData(), fileDestino);
		} catch (IOException e1) {
			FacesMessages.instance().add(
					"Erro ao adicionar arquivo: " +	e1.getMessage());
		}
	}
	
	/**
	 * Metodo que recebe um array de bytes e um File indicando o destino e salva
	 * os bytes no arquivo de destino.
	 * @param bytesOrigem
	 * @param fileDestino
	 * @throws IOException
	 */
	private void saveFile(byte[] bytesOrigem, File fileDestino)
			throws IOException {
		System.out.println("Iniciado salvar");
		if (fileDestino.exists()) {
			if (fileDestino.length() != bytesOrigem.length) {
				fileDestino = new File(getNewFileConflict(
						fileDestino.getAbsolutePath()));
			} else {
				String msg = "Arquivo já existente.";
				System.out.println(msg);
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
		log.info("Upload feito com sucesso: "
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
		
		List<String> files = new ArrayList<String>();
		
		for (int i = 0; i < getImagesDir().length; i++) {
			File dir = new File(getImagesDir()[i]);
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
		for (int i = 0; i < getImagesDir().length; i++) {
			File dir = new File(getImagesDir()[i]);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}		
	}
	
}