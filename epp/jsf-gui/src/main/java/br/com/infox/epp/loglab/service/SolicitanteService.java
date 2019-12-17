package br.com.infox.epp.loglab.service;

import javax.ejb.Stateless;
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

@Stateless
public class SolicitanteService extends PersistenceController {

	@Inject
	@GenericDao
	private Dao<ContribuinteSolicitante, Long> solicitanteDAO;

	@Inject
	private UsuarioLoginManager usuarioLoginManager;

	public ContribuinteSolicitante gravar(ContribuinteSolicitanteVO solicitanteVO) {
		ContribuinteSolicitante solicitante = solicitanteFromContribuinteSolicitanteVO(solicitanteVO);
		if (solicitante.getId() == null) {
			solicitanteDAO.persist(solicitante);
			if (solicitante.getId() != null) {
				UsuarioLogin usuarioLogin = usuarioLoginFromContribuinteSolicitanteVO(solicitanteVO);
				usuarioLoginManager.persist(usuarioLogin, true);
			}
		} else {
			solicitanteDAO.update(solicitante);
		}
		return solicitante;
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

	private UsuarioLogin usuarioLoginFromContribuinteSolicitanteVO(ContribuinteSolicitanteVO vo) {
		UsuarioLogin usuarioLogin = new UsuarioLogin();
		usuarioLogin.setLogin(vo.getCpf());
		usuarioLogin.setNomeUsuario(vo.getNomeCompleto());
		usuarioLogin.setEmail(vo.getEmail());
		usuarioLogin.setAtivo(true);
		usuarioLogin.setTipoUsuario(UsuarioEnum.H);
		return usuarioLogin;
	}

}
