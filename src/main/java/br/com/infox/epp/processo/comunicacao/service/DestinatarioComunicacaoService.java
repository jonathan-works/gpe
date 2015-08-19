package br.com.infox.epp.processo.comunicacao.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.system.Parametros;

@AutoCreate
@Name(DestinatarioComunicacaoService.NAME)
public class DestinatarioComunicacaoService implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "destinatarioComunicacaoService";
	
	@In
	private UsuarioPerfilManager usuarioPerfilManager;
	@In
	private PapelManager papelManager;
	@In
	private LocalizacaoManager localizacaoManager;
	@In
	private String raizLocalizacoesComunicacao;
	
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
