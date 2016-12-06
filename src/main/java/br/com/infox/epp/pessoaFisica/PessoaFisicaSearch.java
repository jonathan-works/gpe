package br.com.infox.epp.pessoaFisica;

import java.text.MessageFormat;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.entity.UsuarioPerfil_;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaFisicaSearch {

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public PessoaFisica getByCpf(String cpf) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PessoaFisica> cq = cb.createQuery(PessoaFisica.class);
		
		Root<PessoaFisica> pessoa = cq.from(PessoaFisica.class);
		
		Predicate ativo = cb.isTrue(pessoa.get(PessoaFisica_.ativo));
		Predicate cpfIgual = cb.equal(pessoa.get(PessoaFisica_.cpf), cpf);
		
		cq = cq.select(pessoa).where(cb.and(ativo, cpfIgual));
		List<PessoaFisica> pessoas =getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
		return pessoas == null || pessoas.isEmpty() ? null : pessoas.get(0);
	}

    public List<PessoaFisica> retrieveBy(Localizacao _localizacao, Papel papel, String query) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PessoaFisica> cq = cb.createQuery(PessoaFisica.class);
        
        From<?,UsuarioPerfil> usuarioPerfil = cq.from(UsuarioPerfil.class);
        From<?, UsuarioLogin> usuario = usuarioPerfil.join(UsuarioPerfil_.usuarioLogin, JoinType.INNER);
        From<?, PessoaFisica> pessoa = usuario.join(UsuarioLogin_.pessoaFisica, JoinType.INNER);
        
        Predicate restrictions = cb.isTrue(usuarioPerfil.get(UsuarioPerfil_.ativo));
        restrictions = cb.and(restrictions, cb.equal(usuarioPerfil.join(UsuarioPerfil_.localizacao, JoinType.INNER), _localizacao));
        restrictions = cb.and(restrictions, cb.equal(usuarioPerfil.join(UsuarioPerfil_.perfilTemplate, JoinType.INNER).join(PerfilTemplate_.papel, JoinType.INNER), papel));
        
        if (StringUtil.isEmpty(query)){
            String formattedQuery = MessageFormat.format("%{0}%", query.toLowerCase());
            restrictions = cb.and(restrictions,
                cb.like(cb.lower(pessoa.get(PessoaFisica_.nome)), formattedQuery)
            );
        }
        
        cq = cq.select(pessoa).where(restrictions).orderBy(cb.asc(pessoa.get(PessoaFisica_.nome)));
        return getEntityManager().createQuery(cq).getResultList();
    }

}
