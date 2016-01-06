package br.com.infox.epp.layout.manager;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.layout.dao.ResourceDao;
import br.com.infox.epp.layout.dao.ResourceSkinDao;
import br.com.infox.epp.layout.dao.SkinDAO;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.epp.layout.entity.Resource.TipoResource;
import br.com.infox.epp.layout.entity.ResourceSkin;
import br.com.infox.epp.layout.entity.Skin;

@Stateless
public class LayoutManager {

	@Inject
	private SkinDAO skinDao;

	// Utilizado @EJB devido a um bug na implementação do CDI do JBoss
	@Inject
	private SkinSessaoManager skinManager;

	@Inject
	private ResourceDao resourceDao;
	
	@Inject
	private ResourceSkinDao resourceSkinDao;

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

	private void setResource(TipoResource tipo, byte[] bin) {
		List<Resource> resourcesAtuais = resourceDao.findByTipo(tipo);
		if (resourcesAtuais != null) {
			for (Resource resourceAtual : resourcesAtuais) {
				resourceDao.remove(resourceAtual);
			}
		}
		Resource resource = new Resource();
		resource.setTipo(tipo);
		resource.setResource(bin);
		List<Skin> skinsAssociadas = skinDao.findAll();
		for (Skin skin : skinsAssociadas) {
			ResourceSkin rs = new ResourceSkin();
			rs.setDataModificacao(new Date());
			rs.setSkin(skin);
			resource.add(rs);
		}
		resourceDao.persist(resource);

	}

	public void setLogoLogin(byte[] logoLogin) {
		setResource(TipoResource.LOGO_LOGIN, logoLogin);
	}

	public void setLogoTopo(byte[] logoTopo) {
		setResource(TipoResource.LOGO_TOPO, logoTopo);
	}
	
	public ResourceSkin getResourceSkin(String codigoSkin, String pathRecurso) {
		Skin skin = skinDao.findByCodigo(codigoSkin);
		if(skin == null) {
			return null;
		}
		ResourceSkin retorno =  resourceSkinDao.findBySkinAndPath(skin, pathRecurso);
		return retorno;
	}
}
