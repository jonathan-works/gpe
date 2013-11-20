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
import org.jboss.seam.international.Messages;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.itx.util.ArrayUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.Crypto;

@Name(FileHome.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class FileHome implements Serializable, FileUploadListener {

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
	
	@Override
	public void processFileUpload(final FileUploadEvent ue) {
		final UploadedFile ui = ue.getUploadedFile();
		this.data = ui.getData();
		this.fileName = ui.getName();
		this.size = Long.valueOf(ui.getSize()).intValue();
		this.contentType = ui.getContentType();
		FacesMessages.instance().add(Messages.instance().get("processoDocumento.doneLabel"));
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