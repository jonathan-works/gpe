package br.com.infox.epp.usuario;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UsuarioLoginSearch extends PersistenceController {

	public UsuarioLogin getUsuarioLoginByPessoaFisica(PessoaFisica pessoaFisica) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioLogin> cq = cb.createQuery(UsuarioLogin.class);
		Root<UsuarioLogin> usuario = createQueryGetUsuarioByPessoaFisica(pessoaFisica, cb, cq);
		cq.select(usuario);
		
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
	public String getLoginByPessoaFisica(PessoaFisica pessoaFisica) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<UsuarioLogin> usuario = createQueryGetUsuarioByPessoaFisica(pessoaFisica, cb, cq);
		cq.select(usuario.get(UsuarioLogin_.login));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	private Root<UsuarioLogin> createQueryGetUsuarioByPessoaFisica(PessoaFisica pessoaFisica, CriteriaBuilder cb, CriteriaQuery<?> cq) {
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		Predicate ativo = usuarioAtivo(cb, usuario);
		Predicate humano = cb.equal(usuario.get(UsuarioLogin_.tipoUsuario), UsuarioEnum.H);
		Predicate pessoaIgual = cb.equal(usuario.get(UsuarioLogin_.pessoaFisica), pessoaFisica);
		cq.where(cb.and(ativo, humano, pessoaIgual));
		
		return usuario;
	}

	public UsuarioLogin getUsuarioLoginByCpf(String cpf) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioLogin> cq = cb.createQuery(UsuarioLogin.class);
		
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		Join<UsuarioLogin, PessoaFisica> pessoa = usuario.join(UsuarioLogin_.pessoaFisica);
		
		Predicate ativo = usuarioAtivo(cb, usuario);
		Predicate humano = cb.equal(usuario.get(UsuarioLogin_.tipoUsuario), UsuarioEnum.H);
		Predicate cpfIgual = cb.equal(pessoa.get(PessoaFisica_.cpf), cpf);
		
		cq = cq.select(usuario).where(cb.and(ativo, humano, cpfIgual));
		
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
	public UsuarioLogin getUsuarioLoginByCpfWhenExists(String cpf) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioLogin> cq = cb.createQuery(UsuarioLogin.class);
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		Join<UsuarioLogin, PessoaFisica> pessoa = usuario.join(UsuarioLogin_.pessoaFisica);
		cq.where(cb.equal(usuario.get(UsuarioLogin_.tipoUsuario), UsuarioEnum.H),
				cb.equal(pessoa.get(PessoaFisica_.cpf), cpf));
		try {
			return getEntityManager().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	private Predicate usuarioAtivo(CriteriaBuilder cb, Path<UsuarioLogin> usuario) {
		Predicate naoProvisorio = cb.isFalse(usuario.get(UsuarioLogin_.provisorio));
		Predicate provisorioNoPrazo = cb.and(cb.isTrue(usuario.get(UsuarioLogin_.provisorio)), cb.greaterThan(usuario.get(UsuarioLogin_.dataExpiracao), new Date()));
		Predicate naoExpirado = cb.or(naoProvisorio,provisorioNoPrazo);
		Predicate naoBloqueado = cb.isFalse(usuario.get(UsuarioLogin_.bloqueio));
		Predicate ativo = cb.and(cb.isTrue(usuario.get(UsuarioLogin_.ativo)), naoBloqueado, naoExpirado);
		return ativo;
	}

	public UsuarioLogin getUsuarioByLogin(String login) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioLogin> cq = cb.createQuery(UsuarioLogin.class);
		
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		
		Predicate ativo = usuarioAtivo(cb, usuario);
		Predicate humano = cb.equal(usuario.get(UsuarioLogin_.tipoUsuario), UsuarioEnum.H);
		Predicate loginIgual = cb.equal(usuario.get(UsuarioLogin_.login), login);
		
		cq = cq.select(usuario).where(cb.and(ativo, humano, loginIgual));
		
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
}
