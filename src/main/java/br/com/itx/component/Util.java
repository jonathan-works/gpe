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
package br.com.itx.component;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.ajax4jsf.context.AjaxContext;
import org.hibernate.AnnotationException;
import org.hibernate.LazyInitializationException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.util.RandomStringUtils;
import org.jboss.seam.util.Strings;
import org.jboss.seam.web.Parameters;

import br.com.itx.component.grid.GridQuery;
import br.com.itx.component.grid.SearchField;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.AnnotationUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.FacesUtil;

@Scope(ScopeType.APPLICATION)
public class Util implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(Util.class);

	/**
	 * Retorna o caminho do projeto.
	 * @return
	 */
	public String getContextPath() {
		FacesContext fc = FacesContext.getCurrentInstance();
		return fc.getExternalContext().getRequestContextPath();
	}

	/**
	 * Retorna o nome definido na anotação @Name do componente
	 * @param obj componente
	 * return Nome do componente
	 */
	public String getComponentName(Object obj) {
		if (obj.getClass().isAnnotationPresent(Name.class)) {
			return ComponentUtil.getComponentName(obj.getClass());
		}
		return null;
	}

	/**
	 * Retorna o caminho completo, ou seja, desde o servidor.
	 * @return
	 */
	public String getContextRealPath() {
		return ServletLifecycle.getServletContext().getRealPath("");
	}
	
	public String getUrlProject() {
		HttpServletRequest rc = getRequest();
		String url = rc.getRequestURL().toString();
		String protEnd = "://";
		int pos = url.indexOf(protEnd) + protEnd.length() + 1;
		return url.substring(0, url.indexOf('/', pos)) + rc.getContextPath();
	}
	
	public String getUrlRequest() {
		return getRequest().getRequestURL().toString();
	}	
	
	public String getUrlRequestParams() {
		HttpServletRequest request = getRequest();
		StringBuilder url = new StringBuilder(request.getRequestURL().toString());
		Map<?, ?> parameterMap = request.getParameterMap();
		boolean first = true;
		for (Entry<?, ?> entry : parameterMap.entrySet()) {
			if (first) {
				url.append("?");
				first = false;
			} else {
				url.append("&amp;");
			}
			String[] value = (String[]) entry.getValue();
			url.append(entry.getKey().toString()).append('=').append(value[0]);
		}
		return url.toString();
	}		
	
	public String getRequestParams() {
		HttpServletRequest request = getRequest();
		StringBuilder url = new StringBuilder();
		Map<?, ?> parameterMap = request.getParameterMap();
		boolean first = true;
		for (Entry<?, ?> entry : parameterMap.entrySet()) {
			if (!first) {
				url.append(", ");
				first = false;
			} 
			String[] value = (String[]) entry.getValue();
			url.append(entry.getKey().toString()).append('=').append(value[0]);
		}
		return url.toString();
	}		
	
	public String getIdPagina() {
		HttpServletRequest request = getRequest();
		String requestURL = request.getRequestURL().toString();
		return requestURL.split(request.getContextPath())[1];
	}

	public HttpServletRequest getRequest() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null && facesContext.getExternalContext() != null) {
			Object requestObj = facesContext.getExternalContext().getRequest();
			if (requestObj instanceof HttpServletRequest) {
				return (HttpServletRequest) requestObj;
			}
		}
		return null;
	}		
	
	/**
	 * Retorna uma string gerada de maneira Randomica com caracteres alfanumericos
	 * de tamanho 20.
	 * @return
	 */
	public String getRandom() {
		return RandomStringUtils.randomAlphanumeric(20).toUpperCase();
	}
	
	/**
	 * Retorna uma string gerada pelo metodo {@link #getRandom() getRandom()},  
	 * concatenado com o prefixo rnd_
	 * @return
	 */
	public String getRandomId() {
		return "rnd_" + getRandom();
	}
	
	/**
	 * Retorna o parametro de requisição action.
	 * @return
	 */
	public String action() {
		Parameters parameters = Parameters.instance();
		String[] action = parameters.getRequestParameters().get("action");
		return (action != null ? action[0] : "");
	}
	
	public String homeEvent(String event, EntityHome<?> home, String type) 
	throws Exception {
		Context eventContext = Contexts.getEventContext();
		eventContext.set("homeActionType", type);
		eventContext.set("home", home);
		eventContext.set("instance", home.getInstance());
		if (event == null || event.equals("")) {
			String exp = "#{home." + type + "}";
			Expressions e = Expressions.instance();
			return (String)e.createMethodExpression(exp).invoke();
		}
		return event(event);
	}
	
	public String event(String event) {
		Context eventContext = Contexts.getEventContext();
		if (event != null && !event.equals("")) {
			eventContext.set("event", event);
			((Events)Component.getInstance("org.jboss.seam.core.events")).raiseEvent(event);
		}
		String action = (String)eventContext.get("action");
		if (action == null) {
			action = (String)eventContext.get("actionFromHome");
		}
		return (action != null ? action : "");
	}
	
	/**
	 * Deixa a primeira letra da string maiuscula.
	 * @param text - Texto a ser convertido
	 * @return Texto com a primeira letra maiuscula.
	 */
	public String upperFirst(String text) {
		if (text == null || text.equals("")) {
			return "";
		}
		return text.substring(0,1).toUpperCase() + text.substring(1);
	}
	
	/**
	 * Busca o componente GridQuery que irá popular uma combo.
	 * Ao encontrar o grid, os campos de pesquisa são removidos para limpar
	 * possível consulta anteriormente realizada.
	 * 
	 * @param gridId é o id do grid a ser utilizado como origem dos registros
	 * 
	 */
	public Object getComboQuery(String gridId) {
		GridQuery grid = (GridQuery) Component.getInstance(gridId + "Grid", true);
		if (grid != null) {
			grid.setSearchFields(new ArrayList<SearchField>());
		}
		return grid;
	}
	
	public String getSelfViewId() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String viewId = fc.getViewRoot().getViewId();
		return viewId.replace(".xhtml", "");
	}

	/**
	 * @return Retorna o diretório do JSF View Id, ou seja, o diretório da página atual.
	 */
	public String getViewIdDirectory() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String viewId = fc.getViewRoot().getViewId();
		return viewId.substring(0, viewId.lastIndexOf('/')+1); 
	}
	
    /**
     * Gera uma lista de SelectItem partindo de uma String separada por vírgula 
     * @param values são os valores separados por vírgulas, no formato valor:label
     * @return lista de SelectItem
     */
	
	//TODO Tratar virgula e dois pontos no valor ou no texto (\, \:) -> ou JSON
    public List<SelectItem> splitAsList(String values) {
    	List<SelectItem> l = new ArrayList<SelectItem>();
    	for (String s : values.split(",")) {
    		if (s.indexOf(":") == -1) {
    			l.add(new SelectItem(s));
    		} else {
    			String[] value = s.split(":");
    			l.add(new SelectItem(value[0], value[1]));
    		}
    	}
		return l ;
    }
	
    /**
     * Gera uma lista de SelectItem partindo de uma String separada por vírgula
     * neste metodo o valor antes dos dois pontos, deverá ser um inteiro. 
     * @param values são os valores separados por vírgulas, no formato valor:label
     * onde valor é um inteiro.
     * @return lista de SelectItem
     */
	
	//TODO Tratar virgula e dois pontos no valor ou no texto (\, \:) -> ou JSON
    public List<SelectItem> splitAsListWithKeyNumber(String values) {
    	List<SelectItem> l = new ArrayList<SelectItem>();
    	for (String s : values.split(",")) {
    		if (s.indexOf(":") == -1) {
    			l.add(new SelectItem(s));
    		} else {
    			String[] value = s.split(":");
    			l.add(new SelectItem(Integer.parseInt(value[0].trim()), value[1]));
    		}
    	}
		return l ;
    }
    
    /**
     * Retorna uma lista de String a partir de um string separado por vírgula 
     * @param values A string unificada separada por vírgula.
     * @return Lista de strings.
     */
    public List<String> getStringAsList(String values) {
    	List<String> l = new ArrayList<String>();
    	for (String s : values.split(",")) {
    		l.add(s);
    	}
		return l ;
    }
    
    public List<Object> getArrayAsList(Object[] array) {
    	return Arrays.asList(array);
    }
    
    /**
     * Cria um valor de expressão a partir de um método do Seam. 
     * @param expression - Expressão a ser criada.
     * @return Expressão criada.
     */
	@SuppressWarnings("unchecked")
	public <C> C eval(String expression) {
		if (expression == null || expression.trim().length() == 0) {
			return null;
		}
		String expr = expression.trim();
		if (!expr.startsWith("#{")) {
			expr = "#{" + expr + "}";
		}
		return (C) Expressions.instance().createValueExpression(expr).getValue();
	}	
    
    /**
     * Verifica se a classe é um subtipo de AbstractHome.
     * @param object - Home em execução.
     * @return True se for um subtipo de AbstractHome
     */
    public boolean isAbstractChild(Object object) {
    	return (object instanceof AbstractHome<?>);
    }
    
    /**
     * Concatena os valores de um List quebrando linha entre eles. Caso a lista
     * esteja vazia devolve a String do parametro <code>valueOnEnptyList</code>
     * @param list
     * @param valueOnEmptyList Valor a ser retornado caso o <code>list<code> esteja nulo ou vazio
     * @return
     */
    public String listToString(List<?> list, String valueOnEmptyList) {
    	StringBuilder sb = new StringBuilder();
    	for (Object object : list) {
    		if (sb.length() > 0) {
    			sb.append('\n');
    		}
			sb.append(object.toString());
		}
    	return list.size() > 0 ? sb.toString() : valueOnEmptyList;
    }
    
    public String listToString(List<?> list) {
    	return listToString(list, "");
    }
    
    public String formatDateLong(Date date) {
    	if (date != null) {
    		Locale ptBR = new Locale("pt", "BR");
    		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, ptBR);
    		return dateFormat.format(date);
    	} else {
    		return null;
    	}
    }
    
	public String formatDataAtual(String formato) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat(formato);
			return sf.format(new Date());
		} catch (Exception e) {
			return null;
		}
	}	    

	public boolean isAjaxRequest() {
		return AjaxContext.getCurrentInstance(FacesContext.getCurrentInstance()).isAjaxRequest();
	}
	
	public Object getFromPageContext(String var) {
		return Contexts.getPageContext().get(var);
	}
	
	public void setToPageContext(String var, Object object) {
		Contexts.getPageContext().set(var, object);
	}	

	public static Object getFromEventContext(String var) {
		return Contexts.getEventContext().get(var);
	}
	
	public static void setToEventContext(String var, Object object) {
		Contexts.getEventContext().set(var, object);
	}		
	
	public List<?> getEmptyList() {
		return Collections.EMPTY_LIST;
	}
	
	//TODO verificar se este metodo ficará aqui mesmo, pois no SelectsItensQuery não estava funcionando
	/**
	 * Método que trata a expressão a ser mostrada nas opções chamadas pelo 
	 * componente s:selectItems
	 * 
	 * @param expression é a expressão no formato {campo}, onde campo é o nome de um 
	 * 					atributo da entidade a ser mostrada.
	 * @param obj é a instância do objeto em cada uma das opções, corresponde ao atributo 
	 * 					var do componente s:selectItems
	 * @return
	 */
	public Object getSelectExpressionSelectItem(String expression, Object obj) {
		if (!Strings.isEmpty(expression)) {
			Contexts.getMethodContext().set("obj", obj);
			expression = expression.replace("{", "#{obj.");
			obj = obj == null ? "" : 
			Expressions.instance().createValueExpression(expression).getValue();
			Contexts.getMethodContext().remove("obj");
		}
		return obj;
	}
	
	public static DataSource getDataSource(String dataSource) throws Exception {
		InitialContext cxt = new InitialContext();
		if ( cxt == null ) {
			throw new Exception("No context!");
		}		
		return (DataSource) cxt.lookup(dataSource);
	}
	
	public static DataSource getDataSourceBin() throws Exception {
		String ds = "java:/" + new Util().eval("dataSourceNameBin");
		return getDataSource(ds);
	}
	
	public String getContextsAsString(Context context, boolean htmlBreak) {
		StringBuilder sb = new StringBuilder();
		for (String name : context.getNames()) {
			sb.append(name).append(" = ");
			try {
				sb.append(context.get(name));
			} catch (LazyInitializationException e) {
				sb.append("----");
			}
			sb.append(htmlBreak ?  "<br />" : '\n');
		}
		return sb.toString();
	}

	public static boolean beginTransaction() {
		try {
		org.jboss.seam.transaction.UserTransaction ut = Transaction.instance();
		if(ut != null && !ut.isActive()) {
			ut.begin();
			return true;
		}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AplicationException(AplicationException.
					createMessage("iniciar transação", 
								  "beginTransaction()", 
								  "RegistraEventoAction", 
								  "BPM"), e);
		}
		return false;
	}

	public static void commitTransction() {
		try {
			org.jboss.seam.transaction.UserTransaction ut = Transaction.instance();
			if(ut != null && ut.isActive()) {
				ut.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AplicationException(AplicationException.
					createMessage("iniciar transação", 
								  "beginTransaction()", 
								  "RegistraEventoAction", 
								  "BPM"), e);
		}
	}
	
	/**
	 * Cria um method expression para a string informada no parametro.
	 * @param methodName Método a ser chamado
	 * @return MethodExpression
	 */
	public static void invokeMethod(String action) {
		if(action != null && !"".equals(action)) {
			MeasureTime mt = new MeasureTime(true);
			StringBuilder sb = new StringBuilder();
			sb.append("#{").append(action).append("}");
	        Expressions.instance().createMethodExpression(sb.toString()).invoke();
	        LOG.info(MessageFormat.format("invokeMethod: {0} [{1} ms]", 
	        		sb, mt.getTime()));
		}
    }

	/**
	 * Verifica se todos os elementos do Array são null
	 * @param o Array que deseja-se verificar se é vazio
	 * @return True se for vazio
	 */
	public static boolean isEmpty(Object[] o) {
		for(int i=0;i<o.length;i++) {
			if(o[i] != null) {
				return false;
			}
		}
		return true;
	}
	
	public String getSessionContextsAsString(boolean htmlBreak) {
		return getContextsAsString(Contexts.getSessionContext(), htmlBreak);
	}	
	
	public String getApplicationContextsAsString(boolean htmlBreak) {
		return getContextsAsString(Contexts.getApplicationContext(), htmlBreak);
	}	
	
	public String getBusinessProcessContextsAsString(boolean htmlBreak) {
		return getContextsAsString(Contexts.getBusinessProcessContext(), htmlBreak);
	}	
	
	public String getConversationContextsAsString(boolean htmlBreak) {
		return getContextsAsString(Contexts.getConversationContext(), htmlBreak);
	}	
	
	public String getEventContextsAsString(boolean htmlBreak) {
		return getContextsAsString(Contexts.getEventContext(), htmlBreak);
	}	
	
	public String getMethodContextsAsString(boolean htmlBreak) {
		return getContextsAsString(Contexts.getMethodContext(), htmlBreak);
	}	
	
	public String getPageContextsAsString(boolean htmlBreak) {
		return getContextsAsString(Contexts.getPageContext(), htmlBreak);
	}	
		
	/**
	 * Elimina comentarios do HTML.
	 * @param text - Texto a ser convertido
	 * @return Texto com os comentarios eliminados.
	 */
	public String removeCommentsHTML(String text) {
        StringBuilder sb = new StringBuilder(text);
        int posIni = sb.indexOf("<!--");
        int posFim = sb.indexOf("-->", posIni);
        while (posIni != -1 && posFim != -1) {
            sb.delete(posIni, posFim + 3);
            posIni = sb.indexOf("<!--");
            posFim = sb.indexOf("-->", posIni);
        }
        return sb.toString();
    }
	
	/**
	 * Adiciona a mensagem passada como parâmetro no FacesMessages e no log. 
	 * @param severity
	 * @param msg Mensagem a ser exibida.
	 */
	public static void setMessage(Severity severity, String msg) {
//		FacesMessages.instance().clearGlobalMessages();
		FacesMessages.instance().add(severity, msg);
		LOG.warn(msg);
	}
	
	/**
	 * Retorna o valor do Id da entidade.
	 * @param object Objeto em que será pesquisada o método que possui a anotação
	 * @return Valor do Id
	 * @throws AnnotationException
	 */
	public Object getIdValue(Object object) throws AnnotationException {
		return AnnotationUtil.getIdValue(object);
	}
	
	/**
	 * Recebe o número de bytes e retorna o número em Kb (kilobytes).
	 * @param bytes número em bytes
	 * @return número em kilobytes
	 */
	public String getFormattedKb(Integer bytes) {
		if (bytes != null && bytes > 0) {
			NumberFormat formatter =  DecimalFormat.getNumberInstance(new Locale("pt","BR"));
			formatter.setMinimumIntegerDigits(1);
			formatter.setMaximumFractionDigits(2);
			formatter.setMinimumFractionDigits(2);
			float kbytes = bytes / 1024f;
			return formatter.format(kbytes) + " Kb";
		} else {
			return null;
		}
	}
	
	public boolean fileExists(String arquivo) {
		File f = new File(FacesUtil.getServletContext(null).getRealPath(arquivo));
		return f.exists();
	}
}