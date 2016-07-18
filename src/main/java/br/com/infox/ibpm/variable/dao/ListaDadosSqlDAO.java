package br.com.infox.ibpm.variable.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.sql.DataSource;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.documento.type.Expression;
import br.com.infox.epp.documento.type.ExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(ListaDadosSqlDAO.NAME)
public class ListaDadosSqlDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "listaDadosSqlDAO";
	public static final String LISTA_DADOS_DS = "ListaDadosDataSource";
	
	private final LogProvider logger = Logging.getLogProvider(ListaDadosSqlDAO.class);
    
    private DataSource dataSource;
    
    public DataSource getDataSource() {
    	if (dataSource == null){
    		dataSource = ApplicationServerService.instance().getDataSource(LISTA_DADOS_DS);
    	}
    	return dataSource;
    }
    
	public List<SelectItem> getListSelectItem(String nativeQuery) {
    	return getListSelectItem(nativeQuery, null);
    }
	
	public List<SelectItem> getListSelectItem(String nativeQuery, TaskInstance taskInstance) {
		List<SelectItem> lista = new ArrayList<>();
    	try (Connection connection = getDataSource().getConnection()) {
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			nativeQuery = addParameters(nativeQuery, taskInstance);
			ResultSet resultSet = statement.executeQuery(nativeQuery);
			int numColumns = resultSet.getMetaData().getColumnCount();
			while (resultSet.next()) {
				if (numColumns == 1){
					lista.add(new SelectItem(resultSet.getString(1)));
				} else {
					lista.add(new SelectItem(resultSet.getString(2), resultSet.getString(1)));
				}
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			logger.error("DominioVariavelTarefaDAO:getListSelectItem - Erro ao executar a query " + nativeQuery, e);
        }
    	return lista;
	}
	private String addParameters(String nativeQuery, TaskInstance taskInstance){
		ExpressionResolver expressionResolver;
		if (taskInstance != null) {
			Integer idProcesso = (Integer) taskInstance.getContextInstance().getVariable("processo");
			expressionResolver = ExpressionResolverChainBuilder.defaultExpressionResolverChain(idProcesso, taskInstance);
		} else {
			expressionResolver = new SeamExpressionResolver();
		}
		StringBuilder sb = new StringBuilder(nativeQuery);
		int start = 0, end = 0;
		while ((start = sb.indexOf("#", start)) != -1){
			end = sb.indexOf("}", start);
			String expression = sb.substring(start, end + 1);
			Expression resolvedExpression = expressionResolver.resolve(new Expression(expression));
			Object value = resolvedExpression.getOriginalValue();
			if (value == null) {
				value = "";
			}
			if (value instanceof String) {
				sb.replace(start, end + 1, "'".concat(value.toString()).concat("'"));
			} else {
				sb.replace(start, end + 1, value.toString());
			}
		}
		return sb.toString();
	}

}
