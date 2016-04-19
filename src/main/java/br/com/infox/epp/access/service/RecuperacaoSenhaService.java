package br.com.infox.epp.access.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.RandomStringUtils;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.entity.RecuperacaoSenha;
import br.com.infox.epp.access.entity.RecuperacaoSenha_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.mail.service.AccessMailService;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RecuperacaoSenhaService implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int CODE_MINUTES_EXPIRE = 5; // Esta constante define o número de minutos em que o código será considerado expirado
	private final int REQUEST_CODE_LENGH = 5;

	@Inject
	private AccessMailService accessMailService;
	@Inject
	private PasswordService passwordService;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void requisitarCodigoRecuperacao(UsuarioLogin usuario) {
		RecuperacaoSenha newRequest = createNewRequest(usuario);
		getEntityManager().persist(newRequest);
		getEntityManager().flush();
		String conteudo = createConteudoRequestNewEmail(newRequest);
		String subject = InfoxMessages.getInstance().get("usuario.senha.generated.subject");
		accessMailService.enviarEmail(conteudo, subject, usuario);
	}

	private String createConteudoRequestNewEmail(RecuperacaoSenha newRequest) {
		String texto = "<p>Este é um email de recuperação de senha para o usuário <strong>" + newRequest.getUsuarioLogin().getLogin() + "</strong>.</p>"
				+ "<p>O código para alteração da senha é <strong>" + newRequest.getCodigo() + "</strong></p>"
				+ "<p>Este código irá expirar em <strong>" + CODE_MINUTES_EXPIRE + " minutos e só poderá ser utilizado uma vez</strong>.</p>"
				+ "<p>Caso não tenha solicitado uma troca de senha, favor ignorar este email.</p>";
		return texto;
	}

	private RecuperacaoSenha createNewRequest(UsuarioLogin usuario) {
		RecuperacaoSenha rs = new RecuperacaoSenha();
		rs.setCodigo(generateRequestCode(usuario));
		rs.setDataCriacao(new Date());
		rs.setUsuarioLogin(usuario);
		rs.setUtilizado(false);
		return rs;
	}

	private String generateRequestCode(UsuarioLogin usuario) {
		String code = RandomStringUtils.randomAlphanumeric(REQUEST_CODE_LENGH);
		while (codeAlreadyExists(code, usuario)) {
			code = RandomStringUtils.randomAlphanumeric(REQUEST_CODE_LENGH);
		}
		return code;
	}

	private Boolean codeAlreadyExists(String code, UsuarioLogin usuario) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<RecuperacaoSenha> rs = cq.from(RecuperacaoSenha.class);
		cq.select(cb.literal(1));
		cq.where(cb.equal(rs.get(RecuperacaoSenha_.codigo), code),
				cb.equal(rs.get(RecuperacaoSenha_.usuarioLogin), usuario),
				cb.greaterThan(rs.get(RecuperacaoSenha_.dataCriacao), getExpireDate()));
		List<Integer> resultList = getEntityManager().createQuery(cq).getResultList();
		return resultList != null && !resultList.isEmpty() && resultList.get(0).equals(1);
	}

	public RecuperacaoSenha getByCodigoAndUsuario(String codigo, UsuarioLogin usuario) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RecuperacaoSenha> cq = cb.createQuery(RecuperacaoSenha.class);
		Root<RecuperacaoSenha> rs = cq.from(RecuperacaoSenha.class);
		cq.select(rs);
		cq.where(cb.equal(rs.get(RecuperacaoSenha_.usuarioLogin), usuario),
				cb.equal(rs.get(RecuperacaoSenha_.codigo), codigo));
		List<RecuperacaoSenha> resultList = getEntityManager().createQuery(cq).getResultList();
		return resultList != null && !resultList.isEmpty() ? resultList.get(0) : null;
	}

	public Boolean verificarValidadeCodigo(String codigo, UsuarioLogin usuario) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<RecuperacaoSenha> rs = cq.from(RecuperacaoSenha.class);
		cq.select(cb.literal(1));
		cq.where(cb.equal(rs.get(RecuperacaoSenha_.codigo), codigo),
				cb.equal(rs.get(RecuperacaoSenha_.usuarioLogin), usuario),
				cb.isFalse(rs.get(RecuperacaoSenha_.utilizado)),
				cb.greaterThan(rs.get(RecuperacaoSenha_.dataCriacao), getExpireDate()));
		
		List<Integer> resultList = getEntityManager().createQuery(cq).getResultList();
		return resultList != null && !resultList.isEmpty() && resultList.get(0).equals(1);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void changePassword(UsuarioLogin usuario, String newPassword, String codigo) {
		passwordService.changePassword(usuario, newPassword);
		RecuperacaoSenha rs = getByCodigoAndUsuario(codigo, usuario);
		marcarCodigoUtilizado(rs);
		getEntityManager().flush();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void marcarCodigoUtilizado(RecuperacaoSenha recuperacaoSenha) {
		recuperacaoSenha.setUtilizado(true);
		getEntityManager().merge(recuperacaoSenha);
	}

	private Date getExpireDate() {
		return new Date(new Date().getTime() - (CODE_MINUTES_EXPIRE  * 60 * 1000));
	}

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
}
