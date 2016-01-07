package br.com.infox.epp.layout.manager;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.layout.dao.BinarioDao;
import br.com.infox.epp.layout.dao.ResourceDao;
import br.com.infox.epp.layout.dao.SkinDao;
import br.com.infox.epp.layout.entity.Binario;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.epp.layout.entity.Resource.TipoResource;
import br.com.infox.epp.layout.entity.Skin;

@Stateless
public class LayoutManager {

	@Inject
	private SkinDao skinDao;

	// Utilizado @EJB devido a um bug na implementação do CDI do JBoss
	@Inject
	private SkinSessaoManager skinManager;

	@Inject
	private ResourceDao resourceDao;
	
	@Inject
	private BinarioDao binarioDao;
	
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
	}
	
	public byte[] carregarBinario(Integer idBinario) {
		return binarioDao.findById(idBinario).getBinario();
	}

	private void setResource(TipoResource tipo, byte[] bin) {
		List<Resource> resourcesAtuais = resourceDao.findByTipo(tipo);
		if (resourcesAtuais != null) {
			for (Resource resourceAtual : resourcesAtuais) {
				binarioDao.removeById(resourceAtual.getIdBinario());
				resourceDao.remove(resourceAtual);
			}
		}
		Resource resource = new Resource();
		resource.setTipo(tipo);
		resource.setDataModificacao(new Date());
		
		Binario binario = new Binario();
		binario.setBinario(bin);
		binarioDao.persist(binario);
		resource.setIdBinario(binario.getId());
		List<Skin> skinsAssociadas = skinDao.findAll();
		for (Skin skin : skinsAssociadas) {
			resource.add(skin);
		}
		resourceDao.persist(resource);

	}

	public void setLogoLogin(byte[] logoLogin) {
		setResource(TipoResource.LOGO_LOGIN, logoLogin);
	}

	public void setLogoTopo(byte[] logoTopo) {
		setResource(TipoResource.LOGO_TOPO, logoTopo);
	}
	
	public Resource getResource(String codigoSkin, String pathRecurso) {
		Skin skin = skinDao.findByCodigo(codigoSkin);
		if(skin == null) {
			return null;
		}
		Resource retorno =  resourceDao.findBySkinAndPath(skin, pathRecurso);
		return retorno;
	}
	
	public byte[] carregarBinario(String codigoSkin, String pathRecurso) {
		Resource resource = getResource(codigoSkin, pathRecurso);
		if(resource == null) {
			return null;
		}
		return carregarBinario(resource.getIdBinario());
	}
	
}
