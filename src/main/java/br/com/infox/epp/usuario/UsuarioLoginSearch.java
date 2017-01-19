package br.com.infox.epp.usuario;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.entity.UsuarioPerfil_;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento_;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;

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

    public boolean existeUsuarioByLogin(String login) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
        cq.where(cb.equal(usuario.get(UsuarioLogin_.login), login), usuarioAtivoPredicate(usuario),
                podeFazerLoginPredicate(usuario));
        cq.select(cb.count(usuario));
        return getEntityManager().createQuery(cq).getSingleResult() > 0;
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
		Predicate ativo = usuarioAtivoPredicate(usuario);
		Predicate humano = podeFazerLoginPredicate(usuario);
		Predicate pessoaIgual = cb.equal(usuario.get(UsuarioLogin_.pessoaFisica), pessoaFisica);
		cq.where(cb.and(ativo, humano, pessoaIgual));
		
		return usuario;
	}

	public UsuarioLogin getUsuarioLoginByCpf(String cpf) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioLogin> cq = cb.createQuery(UsuarioLogin.class);
		
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		Join<UsuarioLogin, PessoaFisica> pessoa = usuario.join(UsuarioLogin_.pessoaFisica);
		
		Predicate ativo = usuarioAtivoPredicate(usuario);
		Predicate podeFazerLogin = podeFazerLoginPredicate(usuario);
		Predicate cpfIgual = cb.equal(pessoa.get(PessoaFisica_.cpf), cpf);
		
		cq = cq.select(usuario).where(cb.and(ativo, podeFazerLogin, cpfIgual));
		
		return getEntityManager().createQuery(cq).getSingleResult();
	}

    Predicate podeFazerLoginPredicate(From<?,UsuarioLogin> usuario) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        return cb.equal(usuario.get(UsuarioLogin_.tipoUsuario), UsuarioEnum.S).not();
    }
	
	public UsuarioLogin getUsuarioLoginByCpfWhenExists(String cpf) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioLogin> cq = cb.createQuery(UsuarioLogin.class);
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		Join<UsuarioLogin, PessoaFisica> pessoa = usuario.join(UsuarioLogin_.pessoaFisica);
		cq.where(podeFazerLoginPredicate(usuario),
				cb.equal(pessoa.get(PessoaFisica_.cpf), cpf));
		try {
			return getEntityManager().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	Predicate usuarioAtivoPredicate(From<?,UsuarioLogin> usuario) {
	        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
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
		
		Predicate ativo = usuarioAtivoPredicate(usuario);
		
		Predicate podeFazerLogin = podeFazerLoginPredicate(usuario);
		Predicate loginIgual = cb.equal(usuario.get(UsuarioLogin_.login), login);
		
		cq = cq.select(usuario).where(cb.and(ativo, podeFazerLogin, loginIgual));
		
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
	public boolean existsUsuarioWithLocalizacaoPerfil(Localizacao localizacao, PerfilTemplate perfilTemplate) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		Join<UsuarioLogin, UsuarioPerfil> up = usuario.join(UsuarioLogin_.usuarioPerfilList);
		cq.where(usuarioAtivoPredicate(usuario),
		                podeFazerLoginPredicate(usuario),
				cb.equal(up.get(UsuarioPerfil_.localizacao), localizacao));
		if (perfilTemplate != null) {
			cq.where(cq.getRestriction(),
					cb.equal(up.get(UsuarioPerfil_.perfilTemplate), perfilTemplate));
		}
		cq.select(cb.count(usuario.get(UsuarioLogin_.idUsuarioLogin)));
		return getEntityManager().createQuery(cq).getSingleResult() > 0;
	}

    public boolean getAssinouTermoAdesao(String cpf) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        
        Root<PessoaFisica> pessoa = cq.from(PessoaFisica.class);
        From<?, DocumentoBin> termoAdesao = pessoa.join(PessoaFisica_.termoAdesao, JoinType.INNER);
        From<?, AssinaturaDocumento> assinatura = termoAdesao.join(DocumentoBin_.assinaturas, JoinType.INNER);
        From<?, PessoaFisica> pessoaFisica = assinatura.join(AssinaturaDocumento_.pessoaFisica, JoinType.INNER);
        
        cq=cq.where(cb.equal(pessoaFisica.get(PessoaFisica_.cpf), cpf));
        cq=cq.select(cb.countDistinct(assinatura));
        
        return getEntityManager().createQuery(cq).getSingleResult() > 0L;
    }
	
}
