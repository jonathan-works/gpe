package br.com.infox.epp.system.util;

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
import javax.persistence.JoinColumn;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import org.jboss.seam.util.Reflections;

import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.system.annotation.Ignore;
import br.com.infox.epp.system.entity.EntityLog;
import br.com.infox.epp.system.entity.EntityLogDetail;
import br.com.infox.epp.system.exception.LogException;
import br.com.infox.epp.system.type.TipoOperacaoLogEnum;

public final class LogUtil {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";
    private static final LogProvider LOG = Logging.getLogProvider(LogUtil.class);

    private LogUtil() {
        super();
    }

    /**
     * Checa se a classe é um array de bytes.
     * 
     * @param type
     * @return
     */
    public static boolean isBinario(Class<?> type) {
        return type.isArray()
                && type.getComponentType().getName().equals("byte");
    }

    /**
     * Checa se um atributo de um objeto é um array de bytes.
     * 
     * @param entidade
     * @param nomeAtributo
     * @return
     * @throws Exception
     */
    public static boolean isBinario(Object entidade, String nomeAtributo) {
        Class<?> classAtributo = getType(entidade, nomeAtributo);
        return isBinario(classAtributo);
    }

    private static Class<?> getType(Object entidade, String nomeAtributo) {
        return Reflections.getField(entidade.getClass(), nomeAtributo).getType();
    }

    /**
     * Checa se o atributo de um objeto é uma coleção.
     * 
     * @param entidade
     * @param nomeAtributo
     * @return
     * @throws Exception
     */
    public static boolean isCollection(Object entidade, String nomeAtributo) {
        Class<?> classAtributo = getType(entidade, nomeAtributo);
        return isCollectionClass(classAtributo);
    }

    private static boolean isCollectionClass(Class<?> classAtributo) {
        return ArrayList.class.equals(classAtributo)
                || List.class.equals(classAtributo)
                || Set.class.equals(classAtributo);
    }

    /**
     * Testa se a entidade possui a anotação @Ignore, caso possua não será
     * logada
     * 
     * @param entidade
     * @return
     */
    public static boolean isLogable(Object entity) {
        return !entity.getClass().getName().startsWith("org.jbpm") && !EntityUtil.isAnnotationPresent(entity, Ignore.class);
    }

    public static boolean compareObj(Object object1, Object object2) {
        if (object1 == null) {
            return object2 == null;
        }
        return object1.equals(object2);
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
            return EntityUtil.getEntityIdObject(object) + ": "
                    + object.toString();
        } else {
            return object.toString();
        }
    }

    public static Map<String, Object> getFields(Object component) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            PropertyDescriptor[] props = Introspector.getBeanInfo(component.getClass()).getPropertyDescriptors();
            for (PropertyDescriptor descriptor : props) {
                if (isColumn(descriptor)) {
                    Object field = descriptor.getReadMethod().invoke(component);
                    map.put(descriptor.getName(), field);
                }
            }
            LOG.info("getFields(" + component.getClass().getName() + ")");
            return map;
        } catch (Exception e) {
            LOG.error(".getFields(component)", e);
            return new HashMap<String, Object>();
        }
    }

    private static boolean isColumn(PropertyDescriptor pd) {
        Method rm = pd.getReadMethod();
        return rm != null
                && (rm.isAnnotationPresent(Column.class) || rm.isAnnotationPresent(JoinColumn.class));
    }

    public static EntityLog getEntityLog(Object component,
            TipoOperacaoLogEnum operacaoLogEnum) {
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
        entityLog.setUsuario(Authenticator.getUsuarioLogado());
        entityLog.setDataLog(new Date());
        // TODO melhorar esse try-catch
        try {
            entityLog.setIp(getIpRequest());
            entityLog.setUrlRequisicao(getUrlRequest());
        } catch (LogException e) {
            LOG.debug(".createEntityLog(component) : Requisição for executada por temporizador", e);
            // Se a requisição for executada por temporizador, não há requisição
            // então não se consegue obter o ip
            entityLog.setIp("localhost");
        }
        Class<? extends Object> clazz = EntityUtil.getEntityClass(component);
        entityLog.setNomeEntidade(clazz.getSimpleName());
        entityLog.setNomePackage(clazz.getPackage().getName());
        entityLog.setIdEntidade(EntityUtil.getEntityIdObject(component).toString());
        return entityLog;
    }

}
