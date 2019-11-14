package br.com.infox.epp.usuario.rest;

import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Specializes;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.epp.pessoa.documento.dao.PessoaDocumentoSearch;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento_;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica_;

@Stateless
@Specializes
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaDocumentoDTOSearch extends PessoaDocumentoSearch {


    public List<PessoaDocumentoDTO> getDocumentosByCPF(String cpfOuCnpj) {
        if (cpfOuCnpj==null)
            return Collections.emptyList();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PessoaDocumentoDTO> cq = cb.createQuery(PessoaDocumentoDTO.class);
        Root<PessoaDocumento> documento = cq.from(PessoaDocumento.class);
        Path<Pessoa> pessoa = documento.get(PessoaDocumento_.pessoa);
        
        Predicate pessoaFisicaIgual = isPessoaFisicaIgualPredicate(cq, pessoa, cpfOuCnpj);
        Predicate pessoaJuridicaIgual = isPessoaJuridicaIgualPredicate(cq, pessoa, cpfOuCnpj);
        
        cq.distinct(true).select(cb.construct(PessoaDocumentoDTO.class, documento)).where(cb.or(pessoaFisicaIgual, pessoaJuridicaIgual));
        return getEntityManager().createQuery(cq).getResultList();
    }

    private Predicate isPessoaFisicaIgualPredicate(AbstractQuery<?> query, Path<Pessoa> pessoa, String cpfOuCnpj) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<PessoaFisica> pessoaFisica = query.from(PessoaFisica.class);
        Predicate cpfIgual = cb.equal(pessoaFisica.get(PessoaFisica_.cpf), cpfOuCnpj);
        Predicate pessoaFisicaIgual = cb.equal(pessoa, pessoaFisica);
        return cb.and(cpfIgual, pessoaFisicaIgual);
    }
    
    private Predicate isPessoaJuridicaIgualPredicate(AbstractQuery<?> query, Path<Pessoa> pessoa, String cpfOuCnpj) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<PessoaJuridica> pessoaJuridica = query.from(PessoaJuridica.class);
        Predicate cnpjIgual = cb.equal(pessoaJuridica.get(PessoaJuridica_.cnpj), cpfOuCnpj);
        Predicate pessoaJuridicaIgual = cb.equal(pessoa, pessoaJuridica);
        return cb.and(cnpjIgual, pessoaJuridicaIgual);
    }
    
}
