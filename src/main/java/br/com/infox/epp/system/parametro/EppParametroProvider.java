package br.com.infox.epp.system.parametro;

import static br.com.infox.epp.Filter.equal;
import static br.com.infox.epp.Filter.isFalse;
import static br.com.infox.epp.Filter.isNull;
import static br.com.infox.epp.Filter.isTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.metamodel.SingularAttribute;

import br.com.infox.epp.FieldType;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.Papel_;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.ModeloPasta_;
import br.com.infox.epp.system.parametro.ParametroDefinition.Precedencia;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EppParametroProvider implements Serializable, ParametroProvider {

	private static final long serialVersionUID = 1L;

	private List<ParametroDefinition<?>> parametroDefinitions;

	@PostConstruct
	public void init() {
		parametroDefinitions = new ArrayList<>();
		initParametrosControleAcesso();
		initParametrosReCaptcha();
		initParametrosTwitter();
		initParametrosComunicacao();
		initParametrosAnaliseDocumento();
		initParametrosExecFluxo();
		initParametrosLDAP();
		initParametrosSistema();
		create("loginWebService", "authorizationSecret", FieldType.STRING);
	    create("externalAuthenticationService","externalAuthenticationServiceUrl", FieldType.STRING);		
	}

	private void initParametrosControleAcesso() {
		final String grupo = "controle de acesso";
		create(grupo, "termoAdesao", ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.tituloModeloDocumento)
				.addFilter(isTrue(ModeloDocumento_.ativo));
		create(grupo, "usuarioExternoPodeVerDocExcluido", FieldType.BOOLEAN);
		create(grupo, "somenteUsuarioInternoVerMotivoExclusaoDoc", FieldType.BOOLEAN);
	}

	private void initParametrosReCaptcha() {
		create("recaptcha", "recaptchaPrivateKey", FieldType.STRING);
		create("recaptcha", "recaptchaPublicKey", FieldType.STRING);
	}

	private void initParametrosLDAP() {
		create("ldap", "ldapDomainName", FieldType.STRING);
		create("ldap", "ldapProviderUrl", FieldType.STRING);
	}

	private void initParametrosExecFluxo() {
		create("fluxo", "idUsuarioProcessoSistema", UsuarioLogin_.nomeUsuario, UsuarioLogin_.idUsuarioLogin)
				.addFilter(isTrue(UsuarioLogin_.ativo)).addFilter(equal(UsuarioLogin_.tipoUsuario, UsuarioEnum.S))
				.addFilter(isFalse(UsuarioLogin_.bloqueio));
	}

	private void initParametrosAnaliseDocumento() {
		create("analise de documento", "codigoFluxoDocumento", Fluxo_.fluxo, Fluxo_.codFluxo)
				.addFilter(isTrue(Fluxo_.ativo)).addFilter(isTrue(Fluxo_.publicado));
		;
	}

	private void initParametrosComunicacao() {
		create("comunicacao", "raizLocalizacoesComunicacao", Localizacao_.localizacao, Localizacao_.localizacao)
				.addFilter(isNull(Localizacao_.estruturaPai)).addFilter(isNull(Localizacao_.estruturaFilho))
				.addFilter(isTrue(Localizacao_.ativo));
		create("comunicacao", "codigoFluxoComunicacao", Fluxo_.fluxo, Fluxo_.codFluxo).addFilter(isTrue(Fluxo_.ativo))
				.addFilter(isTrue(Fluxo_.publicado));
	}

	private void initParametrosTwitter() {
		create("twitter", "oauthConsumerKey", FieldType.STRING);
		create("twitter", "oauthConsumerSecret", FieldType.STRING);
	}

	private void initParametrosSistema() {
		create("sistema", "nomeSistema", FieldType.STRING);
		create("sistema", "emailSistema", FieldType.STRING);
		create("sistema", "subNomeSistema", FieldType.STRING);
		create("sistema", "exportarXLS", FieldType.BOOLEAN);
		create("sistema", "exportarPDF", FieldType.BOOLEAN);

		create("sistema", "idUsuarioSistema", UsuarioLogin_.nomeUsuario, UsuarioLogin_.idUsuarioLogin)
				.addFilter(isTrue(UsuarioLogin_.ativo)).addFilter(equal(UsuarioLogin_.tipoUsuario, UsuarioEnum.S))
				.addFilter(isFalse(UsuarioLogin_.bloqueio));
		create("sistema", "tituloModeloEmailMudancaSenha", ModeloDocumento_.tituloModeloDocumento,
				ModeloDocumento_.tituloModeloDocumento).addFilter(isTrue(ModeloDocumento_.ativo));
		create("sistema", "tituloModeloEmailMudancaSenhaComLogin", ModeloDocumento_.tituloModeloDocumento,
				ModeloDocumento_.tituloModeloDocumento).addFilter(isTrue(ModeloDocumento_.ativo));
		create("sistema", "usuarioInterno", Papel_.nome, Papel_.identificador).addFilter(isFalse(Papel_.termoAdesao));
		create("sistema", "usuarioExterno", Papel_.nome, Papel_.identificador).addFilter(isTrue(Papel_.termoAdesao));
		create("sistema", "pastaDocumentoGerado", ModeloPasta_.descricao, ModeloPasta_.nome);
	}

	public <T> ParametroDefinition<T> create(String grupo, String nome, SingularAttribute<T, ?> keyAttribute,
			SingularAttribute<T, ?> labelAttribute) {
		return create(grupo, nome, FieldType.SELECT_ONE, keyAttribute, labelAttribute);
	}

	public <T> ParametroDefinition<T> create(String grupo, String nome, FieldType tipo,
			SingularAttribute<T, ?> keyAttribute, SingularAttribute<T, ?> labelAttribute) {
		ParametroDefinition<T> parametroDefinition = new ParametroDefinition<T>(grupo, nome, tipo, keyAttribute,
				labelAttribute, Precedencia.DEFAULT);
		parametroDefinitions.add(parametroDefinition);
		return parametroDefinition;
	}

	public <T> ParametroDefinition<T> create(String grupo, String nome, FieldType tipo) {
		return create(grupo, nome, tipo, null, null);
	}

	@Override
	public List<ParametroDefinition<?>> getParametroDefinitions() {
		return Collections.unmodifiableList(parametroDefinitions);
	}

}
