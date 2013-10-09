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

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

@Name("reportUtil")
@BypassInterceptors
public class ReportUtil {

	 /**
     * Retorna a o valor da propriedade da linha informada.
     * @param obj - Linha
     * @param arg - Nome da propriedade no Entity, que pode ser composta (pessoa.nome)
     * @return Valor da propriedade
     */
	
    public Object getValueRow(Object obj, String arg) {
    	Object old = Contexts.getEventContext().get("row");
    	Contexts.getEventContext().set("row", obj);
    	Object s = new Util().eval("row." + arg  );
    	Contexts.getEventContext().set("row", old);
    	return s;
    }
    
    /**
     * Retorna a lista de Strings para o cabeçalho e/ou nome das propriedades
     * para montar a tabela no Seam - PDF
     * @param args - Valores a serem exibidos
     * @return Lista com os valores informados
     */
	public List<String> getPdfList(String... args) {
		List<String> list = new ArrayList<String>();
		for (String value : args) {
			list.add(value);
		}
		return list;
	}

	
}