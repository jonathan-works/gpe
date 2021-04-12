package br.com.infox.epp.loglab.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.loglab.contribuinte.type.ContribuinteEnum;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.service.ETurmalinaService;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante_;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.pessoa.type.TipoGeneroEnum;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ContribuinteSolicitanteSearch extends PersistenceController {

    @Inject
    private ETurmalinaService eTurmalinaService;

    @Inject
    private EstadoSearch estadoSearch;

    public List<ContribuinteSolicitanteVO> getDadosContribuinteSolicitante(String numeroCpf, String numeroMatricula, ContribuinteEnum tipoContribuinte){
        List<ContribuinteSolicitanteVO> contribuinteSolicitanteList = new ArrayList<ContribuinteSolicitanteVO>();
        DadosServidorBean dadosServidor = new DadosServidorBean(numeroCpf, numeroMatricula);

        Boolean emExercicio = tipoContribuinte.equals(ContribuinteEnum.CO)? Boolean.FALSE : Boolean.TRUE;
        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor, emExercicio);
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosServidores) {
            ContribuinteSolicitanteVO contribuinteSolicitante = convertDadosServidorResponse(dadosServidorResponseBean, tipoContribuinte);
            contribuinteSolicitanteList.add(contribuinteSolicitante);
        }

        return contribuinteSolicitanteList;
    }

    public boolean isExisteUsuarioContribuinteSolicitante(String numeroCpf){
        return getContribuinteSolicitanteByCpfAndTipoContribuinte(numeroCpf, null) != null;
    }

    private ContribuinteSolicitanteVO convertDadosServidorResponse(DadosServidorResponseBean dadosServidorResponseBean, ContribuinteEnum tipoContribuinte) {
        ContribuinteSolicitanteVO contribuinteSolicitante = new ContribuinteSolicitanteVO();
        contribuinteSolicitante.setCpf(dadosServidorResponseBean.getCpf());
        contribuinteSolicitante.setMatricula(dadosServidorResponseBean.getMatricula());
        contribuinteSolicitante.setNomeCompleto(dadosServidorResponseBean.getNome());
        Date dataNascimento = DateUtil.parseDate(dadosServidorResponseBean.getServidorDataNascimento(), "dd/MM/yyyy");
        contribuinteSolicitante.setDataNascimento(dataNascimento);
        contribuinteSolicitante.setNumeroRg(dadosServidorResponseBean.getServidorRG());
        contribuinteSolicitante.setEmissorRg(dadosServidorResponseBean.getServidorRGOrgao());
        Estado estado = estadoSearch.retrieveEstadoByCodigo(dadosServidorResponseBean.getServidorRGUF());
        contribuinteSolicitante.setIdEstadoRg(estado != null ? estado.getId() : null);
        contribuinteSolicitante.setCdEstadoRg(estado != null ? estado.getCodigo() : null);
        contribuinteSolicitante.setNomeMae(dadosServidorResponseBean.getServidorFiliacaoMae());
        contribuinteSolicitante.setCidade(dadosServidorResponseBean.getServidorCidadeNome());
        String logradouro = dadosServidorResponseBean.getServidorLogrTipoDesc() + " " + dadosServidorResponseBean.getServidorLogradouro();
        contribuinteSolicitante.setLogradouro(logradouro);
        contribuinteSolicitante.setBairro(dadosServidorResponseBean.getServidorBairroNome());
        contribuinteSolicitante.setComplemento(dadosServidorResponseBean.getServidorEndComplemento());
        contribuinteSolicitante.setNumero(dadosServidorResponseBean.getServidorEndNumero());
        contribuinteSolicitante.setCep(dadosServidorResponseBean.getServidorCEP());
        TipoGeneroEnum tipoGeneroEnum = TipoGeneroEnum.safeValueOf(dadosServidorResponseBean.getServidorSexo().trim());
        contribuinteSolicitante.setSexo(tipoGeneroEnum);
        contribuinteSolicitante.setEmail(dadosServidorResponseBean.getServidorEmail());
        contribuinteSolicitante.setTelefone(dadosServidorResponseBean.getServidorTelefoneCelular());
        contribuinteSolicitante.setStatus(dadosServidorResponseBean.getStatus());

        ContribuinteSolicitante contrSolic = getContribuinteSolicitanteByMatriculaAndTipoContribuinte(
                dadosServidorResponseBean.getMatricula(), tipoContribuinte);

        if(contrSolic == null) {
            contrSolic = getContribuinteSolicitanteByCpfAndTipoContribuinte(
                    dadosServidorResponseBean.getCpf(), tipoContribuinte);
        }

        if (contrSolic != null) {
            contribuinteSolicitante.setId(contrSolic.getId());
            if (estado == null) {
                contribuinteSolicitante.setIdEstadoRg(contrSolic.getEstadoRg().getId());
                contribuinteSolicitante.setCdEstadoRg(contrSolic.getEstadoRg().getCodigo());
            }
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorCidadeNome())) {
                contribuinteSolicitante.setCidade(contrSolic.getCidade());
            }
            if (StringUtil.isEmpty(logradouro)) {
                contribuinteSolicitante.setLogradouro(contrSolic.getLogradouro());
            }
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorBairroNome())) {
                contribuinteSolicitante.setBairro(contrSolic.getBairro());
            }
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorEndComplemento())) {
                contribuinteSolicitante.setComplemento(contrSolic.getComplemento());
            }
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorEndNumero())) {
                contribuinteSolicitante.setNumero(contrSolic.getNumero());
            }
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorCEP())) {
                contribuinteSolicitante.setCep(contrSolic.getCep());
            }
            if (tipoGeneroEnum == null) {
                contribuinteSolicitante.setSexo(contrSolic.getSexo());
            }
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorEmail())) {
                contribuinteSolicitante.setEmail(contrSolic.getEmail());
            }
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorTelefoneCelular())) {
                contribuinteSolicitante.setTelefone(contrSolic.getTelefone());
            }
        }

        return contribuinteSolicitante;
    }

    public ContribuinteSolicitante getContribuinteSolicitanteByMatriculaAndTipoContribuinte(String matricula,
            ContribuinteEnum tipoContribuinte) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ContribuinteSolicitante> query = cb.createQuery(ContribuinteSolicitante.class);
        Root<ContribuinteSolicitante> contrSolic = query.from(ContribuinteSolicitante.class);

        query.select(contrSolic);

        query.where(cb.equal(contrSolic.get(ContribuinteSolicitante_.matricula), matricula),
                cb.equal(contrSolic.get(ContribuinteSolicitante_.tipoContribuinte), tipoContribuinte));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ContribuinteSolicitante getContribuinteSolicitanteByCpfAndTipoContribuinte(
        String numeroCpf,
        ContribuinteEnum tipoContribuinte
    ){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ContribuinteSolicitante> query = cb.createQuery(ContribuinteSolicitante.class);
        Root<ContribuinteSolicitante> contrSolic = query.from(ContribuinteSolicitante.class);
        Join<ContribuinteSolicitante, PessoaFisica> pessoaFisica = contrSolic.join(ContribuinteSolicitante_.pessoaFisica);

        query.select(contrSolic);

        query.where(
            cb.or(
                cb.equal(contrSolic.get(ContribuinteSolicitante_.cpf), numeroCpf),
                cb.equal(pessoaFisica.get(PessoaFisica_.cpf), numeroCpf)
            )
        );

        if(tipoContribuinte != null) {
            query.where(
                query.getRestriction(),
                cb.equal(
                    contrSolic.get(ContribuinteSolicitante_.tipoContribuinte),
                    tipoContribuinte
                )
            );
        }

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
