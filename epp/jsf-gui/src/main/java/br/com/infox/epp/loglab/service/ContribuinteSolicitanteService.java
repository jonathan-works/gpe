package br.com.infox.epp.loglab.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ContribuinteSolicitanteService extends PersistenceController {

    @Inject
    @GenericDao
    private Dao<ContribuinteSolicitante, Long> contribuinteSolicitanteDAO;

    @Inject
    private PessoaFisicaManager pessoaFisicaManager;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;

    public ContribuinteSolicitanteVO gravar(ContribuinteSolicitanteVO vo) {
        ContribuinteSolicitante solicitante = solicitanteFromContribuinteSolicitanteVO(vo);

        PessoaFisica pf = salvarPessoaFisica(vo);
        solicitante.setPessoaFisica(pf);
        salvarUsuarioLogin(vo, pf);
        if (solicitante.getId() == null) {
            solicitante = contribuinteSolicitanteDAO.persist(solicitante);
            if (solicitante.getId() != null) {
                vo.setId(solicitante.getId());
            }
        } else {
            contribuinteSolicitanteDAO.update(solicitante);
        }
        return vo;
    }

    private void salvarUsuarioLogin(ContribuinteSolicitanteVO vo, PessoaFisica pf) {
        UsuarioLogin usuLogin = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pf);
        if(usuLogin == null) {
            usuLogin = new UsuarioLogin();
            usuLogin.setLogin(vo.getCpf());
            usuLogin.setAtivo(Boolean.TRUE);
            usuLogin.setTipoUsuario(UsuarioEnum.H);
            usuLogin.setPessoaFisica(pf);
        }
        usuLogin.setNomeUsuario(vo.getNomeCompleto());
        usuLogin.setEmail(vo.getEmail());
        if (usuLogin.getIdUsuarioLogin() != null) {
            usuLogin = usuarioLoginManager.update(usuLogin);
        } else {
            usuLogin = usuarioLoginManager.persist(usuLogin);
        }
    }

    private PessoaFisica salvarPessoaFisica(ContribuinteSolicitanteVO vo) {
        PessoaFisica pf = pessoaFisicaManager.getByCpf(vo.getCpf());
        if(pf == null) {
            pf = new PessoaFisica();
        }
        pf.setTipoPessoa(TipoPessoaEnum.F);
        pf.setNome(vo.getNomeCompleto());
        pf.setAtivo(Boolean.TRUE);
        pf.setCpf(vo.getCpf());
        pf.setDataNascimento(vo.getDataNascimento());
        if(pf.getIdPessoa() != null) {
            pf = pessoaFisicaManager.update(pf);
        } else {
            pf = pessoaFisicaManager.persist(pf);
        }
        return pf;
    }

    private ContribuinteSolicitante solicitanteFromContribuinteSolicitanteVO(ContribuinteSolicitanteVO vo) {
        ContribuinteSolicitante solicitante = new ContribuinteSolicitante();
        solicitante.setId(vo.getId());
        solicitante.setTipoContribuinte(vo.getTipoContribuinte());
        solicitante.setCpf(vo.getCpf());
        solicitante.setMatricula(vo.getMatricula());
        solicitante.setNomeCompleto(vo.getNomeCompleto());
        solicitante.setSexo(vo.getSexo());
        solicitante.setDataNascimento(vo.getDataNascimento());
        solicitante.setNumeroRg(vo.getNumeroRg());
        solicitante.setEmissorRg(vo.getEmissorRg());
        solicitante.setEstadoRg(getEntityManager().getReference(Estado.class, vo.getIdEstadoRg()));
        solicitante.setNomeMae(vo.getNomeMae());
        solicitante.setEmail(vo.getEmail());
        solicitante.setTelefone(vo.getTelefone());
        solicitante.setCidade(vo.getCidade());
        solicitante.setLogradouro(vo.getLogradouro());
        solicitante.setBairro(vo.getBairro());
        solicitante.setComplemento(vo.getComplemento());
        solicitante.setNumero(vo.getNumero());
        solicitante.setCep(vo.getCep());
        return solicitante;
    }

}
