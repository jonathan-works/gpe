package br.com.infox.epp.layout.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.ResourceSkin;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ResourceSkinDao extends Dao<ResourceSkin, Integer> {

	public ResourceSkinDao() {
		super(ResourceSkin.class);
	}
	
}
