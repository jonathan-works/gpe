/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.BooleanType;

public abstract class EnumType<T extends Enum<T>> extends BooleanType{
	
	private Enum<T> typeEnum;
	
	protected EnumType(Enum<T> type) {
		this.typeEnum = type;
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public Object get(ResultSet rs, String name) throws SQLException {
		String value = rs.getString(name);
		return value != null ? typeEnum.valueOf(typeEnum.getClass(), value) : null;
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public Object stringToObject(String xml) throws Exception {
		return typeEnum.valueOf(typeEnum.getClass(),xml);
	}
		
	@Override
	public void set(PreparedStatement st, Object value, int index)
			throws SQLException {
		if (value != null) {
			st.setString(index, value.toString());
		}
	}
	
	@Override
	public String objectToSQLString(Object value, Dialect dialect)
			throws Exception {
		return (String) value;
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public Class getReturnedClass() {
		return typeEnum.getClass();
	}
	
	public String getName() {
		return "enum";
	}
	
}