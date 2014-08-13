package br.com.infox.epp.tce.prestacaocontas.modelo.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.GrupoPrestacaoContas;

@Name(GrupoPrestacaoContasDAO.NAME)
@AutoCreate
public class GrupoPrestacaoContasDAO extends DAO<GrupoPrestacaoContas> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "grupoPrestacaoContasDAO";
}
