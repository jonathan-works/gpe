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
package br.com.infox.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("logUtil")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class LogUtil {

	private Level level;
	
	@Create
	public void create() {
		ConsoleAppender appender = (ConsoleAppender) Logger.getRootLogger().getAppender("CONSOLE");
		level = (Level) appender.getThreshold();
	}

	public void setLevel(String level) {
		this.level = Level.toLevel(level);
		ConsoleAppender appender = (ConsoleAppender) Logger.getRootLogger().getAppender("CONSOLE");
		appender.setThreshold(this.level);
	}

	public String getLevel() {
		return level.toString();
	}

	
	
}