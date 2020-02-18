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
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.loglab.contribuinte.type.ContribuinteEnum;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.service.ETurmalinaService;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante_;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ContribuinteSolicitanteSearch extends PersistenceController {

    @Inject
    private ETurmalinaService eTurmalinaService;

    @Inject
    private EstadoSearch estadoSearch;

    @Inject
    private UsuarioSearch usuarioSearch;

    public List<ContribuinteSolicitanteVO> getDadosContribuinteSolicitante(String numeroCpf, String numeroMatricula, ContribuinteEnum tipoContribuinte){
        List<ContribuinteSolicitanteVO> contribuinteSolicitanteList = new ArrayList<ContribuinteSolicitanteVO>();
        DadosServidorBean dadosServidor = new DadosServidorBean(numeroCpf, numeroMatricula);

        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor);
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosServidores) {
            ContribuinteSolicitanteVO contribuinteSolicitante = convertDadosServidorResponse(dadosServidorResponseBean, tipoContribuinte);
            contribuinteSolicitanteList.add(contribuinteSolicitante);
        }

        return contribuinteSolicitanteList;
    }

    public boolean isExisteUsuarioContribuinteSolicitante(String numeroCpf){
        return usuarioSearch.isExisteUsuarioByNumeroCpf(numeroCpf);
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
        Estado estado = estadoSearch.retrieveEstadoByCodigo(dadosServidorResponseBean.getServidorRGOrgao());
        contribuinteSolicitante.setIdEstadoRg(estado != null ? estado.getId() : null);
        contribuinteSolicitante.setCdEstadoRg(estado != null ? estado.getCodigo() : null);
        contribuinteSolicitante.setNomeMae(dadosServidorResponseBean.getServidorFiliacaoMae());
        contribuinteSolicitante.setCidade(null);
        contribuinteSolicitante.setLogradouro(null);
        contribuinteSolicitante.setBairro(null);
        contribuinteSolicitante.setComplemento(null);
        contribuinteSolicitante.setNumero(null);
        contribuinteSolicitante.setCep(null);

        ContribuinteSolicitante contrSolic = getContribuinteSolicitanteByMatriculaAndTipoContribuinte(
                dadosServidorResponseBean.getMatricula(), tipoContribuinte);
        if(contrSolic != null) {
            contribuinteSolicitante.setId(contrSolic.getId());
            contribuinteSolicitante.setCidade(contrSolic.getCidade());
            contribuinteSolicitante.setLogradouro(contrSolic.getLogradouro());
            contribuinteSolicitante.setBairro(contrSolic.getBairro());
            contribuinteSolicitante.setComplemento(contrSolic.getComplemento());
            contribuinteSolicitante.setNumero(contrSolic.getNumero());
            contribuinteSolicitante.setCep(contrSolic.getCep());
            contribuinteSolicitante.setSexo(contrSolic.getSexo());
            contribuinteSolicitante.setEmail(contrSolic.getEmail());
            contribuinteSolicitante.setTelefone(contrSolic.getTelefone());
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
}
