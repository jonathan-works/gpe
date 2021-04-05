package br.com.infox.epp.loglab.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.Estado_;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ServidorContribuinteSearch extends PersistenceController {

    @Inject
    private ETurmalinaService eTurmalinaService;

    public List<ServidorContribuinteVO> pesquisaServidorContribuinte(PesquisaParticipanteVO pesquisaParticipanteVO) {
        if (TipoParticipanteEnum.CO.equals(pesquisaParticipanteVO.getTipoParticipante())) {
            return pesquisaContribuinte(pesquisaParticipanteVO);
        } else {
            return pesquisaServidorETurmalina(pesquisaParticipanteVO);
        }

    }

    public List<ServidorContribuinteVO> pesquisaContribuinte(PesquisaParticipanteVO pesquisaParticipanteVO) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServidorContribuinteVO> query = cb.createQuery(ServidorContribuinteVO.class);
        Root<ContribuinteSolicitante> contribuinte = query.from(ContribuinteSolicitante.class);
        Join<ContribuinteSolicitante, Estado> estado = contribuinte.join(ContribuinteSolicitante_.estado, JoinType.LEFT);

        query.select(cb.construct(query.getResultType(), contribuinte.get(ContribuinteSolicitante_.id),
                contribuinte.get(ContribuinteSolicitante_.pessoaFisica).get(PessoaFisica_.idPessoa),
                contribuinte.get(ContribuinteSolicitante_.cpf), contribuinte.get(ContribuinteSolicitante_.nomeCompleto),
                contribuinte.get(ContribuinteSolicitante_.dataNascimento),
                contribuinte.get(ContribuinteSolicitante_.sexo), contribuinte.get(ContribuinteSolicitante_.nomeMae),
                contribuinte.get(ContribuinteSolicitante_.email), contribuinte.get(ContribuinteSolicitante_.telefone),
                estado.get(Estado_.codigo), contribuinte.get(ContribuinteSolicitante_.cidade),
                contribuinte.get(ContribuinteSolicitante_.logradouro),
                contribuinte.get(ContribuinteSolicitante_.bairro),
                contribuinte.get(ContribuinteSolicitante_.complemento),
                contribuinte.get(ContribuinteSolicitante_.numero), contribuinte.get(ContribuinteSolicitante_.cep)));

        Predicate where = cb.conjunction();

        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getCpf())) {
            where = cb.and(where,
                    cb.equal(contribuinte.get(ContribuinteSolicitante_.cpf), pesquisaParticipanteVO.getCpf()));
        }
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getNomeCompleto())) {
            Expression<String> expressionLike = cb.concat(cb.literal("%"), pesquisaParticipanteVO.getNomeCompleto());
            expressionLike = cb.concat(expressionLike, cb.literal("%"));
            where = cb.and(where,
                    cb.like(cb.lower(contribuinte.get(ContribuinteSolicitante_.nomeCompleto)), cb.lower(expressionLike)));
        }
        query.where(where);

        List<ServidorContribuinteVO> resultList = getEntityManager().createQuery(query).getResultList();
        List<ServidorContribuinteVO> listaPessoaFisica = pesquisaPessoaFisicaContribuinte(pesquisaParticipanteVO, resultList.stream().map(ServidorContribuinteVO::getIdPessoaFisica).collect(Collectors.toList()));
        resultList.addAll(listaPessoaFisica);
        return resultList;
    }

    public List<ServidorContribuinteVO> pesquisaServidorBaseLocal(PesquisaParticipanteVO pesquisaParticipanteVO) {
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
        query.select(cb.construct(query.getResultType(), servidor.get(Servidor_.id),
                servidor.get(Servidor_.pessoaFisica).get(PessoaFisica_.idPessoa), servidor.get(Servidor_.cpf),
                servidor.get(Servidor_.nomeCompleto), servidor.get(Servidor_.dataNascimento),
                servidor.get(Servidor_.matricula), servidor.get(Servidor_.mae), servidor.get(Servidor_.email),
                servidor.get(Servidor_.celular), servidor.get(Servidor_.dataNomeacaoContratacao),
                servidor.get(Servidor_.dataPosse), servidor.get(Servidor_.dataExercicio),
                servidor.get(Servidor_.situacao), servidor.get(Servidor_.secretaria),
                servidor.get(Servidor_.departamento), servidor.get(Servidor_.subFolha), servidor.get(Servidor_.jornada),
                servidor.get(Servidor_.ocupacaoCarreira), servidor.get(Servidor_.cargoCarreira),
                servidor.get(Servidor_.ocupacaoComissao), servidor.get(Servidor_.cargoComissao),
                servidor.get(Servidor_.pai), servidor.get(Servidor_.numeroRg), servidor.get(Servidor_.dataEmissaoRg),
                servidor.get(Servidor_.orgaoEmissorRG)));
        query.where(cb.equal(servidor.get(Servidor_.cpf), cpf));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NonUniqueResultException  e) {
            throw new BusinessRollbackException(String.format("Mais de um servidor encontrado para o cpf %s", cpf), e);
        } catch (NoResultException e) {
            return null;
        }
    }

    public ServidorContribuinteVO getContribuinteByCPF(String cpf) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServidorContribuinteVO> query = cb.createQuery(ServidorContribuinteVO.class);
        Root<ContribuinteSolicitante> contribuinte = query.from(ContribuinteSolicitante.class);
        Join<ContribuinteSolicitante, PessoaFisica> pessoaFisica = contribuinte.join(ContribuinteSolicitante_.pessoaFisica);
        Join<ContribuinteSolicitante, Estado> estado = contribuinte.join(ContribuinteSolicitante_.estado, JoinType.LEFT);

        query.select(cb.construct(query.getResultType(), contribuinte.get(ContribuinteSolicitante_.id),
                pessoaFisica.get(PessoaFisica_.idPessoa),
                contribuinte.get(ContribuinteSolicitante_.cpf), contribuinte.get(ContribuinteSolicitante_.nomeCompleto),
                contribuinte.get(ContribuinteSolicitante_.dataNascimento),
                contribuinte.get(ContribuinteSolicitante_.sexo), contribuinte.get(ContribuinteSolicitante_.nomeMae),
                contribuinte.get(ContribuinteSolicitante_.email), contribuinte.get(ContribuinteSolicitante_.telefone),
                estado.get(Estado_.codigo), contribuinte.get(ContribuinteSolicitante_.cidade),
                contribuinte.get(ContribuinteSolicitante_.logradouro),
                contribuinte.get(ContribuinteSolicitante_.bairro),
                contribuinte.get(ContribuinteSolicitante_.complemento),
                contribuinte.get(ContribuinteSolicitante_.numero), contribuinte.get(ContribuinteSolicitante_.cep)));

        query.where(cb.equal(contribuinte.get(ContribuinteSolicitante_.cpf), cpf));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NonUniqueResultException  e) {
            throw new BusinessRollbackException(String.format("Mais de um contribuiente encontrado para o cpf %s", cpf), e);
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<ServidorContribuinteVO> pesquisaServidorETurmalina(PesquisaParticipanteVO pesquisaParticipanteVO) {
        List<ServidorContribuinteVO> servidorList = new ArrayList<>();
        DadosServidorBean dadosServidor = new DadosServidorBean(pesquisaParticipanteVO.getCpf(), pesquisaParticipanteVO.getMatricula());

        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor, Boolean.FALSE);
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
        servidor.setDataEmissaoRg(DateUtil.parseDate(dadosServidorResponseBean.getServidorRGEmissao(), "dd/MM/yyyy"));
        servidor.setDataExercicio(DateUtil.parseDate(dadosServidorResponseBean.getDataExercicio(), "dd/MM/yyyy"));
        servidor.setDataNascimento(DateUtil.parseDate(dadosServidorResponseBean.getServidorDataNascimento(), "dd/MM/yyyy"));
        servidor.setDataNomeacao(DateUtil.parseDate(dadosServidorResponseBean.getDataNomeacaoContratacao(), "dd/MM/yyyy"));
        servidor.setDataPosse(DateUtil.parseDate(dadosServidorResponseBean.getDataPosse(), "dd/MM/yyyy"));
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
        servidor.setEmail(dadosServidorResponseBean.getServidorEmail());
        servidor.setTipoParticipante(TipoParticipanteEnum.SE);
        servidor.setStatus(dadosServidorResponseBean.getStatus());

        ServidorContribuinteVO servidorByCpf = getServidorByCPF(dadosServidorResponseBean.getCpf());
        if (servidorByCpf != null) {
            servidor.setId(servidorByCpf.getId());
            servidor.setCelular(servidorByCpf.getCelular());
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorEmail())) {
                servidor.setEmail(servidorByCpf.getEmail());
            }
            servidor.setIdPessoaFisica(servidorByCpf.getIdPessoaFisica());
        }
        return servidor;
    }

    private List<ServidorContribuinteVO> pesquisaPessoaFisicaContribuinte(PesquisaParticipanteVO pesquisaParticipanteVO, List<Integer> idsExclusao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServidorContribuinteVO> query = cb.createQuery(ServidorContribuinteVO.class);
        Root<PessoaFisica> pessoaFisica = query.from(PessoaFisica.class);

        query.select(cb.construct(query.getResultType(), pessoaFisica.get(PessoaFisica_.idPessoa),
                pessoaFisica.get(PessoaFisica_.cpf), pessoaFisica.get(PessoaFisica_.nome),
                pessoaFisica.get(PessoaFisica_.dataNascimento)));

        Predicate where = cb.conjunction();

        if(idsExclusao != null && !idsExclusao.isEmpty()) {
            where = cb.and(where, cb.not(pessoaFisica.get(PessoaFisica_.idPessoa).in(idsExclusao)));
        }

        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getCpf())) {
            where = cb.and(where,
                    cb.equal(pessoaFisica.get(PessoaFisica_.cpf), pesquisaParticipanteVO.getCpf()));
        }
        if (!StringUtil.isEmpty(pesquisaParticipanteVO.getNomeCompleto())) {
            Expression<String> expressionLike = cb.concat(cb.literal("%"), pesquisaParticipanteVO.getNomeCompleto());
            expressionLike = cb.concat(expressionLike, cb.literal("%"));
            where = cb.and(where,
                    cb.like(cb.lower(pessoaFisica.get(PessoaFisica_.nome)), cb.lower(expressionLike)));
        }
        query.where(where);

        return getEntityManager().createQuery(query).getResultList();
    }

}
