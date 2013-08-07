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
package br.com.infox.ibpm.jbpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.jboss.seam.annotations.Create;


public abstract class ActionTemplate implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static Set<Class<? extends ActionTemplate>> templates = new HashSet<Class<? extends ActionTemplate>>();

	private Object[] parameters;
	
    public abstract String getFileName();
	
	public abstract String getExpression();

	public abstract String getLabel();
	
	public abstract void extractParameters(String expression);

	
	protected String[] getExpressionParameters(String expression) {
		if (expression == null || expression.equals("")) {
			return new String[0];
		}
		int i = expression.indexOf('(');
		String texto = expression.substring(i + 1);
		StringTokenizer st = new StringTokenizer(texto, ",')}");
		List<String> list = new ArrayList<String>();
		while(st.hasMoreTokens()) {
			list.add(st.nextToken().trim());
		}
		String[] ret = new String[list.size()];
		return list.toArray(ret);
	}


	public Object[] getParameters() {
		return parameters;
	}
	
	protected void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

	public boolean isPublic() {
		return true;
	}
	
	@Create
	public void init() {
		templates.add(this.getClass());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getFileName())
			.append("=")
			.append(getExpression())
			.append(",")
			.append(getLabel());
		return sb.toString();
	}
	

}