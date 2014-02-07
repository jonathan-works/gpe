package br.com.infox.epp.system.util;

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
