package br.com.infox.epp.endereco.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.endereco.entity.Endereco;

@Name(EnderecoDAO.NAME)
@AutoCreate
public class EnderecoDAO extends DAO<Endereco> {
    static final String NAME = "enderecoDAO";
    private static final long serialVersionUID = 1L;

}
