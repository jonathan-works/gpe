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
package br.com.infox.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("logResolver")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class LogResolver {

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