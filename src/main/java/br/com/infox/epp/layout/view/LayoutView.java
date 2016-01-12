package br.com.infox.epp.layout.view;

import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.epp.layout.entity.ResourceBin.TipoArquivo;
import br.com.infox.epp.layout.entity.Skin;
import br.com.infox.epp.layout.manager.LayoutManager;

@Named
@ViewScoped
public class LayoutView implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Skin skinPadrao;
	
	@Inject
	private LayoutManager layoutManager;
	
	private Resource resource;
	private byte[] binarioResource;
	private TipoArquivo tipoArquivo;
	
	private SortedSet<Skin> skins;
	
	@Inject
	private Logger log;
	
	public List<Resource> getResources() {
		return layoutManager.listResources();
	}
	
	private class ComparadorSkins implements Comparator<Skin> {
		@Override
		public int compare(Skin s1, Skin s2) {
			return s1.getNome().compareTo(s2.getNome());
		}
	}
	
	public SortedSet<Skin> getSkins() {
		if(skins == null) {
			skins = new TreeSet<>(new ComparadorSkins());
			skins.addAll(layoutManager.listSkins());
		}
		return skins;
	}

	public Skin getSkinPadrao() {
		if(skinPadrao == null) {
			skinPadrao = layoutManager.getSkinPadrao();
		}
		return skinPadrao;
	}

	public void setSkinPadrao(Skin skinPadrao) {
		this.skinPadrao = skinPadrao;
	}
	
	public void save() {
		layoutManager.setSkinPadrao(skinPadrao);
	}
	
	public void persistResource() {
		if(binarioResource == null) {
			FacesMessages.instance().add("Deve ser selecionado um arquivo");
			return;
		}
		layoutManager.setResource(resource.getCodigo(), binarioResource, tipoArquivo);
		newResource();
	}
	
	private Image getImagem(FileUploadEvent evt) {
		UploadedFile arquivo = evt.getUploadedFile(); 
		try {
			Image imagemUpload = ImageIO.read(arquivo.getInputStream());
			arquivo.getInputStream().close();
			return imagemUpload;
		} catch (IOException e) {
			log.log(Level.SEVERE, "Não foi possível recuperar o inputStream do arquivo carregado", e);
			FacesMessages.instance().add("Erro no upload do arquivo, tente novamente.");
		}
		return null;		
	}
	
	private TipoArquivo getTipoArquivo(FileUploadEvent evt) {
		String extensao = evt.getUploadedFile().getFileExtension();
		if(extensao.equalsIgnoreCase("jpg") || extensao.equalsIgnoreCase("jpeg")) {
			return TipoArquivo.JPG;
		}
		else if(extensao.equalsIgnoreCase(TipoArquivo.GIF.toString())) {
			return TipoArquivo.GIF;
		}
		else if(extensao.equalsIgnoreCase(TipoArquivo.SVG.toString())) {
			return TipoArquivo.SVG;
		}
		else if(extensao.equalsIgnoreCase(TipoArquivo.SVGZ.toString())) {
			return TipoArquivo.SVGZ;
		}
		else {
			return TipoArquivo.PNG;
		}
	}
	
	public void processUploadResource(FileUploadEvent evt) {
		binarioResource = evt.getUploadedFile().getData();
		//imagemLogoTopo = getImagem(evt);
		tipoArquivo = getTipoArquivo(evt);
	}
	
	public void newResource() {
		binarioResource = null;
		tipoArquivo = null;
		resource = null;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public byte[] getBinarioResource() {
		return binarioResource;
	}

	public void setBinarioResource(byte[] binarioResource) {
		this.binarioResource = binarioResource;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}
	
}
