/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.entity.log;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;

import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.type.TipoOperacaoLogEnum;
import br.com.itx.component.MeasureTime;
import br.com.itx.util.EntityUtil;


public class LogUtil {

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";
	private static final LogProvider LOG = Logging.getLogProvider(LogUtil.class);

	/**
	 * Checa se a classe é um array de bytes.
	 * @param type
	 * @return
	 */
	public static boolean isBinario(Class<?> type) {
		return type.isArray() && 
				type.getComponentType().getName().equals("byte"); 
	}	

	/**
	 * Checa se um atributo de um objeto é um array de bytes.
	 * @param entidade
	 * @param nomeAtributo
	 * @return
	 * @throws Exception
	 */
	public static boolean isBinario(Object entidade, String nomeAtributo) throws Exception {
		Class<?> classAtributo = getType(entidade, nomeAtributo);
		return isBinario(classAtributo);
	}

	private static Class<?> getType(Object entidade, String nomeAtributo) {
		return Reflections
					.getField(entidade.getClass(),nomeAtributo).getType();
	}		

	/**
	 * Checa se o atributo de um objeto é uma coleção.
	 * @param entidade
	 * @param nomeAtributo
	 * @return
	 * @throws Exception
	 */
	public static boolean isCollection(Object entidade, String nomeAtributo) throws Exception {
		Class<?> classAtributo = getType(entidade, nomeAtributo);
		return isCollectionClass(classAtributo);
	}

	private static boolean isCollectionClass(Class<?> classAtributo) {
		return ArrayList.class.equals(classAtributo) 
				|| List.class.equals(classAtributo) 
				|| Set.class.equals(classAtributo);
	}		
	
	/**
	 * Testa se o atributo de um objeto é considerado de tamanho pequeno para o armazenamento no log.
	 * @param entidade
	 * @param nomeAtributo
	 * @return
	 * @throws Exception
	 */
	public static boolean isSmallField(Object entidade, String nomeAtributo) throws Exception {
		Class<?> classAtributo = getType(entidade, nomeAtributo);
		if (String.class.equals(classAtributo)) {
			PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(
					entidade.getClass().newInstance(), nomeAtributo);	
			Size lengthAnnotation = pd.getReadMethod().getAnnotation(Size.class);
			return lengthAnnotation != null && lengthAnnotation.max() <= 300;
		} else {
			return !isBinario(classAtributo);
		}
	}	
	
	/**
	 * Testa se a entidade possui a anotação @Ignore, caso possua não será logada
	 * @param entidade
	 * @return
	 */
	public static boolean isLogable(Object entity) {
		return !EntityUtil.isAnnotationPresent(entity, Ignore.class);
	}	
	
	public static boolean compareObj(Object object1, Object object2) {
		if (object1 == null) {
			return object2 == null;
		} 
		return object1.equals(object2);
	}
	
	public static UsuarioLogin getUsuarioLogado() {
		UsuarioLogin usuario = (UsuarioLogin) Contexts.getSessionContext().get("usuarioLogado");
		if (usuario != null){
			usuario = EntityUtil.getEntityManager().find(UsuarioLogin.class, usuario.getIdPessoa());
		}
		return usuario;
	}			
	
	public static String getIpRequest() throws LogException {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new LogException("Não foi possível obter os dados da requisição");
		}
		return request.getRemoteAddr();		
	}
	
	public static String getUrlRequest() throws LogException {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new LogException("Não foi possível obter os dados da requisição");
		}
		return getRequest().getRequestURL().toString();
	}	
	
	public static String getIdPagina() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			return null;
		}
		String requestURL = request.getRequestURL().toString();
		return requestURL.split(request.getContextPath())[1];
	}

	public static HttpServletRequest getRequest() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc == null) {
			return null;
		} 
		return (HttpServletRequest) fc.getExternalContext().getRequest();
	}	
	
	public static String toStringForLog(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Date) {
			SimpleDateFormat date = new SimpleDateFormat(DATE_PATTERN);
			return date.format((Date) object);
		} else if (EntityUtil.isEntity(object)) {
			return EntityUtil.getEntityIdObject(object) + ": " + object.toString();
		} else {
			return object.toString();
		}
	}
	
	public static String toStringFields(Object component) {
		try {
			MeasureTime t = new MeasureTime(true);
			PropertyDescriptor[] props = Introspector.getBeanInfo(
					component.getClass()).getPropertyDescriptors();
			StringBuilder builder = new StringBuilder();
			for (PropertyDescriptor descriptor : props) {
				if (!isCollectionClass(descriptor.getPropertyType()) && descriptor.getReadMethod() != null) {
					Object field = descriptor.getReadMethod().invoke(component);
					builder.append(descriptor.getName()).append('=');
					if (field != null && EntityUtil.isEntity(field)) {
						builder.append(toStringForLog(field));
					} else {
						builder.append(field);
					}
					builder.append("; ");
				}
			}
			LOG.info("toStringFields(Object component): " + t.getTime());
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static Map<String, Object> getFields(Object component) {
		try {
			MeasureTime t = new MeasureTime(true);
			Map<String, Object> map = new HashMap<String, Object>();
			PropertyDescriptor[] props = Introspector.getBeanInfo(
					component.getClass()).getPropertyDescriptors();
			for (PropertyDescriptor descriptor : props) {
				if (isColumn(descriptor)) {
					Object field = descriptor.getReadMethod().invoke(component);
					map.put(descriptor.getName(), field);
				}
			}
			LOG.info("getFields(" + component.getClass().getName() + "): " + t.getTime());
			return map;
		} catch (Exception e) {
			return new HashMap<String, Object>();
		}
	}	
	
	private static boolean isColumn(PropertyDescriptor pd) {
		Method rm = pd.getReadMethod();
		return rm != null && (rm.isAnnotationPresent(Column.class) 
				|| rm.isAnnotationPresent(JoinColumn.class));
	}	
	
	public static EntityLog getEntityLog(Object component, TipoOperacaoLogEnum operacaoLogEnum) {
		EntityLog entityLog = createEntityLog(component);
		entityLog.setTipoOperacao(operacaoLogEnum);
		Map<String, Object> fields = getFields(component);
		for (Entry<String, Object> entry : fields.entrySet()) {
			EntityLogDetail det = new EntityLogDetail();
			det.setNomeAtributo(entry.getKey());
			String value = entry.getValue() == null ? "null" : entry.getValue().toString();
			if (operacaoLogEnum.equals(TipoOperacaoLogEnum.D)) {
				det.setValorAnterior(value);
			} else {
				det.setValorAtual(value);
			}
			det.setEntityLog(entityLog);
			entityLog.getLogDetalheList().add(det);
		}
		return entityLog;
	}
	
	public static EntityLog createEntityLog(Object component) {
		EntityLog entityLog = new EntityLog();
		entityLog.setUsuario(getUsuarioLogado());
		entityLog.setDataLog(new Date());
		try {
			entityLog.setIp(getIpRequest());
			entityLog.setUrlRequisicao(getUrlRequest());
		} catch (LogException e) {
			//Se a requisição for executada por temporizador, não há requisição então não se consegue obter o ip
			entityLog.setIp("localhost");
		}
		Class<? extends Object> clazz = EntityUtil.getEntityClass(component);
		entityLog.setNomeEntidade(clazz.getSimpleName());
		entityLog.setNomePackage(clazz.getPackage().getName());
		entityLog.setIdEntidade(EntityUtil.getEntityIdObject(component).toString());
		return entityLog;
	}
	
	public static void removeEntity(Object entity) {
		if (!EntityUtil.isEntity(entity)) {
			throw new IllegalArgumentException("O objeto não é uma entidade");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(entity.getClass().getName());
		sb.append(" o where o.").append(EntityUtil.getId(entity).getName());
		sb.append(" = :id");
		EntityManager em = EntityUtil.getEntityManager();
		Query query = em.createQuery(sb.toString());
		query.setParameter("id", EntityUtil.getEntityIdObject(entity));
		if (query.executeUpdate() > 0) {
			EntityLog entityLog = getEntityLog(entity, TipoOperacaoLogEnum.D);
			em.persist(entityLog);
		}
	}
	
	
 }