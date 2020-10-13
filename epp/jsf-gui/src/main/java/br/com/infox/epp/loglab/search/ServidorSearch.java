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
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.service.ETurmalinaService;
import br.com.infox.epp.loglab.model.Servidor;
import br.com.infox.epp.loglab.model.Servidor_;
import br.com.infox.epp.loglab.vo.ServidorVO;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ServidorSearch extends PersistenceController {

    @Inject
    private ETurmalinaService eTurmalinaService;
    @Inject
    private UsuarioSearch usuarioSearch;

    public List<ServidorVO> getDadosServidor(String numeroCpf){
        List<ServidorVO> servidorList = new ArrayList<ServidorVO>();
        DadosServidorBean dadosServidor = new DadosServidorBean(numeroCpf);

        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor, Boolean.TRUE);
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosServidores) {
            ServidorVO servidor = convertDadosServidorResponse(dadosServidorResponseBean);
            servidorList.add(servidor);
        }

        return servidorList;
    }

    public boolean isExisteUsuarioServidor(String numeroCpf){
        return usuarioSearch.isExisteUsuarioByNumeroCpf(numeroCpf);
    }

    private ServidorVO convertDadosServidorResponse(DadosServidorResponseBean dadosServidorResponseBean) {
        ServidorVO servidor = new ServidorVO();
        servidor.setCpf(dadosServidorResponseBean.getCpf());
        servidor.setNomeCompleto(dadosServidorResponseBean.getNome());
        servidor.setCargoFuncao(!StringUtil.isEmpty(dadosServidorResponseBean.getCargoCarreira())?
        		dadosServidorResponseBean.getCargoCarreira(): dadosServidorResponseBean.getCargoComissao());
        servidor.setDepartamento(dadosServidorResponseBean.getLocalTrabalho());
        servidor.setSecretaria(dadosServidorResponseBean.getOrgao());
        servidor.setEmail(dadosServidorResponseBean.getServidorEmail());

        Servidor servidorByCpf = getServidorByCpf(dadosServidorResponseBean.getCpf());
        if (servidorByCpf != null) {
            servidor.setId(servidorByCpf.getId());
            servidor.setTelefone(servidorByCpf.getTelefone());
            if (StringUtil.isEmpty(dadosServidorResponseBean.getServidorEmail())) {
                servidor.setEmail(servidorByCpf.getEmail());
            }
        }

        return servidor;
    }

    public Servidor getServidorByCpf(String cpf) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Servidor> query = cb.createQuery(Servidor.class);
        Root<Servidor> sv = query.from(Servidor.class);

        query.select(sv);

        query.where(cb.equal(sv.get(Servidor_.cpf), cpf));

        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
