 package br.com.infox.epp.system.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(ParametroUtil.NAME)
@Scope(ScopeType.APPLICATION)
@Install(dependencies = { CarregarParametrosAplicacao.NAME })
@Startup(depends = CarregarParametrosAplicacao.NAME)
public class ParametroUtil {

	public static final String LOGIN_USUARIO_EXTERNO = "usuarioexterno";
    public static final String NAME = "parametroUtil";
	public static final LogProvider LOG = Logging.getLogProvider(ParametroUtil.class);
	
	public static String getLoginUsuarioExterno() {
        return LOGIN_USUARIO_EXTERNO;
    }
	
	public static String getFromContext(String nomeParametro, boolean validar) {
		String value = (String) Contexts.getApplicationContext().get(nomeParametro);
		if (validar && value == null) {
			String erroMsg = "Parâmetro não encontrado: " + nomeParametro;
			LOG.error(erroMsg);
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, erroMsg);
		}
		return value;
	}

	public static ParametroUtil instance() {
		return ComponentUtil.getComponent(NAME);
	}

	public static String getParametro(String nome) {
		try{
			return getParametroManager().getParametro(nome).getValorVariavel();
		} catch (NoResultException noResultException){
			throw new IllegalArgumentException(noResultException);
		}
	}
	
	public static String getParametroOrFalse(String nome) {
		try{
			return getParametroManager().getParametro(nome).getValorVariavel();
		} catch (Exception exception){
			LOG.info(".getParametroOrFalse(nome)", exception);
			return "false";
		}
	}
	
	public static boolean isLoginComAssinatura() {
	    return isParameterActiveAndTrue("loginComAssinatura");
	}
	
	public static boolean isProducao() {
	    return isParameterActiveAndTrue("producao");
	}

    private static boolean isParameterActiveAndTrue(final String nome) {
        try{
            final Parametro parametro = getParametroManager().getParametro(nome);
            return parametro.getAtivo() && Boolean.parseBoolean(parametro.getValorVariavel());
        } catch (Exception exception){
            return false;
        }
    }

	public String executarFactorys() {
		for (Method metodo : this.getClass().getDeclaredMethods()) {
			try {
				metodo.invoke(this);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			    LOG.error(".executarFactorys()", e);
			}
		}
		return "OK";
	}
	
	private static ParametroManager getParametroManager(){
		return ComponentUtil.getComponent(ParametroManager.NAME);
	}
}