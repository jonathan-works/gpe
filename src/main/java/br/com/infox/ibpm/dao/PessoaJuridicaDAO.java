package br.com.infox.ibpm.dao;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.itx.util.EntityUtil;

@Name(PessoaJuridicaDAO.NAME)
@AutoCreate
public class PessoaJuridicaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaJuridicaDAO";
	
	public PessoaJuridica searchByCnpj(String cnpj){
		String hql = "select o from PessoaJuridica o where o.cnpj = :cnpj";
		Query query = EntityUtil.createQuery(hql).setParameter("cnpj", cnpj);
		return EntityUtil.getSingleResult(query);
	}

}
