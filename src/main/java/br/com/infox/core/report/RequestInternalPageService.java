package br.com.infox.core.report;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import br.com.infox.core.exception.FailResponseAction;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Singleton
@Startup
@Named
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class RequestInternalPageService implements Serializable {

	public static final LogProvider LOG = Logging
			.getLogProvider(RequestInternalPageService.class);
	
	/**
	 * TODO: como está implementada a classe depende do jboss pois é necessario sabe o ip e a porta do servidor que está rodando.
	 * Uma alternativa é passar o <ip>:<port> como parâmetro de inicialização ou colocar na configuração do servidor de aplicação um jndi 
	 * que informe o parâmetro   
	 */
	private static final String JBOSS_HTTP_SOCKET_BINDING = "jboss.as:socket-binding-group=standard-sockets,socket-binding=http";
	private static final String JBOSS_HTTPS_SOCKET_BINDING = "jboss.as:socket-binding-group=standard-sockets,socket-binding=https";
	private static final long serialVersionUID = 1L;
	public static final String KEY_HEADER_NAME = "X-Key";

	private ObjectName socketBindingMBean;
	private SocketBindingType sbt;
	private MBeanServer mBeanServer;

	private String contextPath;
	private UUID key;

	@PostConstruct
	private void init() {
		key = UUID.randomUUID();
	}

	public UUID getKey() {
		return key;
	}

	public boolean isValid(String candidateKey) {
		try {
			UUID candidate = UUID.fromString(candidateKey);
			return key.equals(candidate);
		} catch (NullPointerException | IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Método que retorna um xhtml do sistema em String.
	 * 
	 * @param pagePath
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public String getInternalPage(String pagePath) throws HttpException, IOException {
		buildSocketBindingInfo();
		
		Integer port = getServerListeningPort();
		String host = InetAddress.getLocalHost().getHostAddress();

		StringBuilder stringBuilder = new StringBuilder(sbt.getDescricao());
		stringBuilder.append(host);
		stringBuilder.append(":");
		stringBuilder.append(port);
		stringBuilder.append(this.getContextPath());
		stringBuilder.append(pagePath);

		return requestInternalPage(stringBuilder.toString());

	}

	private Integer getServerListeningPort() {
		try {
			//em alguns AS servers a propriedade é "port" ou inver e "boundPort"
			return (Integer) mBeanServer.getAttribute(socketBindingMBean,
					"boundPort");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException e) {
			LOG.error(e);
		}
		return sbt.defaultPort;
	}

	private void buildSocketBindingInfo() {
		if(sbt != null)
			return;
		
		try {
			mBeanServer = ManagementFactory.getPlatformMBeanServer();

			socketBindingMBean = new ObjectName(JBOSS_HTTPS_SOCKET_BINDING);
			//em alguns AS servers a propriedade é inet-address ao invés de boundAddress
			String  boundAddress = (String) mBeanServer.getAttribute(socketBindingMBean, "boundAddress");
			if (boundAddress == null) {
				socketBindingMBean = new ObjectName(JBOSS_HTTP_SOCKET_BINDING);
				sbt = SocketBindingType.HTTP;
			} else {
				sbt = SocketBindingType.HTTPS;
			}
		} catch (MalformedObjectNameException | AttributeNotFoundException
				| InstanceNotFoundException | MBeanException
				| ReflectionException e) {
			LOG.error(e);
		}
	}

	private String requestInternalPage(String fullPath) throws IOException,
			HttpException {
		HttpClient client = new HttpClient();
		HttpMethod getMethod = new GetMethod(fullPath);
		// Adicionando Header para controle da segurança do sistema
		getMethod.addRequestHeader(KEY_HEADER_NAME, getKey().toString());
		client.executeMethod(getMethod);
		Header errorHeader = getMethod.getResponseHeader(FailResponseAction.HEADER_ERROR_RESPONSE);
		if (errorHeader != null && !errorHeader.getValue().isEmpty()) {
		    throw new BusinessException("A requisição interna falhou");
		}
		return getMethod.getResponseBodyAsString();
	}

	public String getContextPath() {
		return this.contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	enum SocketBindingType {

		HTTP("http://", 80), HTTPS("https://", 443);

		private String descricao;
		private Integer defaultPort;

		SocketBindingType(String description, Integer port) {
			this.setDescricao(description);
			this.setDefaultPort(port);
		}

		public String getDescricao() {
			return descricao;
		}

		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}

		public Integer getDefaultPort() {
			return defaultPort;
		}

		public void setDefaultPort(Integer defaultPort) {
			this.defaultPort = defaultPort;
		}
	}
}
