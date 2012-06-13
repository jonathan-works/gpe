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

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import br.com.itx.component.Util;


@Name("helpFileUpload")
@BypassInterceptors
public class HelpFileHome {

	private static final String IMAGES_DIR = "/img/help";
	private static final long serialVersionUID = 1L;

	public String getImagesDir() {
		return new Util().getContextRealPath() + IMAGES_DIR;
	}

	public String getImagesPath() {
		return new Util().getContextPath() + IMAGES_DIR;
	}

	public void listener(UploadEvent e) {
		//TODO retirar
		System.out.println("Iniciando upload");
		UploadItem uit = e.getUploadItem();
		File fileDestino = new File(getImagesDir(), uit.getFileName());
		try {
			saveFile(uit.getData(), fileDestino);
			//TODO retirar
			System.out.println("Upload feito com sucesso: " + fileDestino.getName());
		} catch (IOException e1) {
			e1.printStackTrace();
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
	private void saveFile(byte[] bytesOrigem, File fileDestino) throws IOException {
		if (fileDestino.exists()) {
			fileDestino.delete();
		}
		fileDestino.createNewFile();
		OutputStream out = null;
		try {
			out = new FileOutputStream(fileDestino);
			out.write(bytesOrigem);
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	public String[] getImages() {
		File dir = new File(getImagesDir());
		if (!dir.canRead()) {
			return null;
		}
		String[] files = dir.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return (name.endsWith(".jpg") ||
				name.endsWith(".png") ||
				name.endsWith(".gif"));
			}
			
		});
		for (int i = 0; i < files.length; i++) {
			files[i] = getImagesPath() + "/" + files[i];
		}
		return files;
	}
	
}