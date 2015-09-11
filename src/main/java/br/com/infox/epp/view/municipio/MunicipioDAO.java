package br.com.infox.epp.view.municipio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.estado.EstadoDAO;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(MunicipioDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class MunicipioDAO extends GenericDAO {
	
    public static final String NAME = "municipioTceDAO";
/*    private static final LogProvider LOG = Logging.getLogProvider(MunicipioDAO.class);

    @In
    private EstadoDAO estadoDAO;
    
    public String getNomeMunicipioSemException(String codigo) {
        try {
			return executeQuery(MunicipioQuery.NOME_MUNICIPIO_QUERY, codigo);
		} catch (DAOException e) {
			LOG.error("getNomeMunicipioSemException", e);
			return null;
		}
    }
    
    public String getNomeMunicipio(String codigo) throws DAOException {
        return executeQuery(MunicipioQuery.NOME_MUNICIPIO_QUERY, codigo);
    }
    
    public List<MunicipioBean> getMunicipiosByEstado(String uf) {
    	try {
	    	List<MunicipioBean> resultado = new ArrayList<>();
	        Connection con = dataSource.getConnection();
	        PreparedStatement ps = con.prepareStatement(MunicipioQuery.MUNCIPIO_BY_ESTADO, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
	        ps.setString(1, uf);
	        ResultSet resultSet = ps.executeQuery();
	        while (resultSet.next()) {
	            String ccod = resultSet.getString(1);
	            String nome = resultSet.getString(2);
	            resultado.add(new MunicipioBean(ccod, nome.trim()));
	        }
	        resultSet.close();
	        ps.close();
	        con.close();
	        return resultado;
	    } catch (SQLException e) {
	        return null;
	    }
	}
    
    public String getCodigoIBGE(String codigo) throws DAOException {
        return executeQuery(MunicipioQuery.CODIGO_IBGE_QUERY, codigo);
    }
    
    public String getUfEstado(String codigo) {
        try {
			return executeQuery(MunicipioQuery.ESTADO_QUERY, codigo);
		} catch (DAOException e) {
			LOG.error("getUfEstado", e);
			return null;
		}
    }
    
    public Estado getEstado(String codigo) throws DAOException {
        return estadoDAO.getBySigla(executeQuery(MunicipioQuery.ESTADO_QUERY, codigo));
    }
    
    public List<MunicipioBean> getMunicipios() {
        try {
            List<MunicipioBean> resultado = new ArrayList<>();
            Connection con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(MunicipioQuery.MUNICIPIOS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String ccod = resultSet.getString(1);
                String nome = resultSet.getString(2);
                resultado.add(new MunicipioBean(ccod, nome));
            }
            resultSet.close();
            ps.close();
            con.close();
            return resultado;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Map<Long, String> getMunicipiosByCodigos(List<Long> codigosUgs) {
        try {
            Map<Long, String> resultado = new HashMap<>();
            Connection con = dataSource.getConnection();
            String query = String.format(MunicipioQuery.MUNICIPIOS_BY_CODIGOS_UG, StringUtil.concatList(codigosUgs, ", "));
            PreparedStatement ps = con.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long codigoUg = resultSet.getLong(1);
                String nome = resultSet.getString(2);
                resultado.put(codigoUg, nome);
            }
            resultSet.close();
            ps.close();
            con.close();
            return resultado;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
}
