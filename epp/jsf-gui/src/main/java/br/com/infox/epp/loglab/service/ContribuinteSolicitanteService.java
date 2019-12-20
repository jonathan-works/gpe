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
import br.com.infox.epp.loglab.search.ContribuinteSolicitanteSearch;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaService;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.seam.exception.ValidationException;

@Stateless
public class ContribuinteSolicitanteService extends PersistenceController {

	@Inject
	@GenericDao
	private Dao<ContribuinteSolicitante, Long> contribuinteSolicitanteDAO;

	@Inject
	private PessoaService pessoaService;
	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
    private ContribuinteSolicitanteSearch contribuinteSolicitanteSearch;

	public ContribuinteSolicitanteVO gravar(ContribuinteSolicitanteVO vo) {
		ContribuinteSolicitante solicitante = solicitanteFromContribuinteSolicitanteVO(vo);
		if (solicitante.getId() == null) {
	        if (contribuinteSolicitanteSearch.isExisteUsuarioContribuinteSolicitante(vo.getCpf())) {
	            throw new ValidationException("Já existe um usuário cadastrado para este CPF.");
	        }

	        contribuinteSolicitanteDAO.persist(solicitante);
			if (solicitante.getId() != null) {
				vo.setId(solicitante.getId());

				PessoaFisica pessoaFisica = pessoaFisicaFromContribuinteSolicitanteVO(vo);
				pessoaService.persist(pessoaFisica);
				if (pessoaFisica.getIdPessoa() != null) {
					UsuarioLogin usuarioLogin = usuarioLoginFromContribuinteSolicitanteVO(vo, pessoaFisica);
					usuarioLoginManager.persist(usuarioLogin, Boolean.TRUE);
				}
			}
		} else {
			contribuinteSolicitanteDAO.update(solicitante);
		}
		return vo;
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

	private PessoaFisica pessoaFisicaFromContribuinteSolicitanteVO(ContribuinteSolicitanteVO vo) {
		PessoaFisica pessoaFisica = new PessoaFisica();
		pessoaFisica.setTipoPessoa(TipoPessoaEnum.F);
		pessoaFisica.setNome(vo.getNomeCompleto());
		pessoaFisica.setAtivo(Boolean.TRUE);
		pessoaFisica.setCpf(vo.getCpf());
		pessoaFisica.setDataNascimento(vo.getDataNascimento());
		return pessoaFisica;
	}

	private UsuarioLogin usuarioLoginFromContribuinteSolicitanteVO(ContribuinteSolicitanteVO vo, PessoaFisica pessoaFisica) {
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
