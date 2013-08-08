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
package br.com.itx.component;

import java.util.LinkedHashMap;

import org.jboss.seam.core.Expressions;

@SuppressWarnings("unchecked")
public class PropertyMap<K,V> extends LinkedHashMap<K,V> {
	
	private static final long serialVersionUID = 1L;

	public PropertyMap() {
	}
	
	@Override
	public V get(Object key) {
		V o = super.get(key);
		if (o instanceof String) {
			String s = (String) o;
			if (s.startsWith("#{")) {
				Object value = Expressions.instance().createValueExpression(s).getValue();
				o = (V) value;
			}
		}
		return o;
	}
}