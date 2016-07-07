package br.com.infox.epp.processo.linkExterno;

import javax.ejb.Stateless;

import br.com.infox.cdi.dao.Dao;

@Stateless
public class LinkAplicacaoExternaDao extends Dao<LinkAplicacaoExterna, Integer> {
    public LinkAplicacaoExternaDao() {
        super(LinkAplicacaoExterna.class);
    }

}
