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