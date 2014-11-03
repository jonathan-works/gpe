package br.com.infox.ibpm.variable.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(DominioVariavelTarefaDAO.NAME)
public class DominioVariavelTarefaDAO extends DAO<DominioVariavelTarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "dominioVariavelTarefaDAO";
    
    private DataSource dataSource;
    
    private void init() {
		try {
			InitialContext ic = new InitialContext();
			dataSource = (DataSource) ic.lookup("java:jboss/datasources/ListaDadosDataSource");
		} catch (NamingException e) {
			e.printStackTrace();
		}
    }
    
	public List<SelectItem> getListSelectItem(String nativeQuery) {
		init();
    	List<SelectItem> lista = new ArrayList<>();
    	try {
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSet = statement.executeQuery(nativeQuery);
			while (resultSet.next()) {
				lista.add(new SelectItem(resultSet.getString(2), resultSet.getString(1)));
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return lista;
    }
}
