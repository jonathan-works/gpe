package br.com.infox.epp.layout.view;

import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.layout.entity.ResourceSkin;
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
	
	private ResourceSkin logotipos;
	
	
	private byte[] logoLogin;
	private byte[] logoTopo;
	
	private Image imagemLogoLogin;
	private TipoImagem tipoImagemLogoLogin;
	private Image imagemLogoTopo;
	private TipoImagem tipoImagemLogoTopo;
	
	@Inject
	private Logger log;
	
	private enum TipoImagem {
		PNG, JPG, GIF
	}
	
	public List<Skin> getSkins() {
		return layoutManager.listSkins();
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
	
	public void persistLogotipos() {
	}
	
	public String persistLogoTopo() {
		layoutManager.setLogoTopo(logoTopo);
		return "";
	}
	
	public String persistLogoLogin() {
		layoutManager.setLogoLogin(logoLogin);
		return "";
	}
	
	public void newLogotipo() {
		logotipos = new ResourceSkin();
	}

	public ResourceSkin getLogotipos() {
		return logotipos;
	}

	public void setLogotipos(ResourceSkin logotipos) {
		this.logotipos = logotipos;
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
	
	private TipoImagem getTipoImagem(FileUploadEvent evt) {
		String extensao = evt.getUploadedFile().getFileExtension();
		if(extensao.equalsIgnoreCase("jpg") || extensao.equalsIgnoreCase("jpeg")) {
			return TipoImagem.JPG;
		}
		else if(extensao.equalsIgnoreCase(TipoImagem.GIF.toString())) {
			return TipoImagem.GIF;
		}
		else {
			return TipoImagem.PNG;
		}
	}
	
	public void processUploadLogoTopo(FileUploadEvent evt) {
		logoTopo = evt.getUploadedFile().getData();
		imagemLogoTopo = getImagem(evt);
		tipoImagemLogoTopo = getTipoImagem(evt);
	}
	
	public void processUploadLogoLogin(FileUploadEvent evt) {
		logoLogin = evt.getUploadedFile().getData();
		imagemLogoLogin = getImagem(evt);
		tipoImagemLogoLogin = getTipoImagem(evt);
	}

	public Image getImagemLogoLogin() {
		return imagemLogoLogin;
	}

	public void setImagemLogoLogin(Image imagemLogoLogin) {
		this.imagemLogoLogin = imagemLogoLogin;
	}

	public Image getImagemLogoTopo() {
		return imagemLogoTopo;
	}

	public void setImagemLogoTopo(Image imagemLogoTopo) {
		this.imagemLogoTopo = imagemLogoTopo;
	}
	
}
