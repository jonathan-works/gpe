package br.com.infox.epp.tarefa.caixa;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(CaixaDAO.NAME)
@AutoCreate
public class CaixaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "caixaDAO";

	public void removeCaixaByIdCaixa(int idCaixa) {
		String hql = "update Processo set caixa = :caixa where caixa.idCaixa = :idCaixa";
		Query q = getEntityManager().createQuery(hql).setParameter("caixa", null).setParameter("idCaixa", idCaixa);
		q.executeUpdate();
	}
}
