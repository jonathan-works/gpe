package br.com.infox.epp.meiocontato.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;

@AutoCreate
@Name(MeioContatoDAO.NAME)
public class MeioContatoDAO extends DAO<MeioContato> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "meioContatoDAO";
	

}
