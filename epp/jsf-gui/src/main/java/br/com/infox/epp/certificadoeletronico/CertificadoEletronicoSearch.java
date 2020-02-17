package br.com.infox.epp.certificadoeletronico;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronico;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronico_;
import br.com.infox.epp.certificadoeletronico.view.CertificadoEletronicoVO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CertificadoEletronicoSearch extends PersistenceController {

    public List<CertificadoEletronicoVO> getListaCertificadoEletronicoVO(){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CertificadoEletronicoVO> cq = cb.createQuery(CertificadoEletronicoVO.class);
        Root<CertificadoEletronico> ce = cq.from(CertificadoEletronico.class);
        Join<?, PessoaFisica> pf = ce.join(CertificadoEletronico_.pessoaFisica);

        cq.select(cb.construct(cq.getResultType(), ce.get(CertificadoEletronico_.id), pf.get(PessoaFisica_.idPessoa),
                pf.get(PessoaFisica_.nome), ce.get(CertificadoEletronico_.dataInicio),
                ce.get(CertificadoEletronico_.dataFim), ce.get(CertificadoEletronico_.dataCadastro)));
        cq.orderBy(cb.asc(pf.get(PessoaFisica_.nome)));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
