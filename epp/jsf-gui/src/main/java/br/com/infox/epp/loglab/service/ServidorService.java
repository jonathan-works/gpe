package br.com.infox.epp.loglab.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.loglab.model.Servidor;
import br.com.infox.epp.loglab.vo.ServidorVO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;

@Stateless
public class ServidorService extends PersistenceController {

	@Inject
	@GenericDao
	private Dao<Servidor, Long> servidorDAO;

    @Inject
    private PessoaFisicaManager pessoaFisicaManager;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;

    public ServidorVO gravar(ServidorVO vo) {
        Servidor servidor = servidorFromServidorVO(vo);

        PessoaFisica pf = salvarPessoaFisica(vo);
        servidor.setPessoaFisica(pf);
        salvarUsuarioLogin(vo, pf);
        if (servidor.getId() == null) {
            servidor = servidorDAO.persist(servidor);
            if (servidor.getId() != null) {
                vo.setId(servidor.getId());
            }
        } else {
            servidorDAO.update(servidor);
        }
        return vo;
    }

	private PessoaFisica salvarPessoaFisica(ServidorVO vo) {
        PessoaFisica pf = pessoaFisicaManager.getByCpf(vo.getCpf());
        if (pf == null) {
            pf = new PessoaFisica();
        }
        pf.setTipoPessoa(TipoPessoaEnum.F);
        pf.setNome(vo.getNomeCompleto());
        pf.setAtivo(Boolean.TRUE);
        pf.setCpf(vo.getCpf());
        if (pf.getIdPessoa() != null) {
            pf = pessoaFisicaManager.update(pf);
        } else {
            pf = pessoaFisicaManager.persist(pf);
        }
        return pf;
	}

    private UsuarioLogin salvarUsuarioLogin(ServidorVO vo, PessoaFisica pf) {
        UsuarioLogin ul = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pf);
        if (ul == null) {
            ul = new UsuarioLogin();
            ul.setLogin(vo.getCpf());
            ul.setAtivo(Boolean.TRUE);
            ul.setTipoUsuario(UsuarioEnum.H);
            ul.setPessoaFisica(pf);
        }
        ul.setNomeUsuario(vo.getNomeCompleto());
        ul.setEmail(vo.getEmail());
        if (ul.getIdUsuarioLogin() != null) {
            ul = usuarioLoginManager.update(ul);
        } else {
            ul = usuarioLoginManager.persist(ul);
        }
        return ul;
    }

	private Servidor servidorFromServidorVO(ServidorVO vo) {
		Servidor servidor = new Servidor();
		servidor.setId(vo.getId());
		servidor.setCpf(vo.getCpf());
		servidor.setNomeCompleto(vo.getNomeCompleto());
		servidor.setCargoFuncao(vo.getCargoFuncao());
		servidor.setEmail(vo.getEmail());
		servidor.setTelefone(vo.getTelefone());
		servidor.setSecretaria(vo.getSecretaria());
		servidor.setDepartamento(vo.getDepartamento());
		return servidor;
	}

}
