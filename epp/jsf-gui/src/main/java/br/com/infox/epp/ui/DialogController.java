package br.com.infox.epp.ui;

import java.io.Serializable;

import javax.inject.Named;

import org.primefaces.context.RequestContext;

import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class DialogController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String src;
	private String header;
	
	public static interface DialogBuilder {
		public BuilderOpcionais src(String src);
	}
	
	public static interface BuilderOpcionais extends BuilderFinal {
		public BuilderOpcionais header(String header);
	}
	
	public static interface BuilderFinal {
		public void show();
	}
	
	private class DialogBuilderImpl implements DialogBuilder, BuilderOpcionais, BuilderFinal {

		@Override
		public void show() {
			DialogController.this.show();
		}

		@Override
		public BuilderOpcionais header(String header) {
			DialogController.this.header = header;
			return this;
		}

		@Override
		public BuilderOpcionais src(String src) {
			DialogController.this.src = src;
			return this;
		}
		
	}
	
	
	public DialogBuilder builder() {
		return new DialogBuilderImpl();
	}
	
	private void iniciarVariaveis() {
		src = null;
		header = null;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	public void clear() {
		this.iniciarVariaveis();
	}
	
	public void show() {
		RequestContext.getCurrentInstance().execute("PF('genericDialogVar').show();");
	}
	
	public void show(String src) {
		this.src = src;
		this.show();
	}
	
	public void show(String src, String header) {
		this.header = header;
		this.show(src);
	}
	
	public void hide() {
		RequestContext.getCurrentInstance().execute("PF('genericDialogVar').hide();");
		this.clear();
	}

}
