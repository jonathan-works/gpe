package br.com.infox.epp.usuario.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.meiocontato.dao.MeioContatoDAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.documento.dao.PessoaDocumentoDAO;
import br.com.infox.epp.pessoa.documento.dao.PessoaDocumentoSearch;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.documento.type.TipoPesssoaDocumentoEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.type.EstadoCivilEnum;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.pessoaFisica.PessoaFisicaSearch;
import br.com.infox.epp.usuario.UsuarioDTOSearch;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.interceptors.ValidarParametros;

@Stateless
@TokenAuthentication
@ValidarParametros
public class UsuarioLoginRestService {

	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private UsuarioLoginSearch usuarioSearch;
	@Inject
	private UsuarioDTOSearch usuarioDTOSearch;
	@Inject
	private PessoaFisicaManager pessoaFisicaManager;
	@Inject
	private PessoaFisicaSearch pessoaFisicaSearch;
	@Inject
	private PessoaDocumentoDAO pessoaDocumentoDAO;
	@Inject
	private PessoaDocumentoSearch pessoaDocumentoSearch;
	@Inject
	private MeioContatoManager meioContatoManager;
	@Inject
	private MeioContatoDAO meioContatoDAO;
	
	public void atualizarUsuario(String cpf, UsuarioDTO usuarioDTO) {
		UsuarioLogin usuarioLogin = usuarioSearch.getUsuarioLoginByCpf(cpf);
		PessoaFisica pessoaFisica = pessoaFisicaSearch.getByCpf(cpf);
		aplicarValoresPessoaFisica(usuarioDTO, pessoaFisica);
		aplicarValoresUsuarioLogin(usuarioDTO, usuarioLogin);
		aplicarValoresPessoaDocumento(usuarioDTO.getDocumentos(), pessoaFisica);
		aplicarValoresMeioContato(usuarioDTO.getMeiosContato(), pessoaFisica);
		usuarioLoginManager.update(usuarioLogin);
	}

	public UsuarioDTO getUsuarioByCpf(String cpf) {
		UsuarioLogin usuarioLogin = usuarioSearch.getUsuarioLoginByCpf(cpf);
		PessoaFisica pessoaFisica = pessoaFisicaSearch.getByCpf(cpf);
		List<PessoaDocumento> pessoaDocumentoList = pessoaDocumentoSearch.getDocumentosByPessoa(pessoaFisica);
		List<MeioContato> meioContatoList = meioContatoManager.getByPessoa(pessoaFisica);
		return new UsuarioDTO(usuarioLogin, pessoaFisica, pessoaDocumentoList, meioContatoList);
	}

	public void removerUsuario(String cpf) {
		UsuarioLogin usuarioLogin = usuarioSearch.getUsuarioLoginByCpf(cpf);
		usuarioLogin.setAtivo(Boolean.FALSE);
		usuarioLoginManager.update(usuarioLogin);
	}
	
	private PessoaFisica getPessoaFisica(UsuarioDTO usuarioDTO) {
		PessoaFisica pessoaFisica = pessoaFisicaSearch.getByCpf(usuarioDTO.getCpf());
		if (pessoaFisica == null){
			pessoaFisica = aplicarValoresPessoaFisica(usuarioDTO, new PessoaFisica());
			pessoaFisicaManager.persist(pessoaFisica);
		}
		return pessoaFisica;
	}
	
	public void adicionarUsuario(UsuarioDTO usuarioDTO, boolean sendPasswordToMail) {
		UsuarioLogin usuarioLogin = aplicarValoresUsuarioLogin(usuarioDTO, new UsuarioLogin());
		usuarioLogin.setAtivo(Boolean.TRUE);
		usuarioLogin.setBloqueio(Boolean.FALSE);
		usuarioLogin.setProvisorio(Boolean.FALSE);
		usuarioLoginManager.persist(usuarioLogin, sendPasswordToMail);
	}
	
	public void adicionarUsuario(UsuarioDTO usuarioDTO) {
		UsuarioLogin usuarioLogin = usuarioSearch.getUsuarioLoginByCpfWhenExists(usuarioDTO.getCpf());
		if (usuarioLogin != null && !usuarioLogin.getAtivo()) {
			usuarioLogin.setAtivo(Boolean.TRUE);
			usuarioLoginManager.update(aplicarValoresUsuarioLogin(usuarioDTO, usuarioLogin));
		} else {
			adicionarUsuario(usuarioDTO, false);
		}
	}

	private UsuarioLogin aplicarValoresUsuarioLogin(UsuarioDTO usuarioDTO, UsuarioLogin usuarioLogin) {
		usuarioLogin.setEmail(usuarioDTO.getEmail());
		usuarioLogin.setNomeUsuario(usuarioDTO.getNome());
		usuarioLogin.setLogin(usuarioDTO.getCpf());
		usuarioLogin.setTipoUsuario(UsuarioDTO.metodoLogin(usuarioDTO.getMetodoLogin()));
		PessoaFisica pessoaFisica = getPessoaFisica(usuarioDTO);
		usuarioLogin.setPessoaFisica(pessoaFisica);
		aplicarValoresPessoaDocumento(usuarioDTO.getDocumentos(), pessoaFisica);
		aplicarValoresMeioContato(usuarioDTO.getMeiosContato(), pessoaFisica);
		return usuarioLogin;
	}

	private PessoaFisica aplicarValoresPessoaFisica(UsuarioDTO usuarioDTO, PessoaFisica pessoaFisica) {
		pessoaFisica.setNome(usuarioDTO.getNome());
		pessoaFisica.setCpf(usuarioDTO.getCpf());
		pessoaFisica.setTipoPessoa(TipoPessoaEnum.F);
		if (usuarioDTO.getEstadoCivil() != null) {
			pessoaFisica.setEstadoCivil(EstadoCivilEnum.valueOf(usuarioDTO.getEstadoCivil()));
		} else {
			pessoaFisica.setEstadoCivil(EstadoCivilEnum.N);
		}
		if (usuarioDTO.getDataNascimento() != null) {
			try {
				pessoaFisica.setDataNascimento(new SimpleDateFormat(ConstantesDTO.DATE_PATTERN).parse(usuarioDTO.getDataNascimento()));
			} catch (ParseException e){
				throw new WebApplicationException(e, 400);
			}
		} else {
			pessoaFisica.setDataNascimento(null);
		}
		return pessoaFisica;
	}
	
	private void aplicarValoresPessoaDocumento(List<PessoaDocumentoDTO> documentos, PessoaFisica pessoaFisica) {
		pessoaDocumentoDAO.removeAllDocumentosByPessoa(pessoaFisica);
		if (documentos != null && !documentos.isEmpty()) {
			List<PessoaDocumento> pessoaDocumentos = new ArrayList<PessoaDocumento>();
			for (PessoaDocumentoDTO pessoaDocumentoDTO : documentos) {
				PessoaDocumento pessoaDoc = new PessoaDocumento();
				pessoaDoc.setPessoa(pessoaFisica);
				pessoaDoc.setDocumento(pessoaDocumentoDTO.getDocumento());
				try {
					pessoaDoc.setDataEmissao(new SimpleDateFormat(ConstantesDTO.DATE_PATTERN).parse(pessoaDocumentoDTO.getDataEmissao()));
				} catch (ParseException e) {
					throw new WebApplicationException(e, 400); //TODO ver depois pra colocar isso aqui num mapper
				}
				pessoaDoc.setTipoDocumento(TipoPesssoaDocumentoEnum.valueOf(pessoaDocumentoDTO.getTipo()));
				pessoaDoc.setOrgaoEmissor(pessoaDocumentoDTO.getDocumento());
				pessoaDocumentos.add(pessoaDoc);
			}
			pessoaDocumentoDAO.adicionaDocumentos(pessoaDocumentos);
		}
	}
	
	private void aplicarValoresMeioContato(List<MeioContatoDTO> meiosContato, PessoaFisica pessoaFisica) {
		meioContatoManager.removeMeioContatoByPessoa(pessoaFisica);
		if (meiosContato != null && !meiosContato.isEmpty()) {
			List<MeioContato> meioContatoList = new ArrayList<>();
			for (MeioContatoDTO meioContatoDTO : meiosContato) {
				meioContatoList.add(meioContatoManager.createMeioContato(
						meioContatoDTO.getMeioContato(), pessoaFisica, TipoMeioContatoEnum.valueOf(meioContatoDTO.getTipo())));
			}
			meioContatoDAO.adicionaMeioContatos(meioContatoList);
		}
	}

	public List<UsuarioDTO> getUsuarios() {
		return usuarioDTOSearch.getUsuarioDTOList();
	}
	public List<UsuarioDTO> getUsuarios(int limit, int offset) {
	    return usuarioDTOSearch.getUsuarioDTOList(limit, offset);
	}
		
}
