package br.com.infox.epp.fluxo.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Item;

@Stateless
@AutoCreate
@Name(ItemDAO.NAME)
public class ItemDAO extends DAO<Item> {

    private static final long serialVersionUID = -7175831474709085125L;
    public static final String NAME = "itemDAO";

}
