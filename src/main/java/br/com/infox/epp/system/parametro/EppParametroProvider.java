package br.com.infox.epp.system.parametro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Papel_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPasta_;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EppParametroProvider implements Serializable, ParametroProvider {

	private static final long serialVersionUID = 1L;

	private List<ParametroDefinition> parametroDefinitions;
	
	@PostConstruct
	public void init(){
		parametroDefinitions = new ArrayList<>();
		parametroDefinitions.add(new ParametroDefinition("sistema", "nomeSistema", String.class));
		parametroDefinitions.add(new ParametroDefinition("sistema", "emailSistema", String.class));
		parametroDefinitions.add(new ParametroDefinition("sistema", "subNomeSistema", String.class));
		parametroDefinitions.add(new ParametroDefinition("sistema", "idUsuarioSistema", UsuarioLogin.class, UsuarioLogin_.nomeUsuario, UsuarioLogin_.idUsuarioLogin));
		parametroDefinitions.add(new ParametroDefinition("sistema", "exportarXLS", Boolean.class));
		parametroDefinitions.add(new ParametroDefinition("sistema", "exportarPDF", Boolean.class));
		parametroDefinitions.add(new ParametroDefinition("sistema", "tituloModeloEmailMudancaSenha", ModeloDocumento.class, ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.tituloModeloDocumento));
		parametroDefinitions.add(new ParametroDefinition("sistema", "tituloModeloEmailMudancaSenhaComLogin", ModeloDocumento.class, ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.tituloModeloDocumento));
		parametroDefinitions.add(new ParametroDefinition("controle de acesso", "termoAdesao", ModeloDocumento.class, ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.tituloModeloDocumento));
		
		parametroDefinitions.add(new ParametroDefinition("documento", "usuarioExternoPodeVerDocExcluido", Boolean.class));
		parametroDefinitions.add(new ParametroDefinition("documento", "somenteUsuarioInternoVerMotivoExclusaoDoc", Boolean.class));
		parametroDefinitions.add(new ParametroDefinition("recaptcha", "recaptchaPrivateKey", String.class));
		parametroDefinitions.add(new ParametroDefinition("recaptcha", "recaptchaPublicKey", String.class));
		parametroDefinitions.add(new ParametroDefinition("twitter", "oauthConsumerKey", String.class));
		parametroDefinitions.add(new ParametroDefinition("twitter", "oauthConsumerSecret", String.class));
		parametroDefinitions.add(new ParametroDefinition("comunicacao", "raizLocalizacoesComunicacao", Localizacao.class, Localizacao_.localizacao, Localizacao_.localizacao));
		parametroDefinitions.add(new ParametroDefinition("comunicacao", "codigoFluxoComunicacao", Fluxo.class, Fluxo_.fluxo, Fluxo_.codFluxo));
		parametroDefinitions.add(new ParametroDefinition("analise de documento", "codigoFluxoDocumento", Fluxo.class, Fluxo_.fluxo, Fluxo_.codFluxo));
		parametroDefinitions.add(new ParametroDefinition("fluxo", "idUsuarioProcessoSistema", UsuarioLogin.class, UsuarioLogin_.nomeUsuario, UsuarioLogin_.idUsuarioLogin));
		parametroDefinitions.add(new ParametroDefinition("ldap", "ldapDomainName", String.class));
		parametroDefinitions.add(new ParametroDefinition("ldap", "ldapProviderUrl", String.class));
		parametroDefinitions.add(new ParametroDefinition("sistema", "usuarioInterno", Papel.class, Papel_.nome, Papel_.identificador));
		parametroDefinitions.add(new ParametroDefinition("sistema", "usuarioExterno", Papel.class, Papel_.nome, Papel_.identificador));
		parametroDefinitions.add(new ParametroDefinition("sistema", "pastaDocumentoGerado", ModeloPasta.class, ModeloPasta_.descricao, ModeloPasta_.nome));
	}
	
	@Override
	public List<ParametroDefinition> getParametroDefinitions() {
		return parametroDefinitions;
	}

}
