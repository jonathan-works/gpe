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
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.parametro.ParametroDefinition.Precedencia;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EppParametroProvider implements Serializable, ParametroProvider {

	private static final long serialVersionUID = 1L;

	private List<ParametroDefinition<?>> parametroDefinitions;

	@PostConstruct
	public void init() {
		parametroDefinitions = new ArrayList<>();
		for (Parametros parametro : Parametros.values()) {
                    if (parametro.getParametroDefinition() != null)
                        parametroDefinitions.add(parametro.getParametroDefinition());
                }
		initParametrosControleAcesso();
		initParametrosComunicacao();
		initParametrosAnaliseDocumento();
		initParametrosExecFluxo();
		initParametrosSistema();
		initParametrsoLog();
	    create("consultaExterna", "ativaConsultaExternaPadrao", FieldType.BOOLEAN);
	}

	private void initParametrosControleAcesso() {
		final String grupo = "controleAcesso";
		create(grupo, "termoAdesao", ModeloDocumento_.tituloModeloDocumento, ModeloDocumento_.tituloModeloDocumento)
				.addFilter(isTrue(ModeloDocumento_.ativo));
		create(grupo, "usuarioExternoPodeVerDocExcluido", FieldType.BOOLEAN);
		create(grupo, "somenteUsuarioInternoVerMotivoExclusaoDoc", FieldType.BOOLEAN);
		create(grupo, "authorizationSecret", FieldType.STRING);
		create(grupo, "webserviceToken", FieldType.STRING);
		create(grupo, "externalAuthenticationServiceUrl", FieldType.STRING);
		create(grupo, "ldapDomainName", FieldType.STRING);
		create(grupo, "ldapProviderUrl", FieldType.STRING);
		create(grupo, "recaptchaPrivateKey", FieldType.STRING);
		create(grupo, "recaptchaPublicKey", FieldType.STRING);
		create(grupo, "usuarioInterno", Papel_.nome, Papel_.identificador);
		create(grupo, "usuarioExterno", Papel_.nome, Papel_.identificador);
	}

	private void initParametrosExecFluxo() {
		create("fluxo", "idUsuarioProcessoSistema", UsuarioLogin_.nomeUsuario, UsuarioLogin_.idUsuarioLogin)
				.addFilter(equal(UsuarioLogin_.tipoUsuario, UsuarioEnum.S)).addFilter(isTrue(UsuarioLogin_.ativo))
				.addFilter(isFalse(UsuarioLogin_.bloqueio));
		// Não deveria ser configurado por fluxo?
		create("fluxo", "pastaDocumentoGerado", ModeloPasta_.descricao, ModeloPasta_.nome);
	}

	private void initParametrosAnaliseDocumento() {
		create("analiseDocumento", "codigoFluxoDocumento", Fluxo_.fluxo, Fluxo_.codFluxo)
				.addFilter(isTrue(Fluxo_.ativo)).addFilter(isTrue(Fluxo_.publicado));
	}

	private void initParametrosComunicacao() {
		create("comunicacao", "raizLocalizacoesComunicacao", Localizacao_.localizacao, Localizacao_.localizacao)
				.addFilter(isNull(Localizacao_.estruturaPai)).addFilter(isNull(Localizacao_.estruturaFilho))
				.addFilter(isTrue(Localizacao_.ativo));
		create("comunicacao", "codigoFluxoComunicacao", Fluxo_.fluxo, Fluxo_.codFluxo).addFilter(isTrue(Fluxo_.ativo))
				.addFilter(isTrue(Fluxo_.publicado));
		create("comunicacao", "codigoFluxoComunicacaoNaoEletronico", Fluxo_.fluxo, Fluxo_.codFluxo).addFilter(isTrue(Fluxo_.ativo))
		.addFilter(isTrue(Fluxo_.publicado));
	}

	private void initParametrosSistema() {
		create("sistema", "nomeSistema", FieldType.STRING);
		create("sistema", "emailSistema", FieldType.STRING);
		create("sistema", "subNomeSistema", FieldType.STRING);
		create("sistema", "exportarXLS", FieldType.BOOLEAN);
		create("sistema", "exportarPDF", FieldType.BOOLEAN);
		create("sistema", "producao", FieldType.BOOLEAN);

		create("sistema", "idUsuarioSistema", UsuarioLogin_.nomeUsuario, UsuarioLogin_.idUsuarioLogin)
				.addFilter(isTrue(UsuarioLogin_.ativo)).addFilter(equal(UsuarioLogin_.tipoUsuario, UsuarioEnum.S))
				.addFilter(isFalse(UsuarioLogin_.bloqueio));
		create("sistema", "tituloModeloEmailMudancaSenha", ModeloDocumento_.tituloModeloDocumento,
				ModeloDocumento_.tituloModeloDocumento).addFilter(isTrue(ModeloDocumento_.ativo));
		create("sistema", "tituloModeloEmailMudancaSenhaComLogin", ModeloDocumento_.tituloModeloDocumento,
				ModeloDocumento_.tituloModeloDocumento).addFilter(isTrue(ModeloDocumento_.ativo));
		create("sistema", Parametros.CODIGO_UF_SISTEMA.getLabel(), FieldType.STRING);
	}

	private void initParametrsoLog() {
		create("envioLog", "codigoClienteEnvioLog", FieldType.STRING);
		create("envioLog", "passwordClienteEnvioLog", FieldType.STRING);
		create("envioLog", "ativarServicoEnvioLogAutomatico", FieldType.BOOLEAN);
		create("envioLog", "urlServicoEnvioLogErro", FieldType.STRING);
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
