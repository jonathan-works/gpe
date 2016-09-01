package br.com.infox.epp.usuario;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Specializes;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.usuario.rest.UsuarioDTO;

@Stateless
@Specializes
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UsuarioDTOSearch extends UsuarioLoginSearch{
	
	public List<UsuarioDTO> getUsuarioDTOList() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UsuarioDTO> cq = cb.createQuery(UsuarioDTO.class);
		
		Root<UsuarioLogin> usuario = cq.from(UsuarioLogin.class);
		From<?, PessoaFisica> pessoa = usuario.join(UsuarioLogin_.pessoaFisica, JoinType.LEFT);
		From<?, PessoaDocumento> documentos = pessoa.join(PessoaFisica_.pessoaDocumentoList, JoinType.LEFT);
		From<?, MeioContato> meiosContato = pessoa.join(PessoaFisica_.meioContatoList, JoinType.LEFT);
		
		Predicate ativo = usuarioAtivoPredicate(usuario);
		Predicate isPessoa = podeFazerLoginPredicate(usuario);
		
		Selection<UsuarioDTO> selection = cb.construct(UsuarioDTO.class, usuario, pessoa, documentos, meiosContato);
		
		Order order = cb.asc(usuario.get(UsuarioLogin_.nomeUsuario));
		
		cq = cq.select(selection).where(cb.and(ativo, isPessoa)).orderBy(order);
		
		return getEntityManager().createQuery(cq).getResultList();
	}
	
}
