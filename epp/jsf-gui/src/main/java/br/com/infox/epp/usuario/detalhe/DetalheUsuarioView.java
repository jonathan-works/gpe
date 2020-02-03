package br.com.infox.epp.usuario.detalhe;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.access.TermoAdesaoService;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.certificadoeletronico.CertificadoEletronicoService;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DetalheUsuarioView implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String tab = "info";
	@Getter
	private String nome;
	@Getter
	private String termo;
	@Getter
	private String urlTermoAdesao;
	@Getter
	private Date dataInicio;
	@Getter
	private Date dataFim;
	@Inject
	private TermoAdesaoService termoAdesaoService;
	@Inject
	private CertificadoEletronicoService certificadoEletronicoService;
	@Getter
	private boolean possuiCertificadoEmitido = false;
	@Getter
	private boolean podeEmitirCertificado = false;
	@Getter
	private boolean podeExibirTermoAdesao = false;

	@PostConstruct
	private void init() {
		possuiCertificadoEmitido = false;
		podeEmitirCertificado = false;
		podeExibirTermoAdesao = false;
		refreshInitValues();
	}

	private void refreshInitValues() {
		UsuarioLogin usuario = Authenticator.getUsuarioLogado();
		this.nome = usuario.getNomeUsuario();
		this.podeExibirTermoAdesao = usuario.getPessoaFisica().getTermoAdesao() != null;
		if (usuario.getPessoaFisica() != null) {
			this.possuiCertificadoEmitido = usuario.getPessoaFisica().getCertificadoEletronico() != null;
			this.podeEmitirCertificado = !this.possuiCertificadoEmitido
					|| !usuario.getPessoaFisica().getCertificadoEletronico().isAtivo();
			if (this.possuiCertificadoEmitido) {
				this.dataInicio = usuario.getPessoaFisica().getCertificadoEletronico().getDataInicio();
				this.dataFim = usuario.getPessoaFisica().getCertificadoEletronico().getDataFim();
			}
		}
		if (isPodeExibirTermoAdesao()) {
			this.termo = usuario.getPessoaFisica().getTermoAdesao().getModeloDocumento();
			this.urlTermoAdesao = termoAdesaoService.buildUrlDownload(getHttpServletRequest().getContextPath(), null,
					usuario.getPessoaFisica().getTermoAdesao().getUuid().toString());

		}
	}

	@ExceptionHandled(successMessage = "Certificado gerado com sucesso")
	public void gerarCertificado() {
		certificadoEletronicoService.gerarCertificado(Authenticator.getUsuarioLogado().getPessoaFisica());
		refreshInitValues();
	}

	private HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

}