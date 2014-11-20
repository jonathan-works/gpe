package br.com.infox.epp.julgamento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.julgamento.entity.Sala;
@AutoCreate
@Name(SalaDAO.NAME)
public class SalaDAO extends DAO<Sala> {

    public static final String NAME = "salaDAO";
    private static final long serialVersionUID = 1L;

}
