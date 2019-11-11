package br.com.infox.ibpm.type.matcher;

import org.jbpm.context.exe.JbpmTypeMatcher;

import br.com.infox.core.util.EntityUtil;

public class JpaTypeMatcher implements JbpmTypeMatcher {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean matches(Object value) {
        return EntityUtil.isEntity(value);
    }

}
