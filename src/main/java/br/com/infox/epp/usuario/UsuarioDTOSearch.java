package br.com.infox.epp.usuario;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.usuario.rest.UsuarioDTO;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UsuarioDTOSearch {
	
	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public List<UsuarioDTO> getUsuarioDTOList() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioDTO> cq = cb.createQuery(UsuarioDTO.class);
		
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		Join<UsuarioLogin, PessoaFisica> pessoa = usuario.join(UsuarioLogin_.pessoaFisica);
		
		Predicate ativo = usuarioAtivo(cb, usuario);
		Predicate humano = cb.equal(usuario.get(UsuarioLogin_.tipoUsuario), UsuarioEnum.H);
		
		Selection<UsuarioDTO> selection = cb.construct(UsuarioDTO.class, usuario, pessoa);
		
		Order order = cb.asc(usuario.get(UsuarioLogin_.nomeUsuario));
		
		cq = cq.select(selection).where(cb.and(ativo, humano)).orderBy(order);
		
		return getEntityManager().createQuery(cq).getResultList();
	}

	private Predicate usuarioAtivo(CriteriaBuilder cb, Path<UsuarioLogin> usuario) {
		Predicate naoProvisorio = cb.isFalse(usuario.get(UsuarioLogin_.provisorio));
		
		Predicate naoExpirado = cb.or(naoProvisorio, cb.greaterThan(usuario.get(UsuarioLogin_.dataExpiracao), cb.function("current_timestamp", Date.class)));
		Predicate naoBloqueado = cb.isFalse(usuario.get(UsuarioLogin_.bloqueio));
		Predicate ativo = cb.and(cb.isTrue(usuario.get(UsuarioLogin_.ativo)), naoBloqueado, naoExpirado);
		return ativo;
	}
	
}
