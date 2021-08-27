package br.com.infox.epp.pessoa.manager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.epp.loglab.vo.AnonimoVO;
import br.com.infox.epp.pessoa.entity.PessoaAnonima;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaAnonimaService {

    @Inject
    @GenericDao
    private Dao<PessoaAnonima, Integer> pessoaAnonimaDao;

    public PessoaAnonima insert(AnonimoVO anonimoVO) {
        PessoaAnonima pessoaAnonima = new PessoaAnonima();
        pessoaAnonima.setTelefone(anonimoVO.getTelefone());
        pessoaAnonima.setNome(anonimoVO.getNomeDefaultIfNull());
        pessoaAnonima.setAtivo(Boolean.TRUE);
        return pessoaAnonimaDao.persist(pessoaAnonima);
    }

}