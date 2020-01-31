package br.com.infox.epp.loglab.search;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.service.ETurmalinaService;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante_;
import br.com.infox.epp.loglab.model.Servidor;
import br.com.infox.epp.loglab.model.Servidor_;
import br.com.infox.epp.loglab.vo.PesquisaParticipanteVO;
import br.com.infox.epp.loglab.vo.ServidorContribuinteVO;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ServidorContribuinteSearch extends PersistenceController {

    @Inject
    private ETurmalinaService eTurmalinaService;

    public List<ServidorContribuinteVO> pesquisaContribuinte(PesquisaParticipanteVO pesquisaParticipanteVO) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServidorContribuinteVO> query = cb.createQuery(ServidorContribuinteVO.class);
        Root<ContribuinteSolicitante> contribuinte = query.from(ContribuinteSolicitante.class);
        query.select(cb.construct(query.getResultType(), contribuinte));

        Predicate where = cb.conjunction();
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getCpf())) {
            where = cb.and(where,
                    cb.equal(contribuinte.get(ContribuinteSolicitante_.cpf), pesquisaParticipanteVO.getCpf()));
        }
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getNomeCompleto())) {
            Expression<String> expressionLike = cb.concat(cb.literal("%"), pesquisaParticipanteVO.getNomeCompleto());
            expressionLike = cb.concat(expressionLike, cb.literal("%"));
            where = cb.and(where,
                    cb.like(cb.lower(contribuinte.get(ContribuinteSolicitante_.nomeCompleto)), expressionLike));
        }
        query.where(where);

        return getEntityManager().createQuery(query).getResultList();
    }

    public List<ServidorContribuinteVO> pesquisaServidor(PesquisaParticipanteVO pesquisaParticipanteVO) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServidorContribuinteVO> query = cb.createQuery(ServidorContribuinteVO.class);
        Root<Servidor> servidor = query.from(Servidor.class);
        query.select(cb.construct(query.getResultType(), servidor));

        Predicate where = cb.conjunction();
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getCpf())) {
            where = cb.and(where, cb.equal(servidor.get(Servidor_.cpf), pesquisaParticipanteVO.getCpf()));
        }
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getMatricula())) {
            where = cb.and(where, cb.equal(servidor.get(Servidor_.matricula), pesquisaParticipanteVO.getMatricula()));
        }
        query.where(where);

        return getEntityManager().createQuery(query).getResultList();
    }

    public ServidorContribuinteVO getServidorByCPF(String cpf) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServidorContribuinteVO> query = cb.createQuery(ServidorContribuinteVO.class);
        Root<Servidor> servidor = query.from(Servidor.class);
        query.select(cb.construct(query.getResultType(), servidor));
        query.where(cb.equal(servidor.get(Servidor_.cpf), cpf));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<ServidorContribuinteVO> pesquisaServidorETurmalina(PesquisaParticipanteVO pesquisaParticipanteVO) {
        List<ServidorContribuinteVO> servidorList = new ArrayList<>();
        DadosServidorBean dadosServidor = new DadosServidorBean(pesquisaParticipanteVO.getCpf(),
                pesquisaParticipanteVO.getMatricula());

        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor);
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosServidores) {
            ServidorContribuinteVO servidor = convertDadosServidorResponse(dadosServidorResponseBean);
            servidorList.add(servidor);
        }

        return servidorList;
    }

    private ServidorContribuinteVO convertDadosServidorResponse(DadosServidorResponseBean dadosServidorResponseBean) {
        ServidorContribuinteVO servidor = new ServidorContribuinteVO();
        servidor.setCargoCarreira(dadosServidorResponseBean.getCargoCarreira());
        servidor.setCargoComissao(dadosServidorResponseBean.getCargoComissao());
        servidor.setCpf(dadosServidorResponseBean.getCpf());
        servidor.setDataEmissaoRg(DateUtil.parseDate(dadosServidorResponseBean.getServidorRGEmissao(), "dd-MM-yyyy"));
        servidor.setDataExercicio(DateUtil.parseDate(dadosServidorResponseBean.getDataExercicio(), "dd-MM-yyyy"));
        servidor.setDataNascimento(DateUtil.parseDate(dadosServidorResponseBean.getServidorDataNascimento(), "dd-MM-yyyy"));
        servidor.setDataNomeacao(DateUtil.parseDate(dadosServidorResponseBean.getDataNomeacaoContratacao(), "dd-MM-yyyy"));
        servidor.setDataPosse(DateUtil.parseDate(dadosServidorResponseBean.getDataPosse(), "dd-MM-yyyy"));
        servidor.setLocalTrabalho(dadosServidorResponseBean.getLocalTrabalho());
        servidor.setJornada(dadosServidorResponseBean.getJornada());
        servidor.setNomeMae(dadosServidorResponseBean.getServidorFiliacaoMae());
        servidor.setMatricula(dadosServidorResponseBean.getMatricula());
        servidor.setNomeCompleto(dadosServidorResponseBean.getNome());
        servidor.setNumeroRg(dadosServidorResponseBean.getServidorRG());
        servidor.setOcupacaoCarreira(dadosServidorResponseBean.getOcupacaoCarreira());
        servidor.setOcupacaoComissao(dadosServidorResponseBean.getOcupacaoComissao());
        servidor.setOrgaoEmissorRG(dadosServidorResponseBean.getServidorRGOrgao());
        servidor.setNomePai(dadosServidorResponseBean.getServidorFiliacaoPai());
        servidor.setOrgao(dadosServidorResponseBean.getOrgao());
        servidor.setSituacao(dadosServidorResponseBean.getSituacao());
        servidor.setSubFolha(dadosServidorResponseBean.getSubFolha());
        servidor.setTipoParticipante(TipoParticipanteEnum.SE);

        ServidorContribuinteVO servidorByCpf = getServidorByCPF(dadosServidorResponseBean.getCpf());
        if(servidorByCpf != null) {
            servidor.setId(servidorByCpf.getId());
            servidor.setCelular(servidorByCpf.getCelular());
            servidor.setEmail(servidorByCpf.getEmail());
            servidor.setIdPessoaFisica(servidorByCpf.getIdPessoaFisica());
        }
        return servidor;
    }

}
