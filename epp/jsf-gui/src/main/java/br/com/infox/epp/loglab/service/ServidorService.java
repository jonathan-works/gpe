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
import br.com.infox.seam.exception.ValidationException;

@Stateless
public class ServidorService extends PersistenceController {

	@Inject
	@GenericDao
	private Dao<Servidor, Long> servidorDAO;

    @Inject
    private UsuarioLoginManager usuarioLoginManager;

    @Inject
    private ServidorSearch servidorSearch;

	public void gravar(ServidorVO servidorVO) {
		Servidor servidor = servidorFromServidorVO(servidorVO);
		
		if (servidor.getId() == null) {
		    if(servidorSearch.isExisteUsuarioServidor(servidorVO.getCpf())) {
		        throw new ValidationException("Já existe um usuário cadastrado para este CPF.");
		    }
		    
            servidorDAO.persist(servidor);
            if (servidor.getId() != null) {
                servidorVO.setId(servidor.getId());
                UsuarioLogin usuarioLogin = usuarioLoginFromContribuinteSolicitanteVO(servidorVO);
                usuarioLoginManager.persist(usuarioLogin, true);
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

    private UsuarioLogin usuarioLoginFromContribuinteSolicitanteVO(ServidorVO vo) {
        UsuarioLogin usuarioLogin = new UsuarioLogin();
        usuarioLogin.setLogin(vo.getCpf());
        usuarioLogin.setNomeUsuario(vo.getNomeCompleto());
        usuarioLogin.setEmail(vo.getEmail());
        usuarioLogin.setAtivo(true);
        usuarioLogin.setTipoUsuario(UsuarioEnum.H);
        return usuarioLogin;
    }
}
