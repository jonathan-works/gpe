/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import br.com.itx.util.ArrayUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.Crypto;

@Name(FileHome.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class FileHome implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "fileHome";

	private byte[] data;
	private String fileName;
	private Integer size;
	private String contentType;

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = ArrayUtil.copyOf(data);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String update() {
		return null;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileType() {
		String ret = "";
		if (fileName != null) {
			ret = fileName.substring(fileName.lastIndexOf('.')+1);
		}
		return ret;
	}
	
	public static FileHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	public void clear() {
		this.data = null;
		this.fileName = null;
		this.size = null;
		this.contentType = null;
	}
	
	public String getMD5() {
		return Crypto.encodeMD5(data);
	}
	
	public void listener(UploadEvent ue) {
		UploadItem ui = ue.getUploadItem();
		this.data = ui.getData();
		this.fileName = ui.getFileName();
		this.size = ui.getFileSize();
		this.contentType = ui.getContentType();
	}

	public void download() {
		if (data == null) {
			return;
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType(contentType);
		response.setContentLength(data.length);
		response.setHeader("Content-disposition", "attachment; filename=\""	+ getFileName() + "\"");
		try {
			OutputStream out = response.getOutputStream();
			out.write(data);
			out.flush();
			facesContext.responseComplete();
		} catch (IOException ex) {
			FacesMessages.instance().add("Erro ao descarregar o arquivo: " + fileName);
		}
	}

}