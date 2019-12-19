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
import br.com.infox.epp.loglab.search.ServidorSearch;
import br.com.infox.epp.loglab.vo.ServidorVO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaService;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.seam.exception.ValidationException;

@Stateless
public class ServidorService extends PersistenceController {

	@Inject
	@GenericDao
	private Dao<Servidor, Long> servidorDAO;

	@Inject
	private PessoaService pessoaService;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    @Inject
    private ServidorSearch servidorSearch;

	public void gravar(ServidorVO servidorVO) {
		Servidor servidor = servidorFromServidorVO(servidorVO);
		if (servidor.getId() == null) {
		    if (servidorSearch.isExisteUsuarioServidor(servidorVO.getCpf())) {
		        throw new ValidationException("Já existe um usuário cadastrado para este CPF.");
		    }

            servidorDAO.persist(servidor);
            if (servidor.getId() != null) {
                servidorVO.setId(servidor.getId());

				PessoaFisica pessoaFisica = pessoaFisicaFromServidorVO(servidorVO);
				pessoaService.persist(pessoaFisica);
				if (pessoaFisica.getIdPessoa() != null) {
					UsuarioLogin usuarioLogin = usuarioLoginFromServidorVO(servidorVO, pessoaFisica);
					usuarioLoginManager.persist(usuarioLogin, Boolean.TRUE);
				}
            }
        } else {
            servidorDAO.update(servidor);
        }
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

	private PessoaFisica pessoaFisicaFromServidorVO(ServidorVO vo) {
		PessoaFisica pessoaFisica = new PessoaFisica();
		pessoaFisica.setTipoPessoa(TipoPessoaEnum.F);
		pessoaFisica.setNome(vo.getNomeCompleto());
		pessoaFisica.setAtivo(Boolean.TRUE);
		pessoaFisica.setCpf(vo.getCpf());
		return pessoaFisica;
	}

    private UsuarioLogin usuarioLoginFromServidorVO(ServidorVO vo, PessoaFisica pessoaFisica) {
        UsuarioLogin usuarioLogin = new UsuarioLogin();
        usuarioLogin.setLogin(vo.getCpf());
        usuarioLogin.setNomeUsuario(vo.getNomeCompleto());
        usuarioLogin.setEmail(vo.getEmail());
        usuarioLogin.setAtivo(Boolean.TRUE);
        usuarioLogin.setTipoUsuario(UsuarioEnum.H);
		usuarioLogin.setPessoaFisica(pessoaFisica);
        return usuarioLogin;
    }
}
