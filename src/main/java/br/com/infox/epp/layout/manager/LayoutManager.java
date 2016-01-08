package br.com.infox.epp.layout.manager;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.google.common.io.Files;

import br.com.infox.epp.layout.dao.BinarioDao;
import br.com.infox.epp.layout.dao.ResourceBinDao;
import br.com.infox.epp.layout.dao.ResourceDao;
import br.com.infox.epp.layout.dao.SkinDao;
import br.com.infox.epp.layout.entity.Binario;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.epp.layout.entity.Resource.Resources;
import br.com.infox.epp.layout.entity.ResourceBin;
import br.com.infox.epp.layout.entity.ResourceBin.TipoResource;
import br.com.infox.epp.layout.entity.Skin;
import br.com.infox.epp.layout.rest.entity.MetadadosResource;

@Stateless
public class LayoutManager {

	@Inject
	private SkinDao skinDao;

	@Inject
	private SkinSessaoManager skinManager;

	@Inject
	private ResourceBinDao resourceBinDao;
	
	@Inject
	private BinarioDao binarioDao;
	
	@Inject
	private ResourceDao resourceDao;
	
	public List<Skin> listSkins() {
		return skinDao.findAll();
	}

	public Skin getSkinPadrao() {
		return skinDao.getSkinPadrao();
	}

	public void setSkinPadrao(Skin skin) {
		Skin skinPadraoAntiga = getSkinPadrao();
		skinPadraoAntiga.setPadrao(false);
		skin.setPadrao(true);
		skinDao.persist(skinPadraoAntiga);
		skinDao.persist(skin);
		skinManager.setSkin(skin.getCodigo());
		skinManager.setSkinCookie(skin.getCodigo());
	}
	
	public byte[] carregarBinario(Integer idBinario) {
		return binarioDao.findById(idBinario).getBinario();
	}

	private void setResourceBin(String codigo, byte[] bin, TipoResource tipo) {
		Resource resource = resourceDao.findByCodigo(codigo);
		List<ResourceBin> resourcesAtuais = resourceBinDao.findByResource(resource);
		if (resourcesAtuais != null) {
			for (ResourceBin resourceAtual : resourcesAtuais) {
				binarioDao.removeById(resourceAtual.getIdBinario());
				resourceBinDao.remove(resourceAtual);
			}
		}
		ResourceBin resourceBin = new ResourceBin();
		resourceBin.setResource(resource);
		resourceBin.setTipo(tipo);
		resourceBin.setDataModificacao(new Date());
		
		Binario binario = new Binario();
		binario.setBinario(bin);
		binarioDao.persist(binario);
		resourceBin.setIdBinario(binario.getId());
		List<Skin> skinsAssociadas = skinDao.findAll();
		for (Skin skin : skinsAssociadas) {
			resourceBin.add(skin);
		}
		resourceBinDao.persist(resourceBin);

	}

	public void setLogoLogin(byte[] logoLogin, TipoResource tipoResource) {
		setResourceBin(Resources.LOGO_LOGIN.toString(), logoLogin, tipoResource);
	}

	public void setLogoTopo(byte[] logoTopo, TipoResource tipoResource) {
		setResourceBin(Resources.LOGO_TOPO.toString(), logoTopo, tipoResource);
	}
	
	/**
	 * Localiza um {@link ResourceBin} por código e path que inicie com o path informado (ignorando a extensão do arquivo) 
	 */
	private ResourceBin getResourceBin(String codigoSkin, String pathRecurso) {
		Skin skin = skinDao.findByCodigo(codigoSkin);
		if(skin == null) {
			return null;
		}
		
		String pathSemExtensao = pathRecurso.substring(0, pathRecurso.lastIndexOf(".") + 1);
		Resource resource = resourceDao.findByStartingPath(pathSemExtensao);
		
		ResourceBin retorno = resourceBinDao.findBySkinAndPath(skin, resource.getPath());
		return retorno;
	}
	
	public byte[] carregarBinario(String codigoSkin, String pathRecurso) {
		ResourceBin resourceBin = getResourceBin(codigoSkin, pathRecurso);
		if(resourceBin == null) {
			return null;
		}
		return carregarBinario(resourceBin.getIdBinario());
	}
	
	private String getResourcePathRest(String codigoSkin, String pathRecurso) {
		ResourceBin resourceBin = getResourceBin(codigoSkin, pathRecurso);
		
		
		Path diretorio = Paths.get(pathRecurso).getParent();
		String nomeArquivo = Files.getNameWithoutExtension(pathRecurso);
		String extensao = resourceBin.getTipo().toString().toLowerCase();
		
		return MessageFormat.format("/rest/skin/{0}{1}/{2}.{3}", codigoSkin, diretorio, nomeArquivo, extensao);
	}

	
	private String getResourcePathJava(String codigoSkin, String pathRecurso) {
		return MessageFormat.format("/resources/styleSkinInfox/{0}{1}", codigoSkin, pathRecurso);
	}
	
	public MetadadosResource getMetadados(String codigoSkin, String pathRecurso) {
		ResourceBin resourceBin = getResourceBin(codigoSkin, pathRecurso);
		//Retorna resources do banco
		if(resourceBin != null) {
			return new MetadadosResource(resourceBin);			
		}
		
		URL url = LayoutManager.class.getResource(getResourcePathJava(codigoSkin, pathRecurso));
		return new MetadadosResource(url);
	}
	
	public String getResourcePath(String codigoSkin, String path) {
		ResourceBin resourceBin = getResourceBin(codigoSkin, path);
		if(resourceBin != null) {
			return getResourcePathRest(codigoSkin, path);
		}
		return getResourcePathJava(codigoSkin, path);
	}
	
	
	
}
