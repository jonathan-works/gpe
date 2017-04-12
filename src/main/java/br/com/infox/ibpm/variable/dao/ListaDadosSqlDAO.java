package br.com.infox.ibpm.variable.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;
import javax.sql.DataSource;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.documento.type.Expression;
import br.com.infox.epp.documento.type.ExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.system.Configuration;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(ListaDadosSqlDAO.NAME)
public class ListaDadosSqlDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "listaDadosSqlDAO";
	
	private final LogProvider logger = Logging.getLogProvider(ListaDadosSqlDAO.class);
	private static final Pattern reEL = Pattern.compile("#\\{[^}]*\\}");
    
    private DataSource dataSource;
    
    public DataSource getDataSource() {
    	if (dataSource == null){
    		dataSource = Configuration.getInstance().getApplicationServer().getListaDadosDataSource();
    	}
    	return dataSource;
    }
    
	public List<SelectItem> getListSelectItem(String nativeQuery) {
		return getListSelectItem(nativeQuery, (TaskInstance)null);
    }
	
	public List<SelectItem> getListSelectItem(String nativeQuery, TaskInstance taskInstance) {
		Map<String, Object> parametersMap = getParametersMap(nativeQuery, taskInstance);
		try {
			return getListSelectItem(nativeQuery, parametersMap);
		}
		catch (SQLException e) {
			logger.error("DominioVariavelTarefaDAO:getListSelectItem - Erro ao executar a query " + nativeQuery, e);
			return Collections.<SelectItem>emptyList();
		}    
	}
	
	public List<SelectItem> getListSelectItem(String nativeQuery, Map<String, Object> parametersMap) throws SQLException {
		List<SelectItem> lista = new ArrayList<>();
		
    	try (Connection connection = getDataSource().getConnection()) {
    		try(PreparedStatement statement = getPreparedStatement(connection, nativeQuery, parametersMap)) {
    			try(ResultSet resultSet = statement.executeQuery()) {
    				int numColumns = resultSet.getMetaData().getColumnCount();
    				while (resultSet.next()) {
    					if (numColumns == 1){
    						lista.add(new SelectItem(resultSet.getString(1)));
    					} else {
    						lista.add(new SelectItem(resultSet.getString(2), resultSet.getString(1)));
    					}
    				}
    			}
    		}
		}
    	return lista;		
	}
	
	public Collection<String> getParameters(String nativeQuery) {
		Matcher matcher = reEL.matcher(nativeQuery);

		Collection<String> retorno = new LinkedHashSet<>();
		
		while(matcher.find()) {
			String el = matcher.group();
			retorno.add(el);
		}
		
		return retorno;		
	}
	
	private Map<String, Object> getParametersMap(String nativeQuery, TaskInstance taskInstance) {
		ExpressionResolver expressionResolver;
		if (taskInstance != null) {
			Integer idProcesso = (Integer) taskInstance.getContextInstance().getVariable("processo");
			expressionResolver = ExpressionResolverChainBuilder.defaultExpressionResolverChain(idProcesso, taskInstance);
		} else {
			expressionResolver = new SeamExpressionResolver();
		}

		Collection<String> parameters = getParameters(nativeQuery);
		
		Map<String, Object> retorno = new HashMap<>();
		
		for(String parameter : parameters) {
			Expression resolvedExpression = expressionResolver.resolve(new Expression(parameter));
			Object value = resolvedExpression.getOriginalValue();
			retorno.put(parameter, value);
		}
		
		return retorno;
	}
	
	private PreparedStatement getPreparedStatement(Connection connection, String nativeQuery, Map<String, Object> parameters) throws SQLException {
		Matcher matcher = reEL.matcher(nativeQuery);
		String queryStatement = matcher.replaceAll("\\?");
		
		
		PreparedStatement statement = connection.prepareStatement(queryStatement);
		matcher = reEL.matcher(nativeQuery);

		int idx = 1;

		while(matcher.find()) {
			String parameter = matcher.group();
			Object value = parameters.get(parameter);
			
			if(value instanceof Date) {
				value = new java.sql.Timestamp(((Date)value).getTime());				
			}
			
			statement.setObject(idx++, value);
		}

		return statement;
	}
}
