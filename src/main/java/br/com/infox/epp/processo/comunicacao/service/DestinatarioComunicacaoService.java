package br.com.infox.epp.processo.comunicacao.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.action.DestinatarioBean;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.system.Parametros;

@AutoCreate
@Name(DestinatarioComunicacaoService.NAME)
public class DestinatarioComunicacaoService implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "destinatarioComunicacaoService";
	
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private PrazoComunicacaoService prazoComunicacaoService;
	@In
	private UsuarioPerfilManager usuarioPerfilManager;
	@In
	private PapelManager papelManager;
	@In
	private LocalizacaoManager localizacaoManager;
	@In
	private String raizLocalizacoesComunicacao;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private boolean usuarioInterno = Identity.instance().hasRole(Parametros.PAPEL_USUARIO_INTERNO.getValue());
	
	public List<DestinatarioBean> getDestinatarios(ModeloComunicacao modeloComunicacao) {
		List<DestinatarioBean> destinatarios = new ArrayList<>();
		for (DestinatarioModeloComunicacao destinatarioModeloComunicacao : modeloComunicacao.getDestinatarios()) {
			if (!destinatarioModeloComunicacao.getExpedido()) {
				continue;
			}

			Processo comunicacao = modeloComunicacaoManager.getComunicacao(destinatarioModeloComunicacao);
			if (comunicacao == null) {
				continue;
			}
			boolean cienciaConfirmada = isCienciaConfirmada(modeloComunicacaoManager.getComunicacao(destinatarioModeloComunicacao));
			if (!usuarioInterno && !cienciaConfirmada) {
				continue;
			}
			destinatarios.add(createDestinatarioBean(destinatarioModeloComunicacao));
		}
		return destinatarios;
	}
	
	protected DestinatarioBean createDestinatarioBean(DestinatarioModeloComunicacao destinatario) {
		DestinatarioBean bean = instanciarDestinatarioBean();
		bean.setIdDestinatario(destinatario.getId());
		bean.setDestinatario(destinatario);
		bean.setComunicacao(modeloComunicacaoManager.getComunicacao(destinatario));
		bean.setMeioExpedicao(destinatario.getMeioExpedicao().getLabel());
		bean.setTipoComunicacao(destinatario.getModeloComunicacao().getTipoComunicacao());
		bean.setNome(destinatario.getNome());
		bean.setDocumentoComunicacao(destinatario.getDocumentoComunicacao());
		bean.setModeloComunicacao(destinatario.getModeloComunicacao());
		
		Processo comunicacao = bean.getComunicacao();
		MetadadoProcesso metadadoPrazo = comunicacao.getMetadado(ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO);
		if (metadadoPrazo != null) {
			bean.setPrazoAtendimento(metadadoPrazo.getValue().toString());
		} else {
			bean.setPrazoAtendimento("-");
		}
		bean.setDataEnvio(dateFormat.format(comunicacao.getDataInicio()));
		bean.setDataConfirmacao(getDataConfirmacao(comunicacao));
		bean.setResponsavelConfirmacao(getResponsavelConfirmacao(comunicacao));
		bean.setPrazoFinal(getPrazoFinal(comunicacao));
		bean.setPrazoOriginal(getPrazoOriginal(comunicacao));
		bean.setDataResposta(getDataResposta(comunicacao));
		bean.setStatusProrrogacao(getStatusProrrogacao(comunicacao));
		return bean;
	}
	
	protected DestinatarioBean instanciarDestinatarioBean(){
		return new DestinatarioBean();
	}
	
	private String getDataConfirmacao(Processo comunicacao) {
		MetadadoProcesso metadado = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA);
		if (metadado != null) {
			return dateFormat.format(metadado.getValue());
		}
		return "-";
	}
	
	private String getResponsavelConfirmacao(Processo comunicacao) {
		MetadadoProcesso metadado = comunicacao.getMetadado(ComunicacaoMetadadoProvider.RESPONSAVEL_CIENCIA);
		if (metadado != null) {
			UsuarioLogin usuario = metadado.getValue();
			return usuario.getNomeUsuario();
		}
		return "-";
	}
	
	private String getPrazoFinal(Processo comunicacao) {
		MetadadoProcesso metadado = comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
		if (metadado != null) {
			return dateFormat.format(metadado.getValue());
		}
		return "-";
	}
	
	private String getPrazoOriginal(Processo comunicacao) {
	    MetadadoProcesso metadado = comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO_INICIAL);
	    if (metadado != null) {
	        return dateFormat.format(metadado.getValue());
	    }
	    return "-";
	}
	

	private String getDataResposta(Processo comunicacao){
		MetadadoProcesso metadado = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CUMPRIMENTO);
		if(metadado != null){
			return new SimpleDateFormat("dd/MM/yyyy").format(metadado.getValue());
		}
		return "-";
	}
	
	private String getStatusProrrogacao(Processo comunicacao){
		return prazoComunicacaoService.getStatusProrrogacaoFormatado(comunicacao);
	}
	
	public boolean isCienciaConfirmada(Processo comunicacao) {
		return !getDataConfirmacao(comunicacao).equals("-");
	}
	
	public List<MeioExpedicao> getMeiosExpedicao(DestinatarioModeloComunicacao destinatario) {
		if (destinatario.getDestinatario() != null) {
			PessoaFisica pessoa = destinatario.getDestinatario();
			UsuarioLogin usuario = pessoa.getUsuarioLogin();
			if (pessoa.getTermoAdesao() != null) {
				return order(MeioExpedicao.values());
			}
			if (usuario != null) {
				List<UsuarioPerfil> usuarioPerfilList = usuarioPerfilManager.listByUsuarioLogin(usuario);
				List<String> papeisHerdeirosUsuarioInterno = papelManager.getIdentificadoresPapeisHerdeiros(Parametros.PAPEL_USUARIO_INTERNO.getValue());
				for (UsuarioPerfil usuarioPerfil : usuarioPerfilList) {
					Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
					if (papeisHerdeirosUsuarioInterno.contains(papel.getIdentificador())) {
						return order(MeioExpedicao.values());
					}
				}
			}
		} else {
			Localizacao localizacaoRaiz = localizacaoManager.getLocalizacaoByCodigo(raizLocalizacoesComunicacao);
			if (destinatario.getDestino().getCaminhoCompleto().startsWith(localizacaoRaiz.getCaminhoCompleto())) {
				return order(MeioExpedicao.values());
			}
		}
		
		return order(MeioExpedicao.DO, MeioExpedicao.EM, MeioExpedicao.IM);
	}
	
	private List<MeioExpedicao> order(MeioExpedicao... meios) {
		List<MeioExpedicao> meiosExpedicao = Arrays.asList(meios);
		Collections.sort(meiosExpedicao, new Comparator<MeioExpedicao>() {
			@Override
			public int compare(MeioExpedicao o1, MeioExpedicao o2) {
				return o1.getLabel().compareToIgnoreCase(o2.getLabel());
			}
		});
		return meiosExpedicao;
	}
}
