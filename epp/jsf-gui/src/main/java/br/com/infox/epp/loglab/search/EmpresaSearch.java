package br.com.infox.epp.loglab.search;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.loglab.model.Empresa;
import br.com.infox.epp.loglab.model.Empresa_;
import br.com.infox.epp.loglab.vo.EmpresaVO;
import br.com.infox.epp.loglab.vo.PesquisaParticipanteVO;
import br.com.infox.epp.municipio.Estado_;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmpresaSearch extends PersistenceController {

    public Empresa findById(Long id) {
        Empresa empresa = getEntityManager().find(Empresa.class, id);
        return empresa;
    }

    public EmpresaVO getEmpresaVOByCnpj(String cnpj) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EmpresaVO> query = cb.createQuery(EmpresaVO.class);
        Root<Empresa> empresa = query.from(Empresa.class);
        query.select(cb.construct(query.getResultType(), empresa.get(Empresa_.id),
                empresa.get(Empresa_.pessoaJuridica).get(PessoaJuridica_.idPessoa), empresa.get(Empresa_.cnpj),
                empresa.get(Empresa_.tipoEmpresa), empresa.get(Empresa_.razaoSocial),
                empresa.get(Empresa_.nomeFantasia), empresa.get(Empresa_.dataAbertura),
                empresa.get(Empresa_.telefoneCelular), empresa.get(Empresa_.telefoneFixo), empresa.get(Empresa_.email),
                empresa.get(Empresa_.estado).get(Estado_.codigo), empresa.get(Empresa_.cidade),
                empresa.get(Empresa_.logradouro), empresa.get(Empresa_.bairro), empresa.get(Empresa_.complemento),
                empresa.get(Empresa_.numeroResidencia), empresa.get(Empresa_.cep)));

        query.where(cb.equal(empresa.get(Empresa_.cnpj), cnpj));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<EmpresaVO> pesquisaEmpresaVO(PesquisaParticipanteVO pesquisaParticipanteVO) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EmpresaVO> query = cb.createQuery(EmpresaVO.class);
        Root<Empresa> empresa = query.from(Empresa.class);
        query.select(cb.construct(query.getResultType(), empresa.get(Empresa_.id),
                empresa.get(Empresa_.pessoaJuridica).get(PessoaJuridica_.idPessoa), empresa.get(Empresa_.cnpj),
                empresa.get(Empresa_.tipoEmpresa), empresa.get(Empresa_.razaoSocial),
                empresa.get(Empresa_.nomeFantasia), empresa.get(Empresa_.dataAbertura),
                empresa.get(Empresa_.telefoneCelular), empresa.get(Empresa_.telefoneFixo), empresa.get(Empresa_.email),
                empresa.get(Empresa_.estado).get(Estado_.codigo), empresa.get(Empresa_.cidade),
                empresa.get(Empresa_.logradouro), empresa.get(Empresa_.bairro), empresa.get(Empresa_.complemento),
                empresa.get(Empresa_.numeroResidencia), empresa.get(Empresa_.cep)));

        Predicate where = cb.conjunction();
        Expression<String> expressionLike;
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getCnpj())) {
            where = cb.and(where, cb.equal(empresa.get(Empresa_.cnpj), pesquisaParticipanteVO.getCnpj()));
        }
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getNomeFantasia())) {
            expressionLike = cb.concat(cb.literal("%"), pesquisaParticipanteVO.getNomeFantasia());
            expressionLike = cb.concat(expressionLike, cb.literal("%"));
            where = cb.and(where, cb.like(cb.lower(empresa.get(Empresa_.nomeFantasia)), cb.lower(expressionLike)));
        }
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getRazaoSocial())) {
            expressionLike = cb.concat(cb.literal("%"), pesquisaParticipanteVO.getRazaoSocial());
            expressionLike = cb.concat(expressionLike, cb.literal("%"));
            where = cb.and(where, cb.like(cb.lower(empresa.get(Empresa_.razaoSocial)), cb.lower(expressionLike)));
        }
        query.where(where);

        List<EmpresaVO> resultList = getEntityManager().createQuery(query).getResultList();
        List<EmpresaVO> listaPessoaJuridica = pesquisaPessoaFisicaEmpresaVO(pesquisaParticipanteVO, resultList.stream().map(EmpresaVO::getIdPessoaJuridica).collect(Collectors.toList()));
        resultList.addAll(listaPessoaJuridica);
        return resultList;
    }

    private List<EmpresaVO> pesquisaPessoaFisicaEmpresaVO(PesquisaParticipanteVO pesquisaParticipanteVO, List<Integer> idsExclusao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EmpresaVO> query = cb.createQuery(EmpresaVO.class);
        Root<PessoaJuridica> pessoaJuridica = query.from(PessoaJuridica.class);
        query.select(cb.construct(query.getResultType(),
                pessoaJuridica.get(PessoaJuridica_.idPessoa), pessoaJuridica.get(PessoaJuridica_.cnpj),
                pessoaJuridica.get(PessoaJuridica_.razaoSocial),
                pessoaJuridica.get(PessoaJuridica_.nome)));

        Predicate where = cb.conjunction();
        if(idsExclusao != null && !idsExclusao.isEmpty()) {
            where = cb.and(where, cb.not(pessoaJuridica.get(PessoaJuridica_.idPessoa).in(idsExclusao)));
        }
        Expression<String> expressionLike;
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getCnpj())) {
            where = cb.and(where, cb.equal(pessoaJuridica.get(PessoaJuridica_.cnpj), pesquisaParticipanteVO.getCnpj()));
        }
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getNomeFantasia())) {
            expressionLike = cb.concat(cb.literal("%"), pesquisaParticipanteVO.getNomeFantasia());
            expressionLike = cb.concat(expressionLike, cb.literal("%"));
            where = cb.and(where, cb.like(cb.lower(pessoaJuridica.get(PessoaJuridica_.nome)), cb.lower(expressionLike)));
        }
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getRazaoSocial())) {
            expressionLike = cb.concat(cb.literal("%"), pesquisaParticipanteVO.getRazaoSocial());
            expressionLike = cb.concat(expressionLike, cb.literal("%"));
            where = cb.and(where, cb.like(cb.lower(pessoaJuridica.get(PessoaJuridica_.razaoSocial)), cb.lower(expressionLike)));
        }
        query.where(where);

        return getEntityManager().createQuery(query).getResultList();
    }

}
