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
import br.com.infox.epp.layout.entity.ResourceBin;
import br.com.infox.epp.layout.entity.ResourceBin.TipoArquivo;
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
	
	public List<Resource> listResources() {
		return resourceDao.findAll();
	}
	
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

	public void setResource(String codigoResource, byte[] bin, TipoArquivo tipoArquivo) {
		Resource resource = resourceDao.findByCodigo(codigoResource);
		List<ResourceBin> resourcesAtuais = resourceBinDao.findByResource(resource);
		if (resourcesAtuais != null) {
			for (ResourceBin resourceAtual : resourcesAtuais) {
				binarioDao.removeById(resourceAtual.getIdBinario());
				resourceBinDao.remove(resourceAtual);
			}
		}
		ResourceBin resourceBin = new ResourceBin();
		resourceBin.setResource(resource);
		resourceBin.setTipo(tipoArquivo);
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

	/**
	 * Retorna o código de um resource a partir de seu path
	 */
	public String getCodigo(String resourcePath) {
		String pathSemExtensao = resourcePath.substring(0, resourcePath.lastIndexOf(".") + 1);
		Resource resource = resourceDao.findByStartingPath(pathSemExtensao);
		return resource.getCodigo();
	}
	
	private ResourceBin getResourceBinByPath(String codigoSkin, String path) {
		String pathSemExtensao = path.substring(0, path.lastIndexOf(".") + 1);
		Resource resource = resourceDao.findByStartingPath(pathSemExtensao);
		return getResourceBin(codigoSkin, resource.getCodigo());
	}
	
	private ResourceBin getResourceBin(String codigoSkin, String codigoResource) {
		Skin skin = skinDao.findByCodigo(codigoSkin);
		if(skin == null) {
			return null;
		}
				
		ResourceBin retorno = resourceBinDao.findBySkinAndCodigo(skin, codigoResource);
		return retorno;
	}
	
	public byte[] carregarBinario(String codigoSkin, String codigoResource) {
		ResourceBin resourceBin = getResourceBin(codigoSkin, codigoResource);
		if(resourceBin == null) {
			return null;
		}
		return carregarBinario(resourceBin.getIdBinario());
	}
	
	private String getUrlRest(String codigoSkin, String codigoResource) {
		return MessageFormat.format("/rest/skin/{0}/{1}", codigoSkin, codigoResource);
	}
	
	private String getUrlRestByPath(String codigoSkin, String resourcePath) {
		ResourceBin resourceBin = getResourceBinByPath(codigoSkin, resourcePath);
		
		
		Path diretorio = Paths.get(resourcePath).getParent();
		String nomeArquivo = Files.getNameWithoutExtension(resourcePath);
		String extensao = resourceBin.getTipo().toString().toLowerCase();
		
		return MessageFormat.format("/rest/skin/{0}/path{1}/{2}.{3}", codigoSkin, diretorio, nomeArquivo, extensao);
	}

	
	private String getUrlJava(String codigoSkin, String codigoResource) {
		Resource res = resourceDao.findByCodigo(codigoResource);
		return getUrlJavaByPath(codigoSkin, res.getPath());
	}
	
	private String getUrlJavaByPath(String codigoSkin, String resourcePath) {
		return MessageFormat.format("/resources/styleSkinInfox/{0}{1}", codigoSkin, resourcePath);
	}
	
	public MetadadosResource getMetadados(String codigoSkin, String codigoResource) {
		ResourceBin resourceBin = getResourceBin(codigoSkin, codigoResource);
		//Retorna resources do banco
		if(resourceBin != null) {
			return new MetadadosResource(resourceBin);			
		}
		
		URL url = LayoutManager.class.getResource(getUrlJava(codigoSkin, codigoResource));
		return new MetadadosResource(url);
	}
	
	public String getResourceUrlByPath(String codigoSkin, String path) {
		ResourceBin resourceBin = getResourceBinByPath(codigoSkin, path);
		if(resourceBin != null) {
			return getUrlRestByPath(codigoSkin, path);
		}
		return getUrlJavaByPath(codigoSkin, path);
	}
	
	public String getResourceUrl(String codigoSkin, String codigo) {
		ResourceBin resourceBin = getResourceBin(codigoSkin, codigo);
		if(resourceBin != null) {
			return getUrlRest(codigoSkin, codigo);
		}
		return getUrlJava(codigoSkin, codigo);
	}
	
	
}
