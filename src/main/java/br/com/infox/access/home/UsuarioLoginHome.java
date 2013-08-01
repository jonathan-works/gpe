package br.com.infox.access.home;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.security.management.action.UserAction;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(UsuarioLoginHome.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class UsuarioLoginHome extends AbstractHome<UsuarioLogin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginHome";

    private String papel;

    public void setUsuarioId(Integer id) {
        super.setId(id);
        Conversation.instance().end();
        UsuarioLogin u = getInstance();
        UserAction action = getUserAction();
        action.editUser(u.getLogin());
        action.setEnabled(u.getAtivo());
        action.setFirstname(u.getNome());
        papel = null;
    }

    public Integer getUsuarioId() {
        return (Integer) getId();
    }

    @Override
    public void newInstance() {
        super.newInstance();
        Contexts.removeFromAllContexts("org.jboss.seam.security.management.userAction");
    }

    @Override
    protected UsuarioLogin createInstance() {
        UsuarioLogin usuarioLogin = new UsuarioLogin();
        UserAction action = getUserAction();
        action.createUser();
        action.setEnabled(true);
        return usuarioLogin;
    }

    private UserAction getUserAction() {
        return ComponentUtil.getComponent("org.jboss.seam.security.management.userAction");
    }

    @Override
    public String remove(UsuarioLogin u) {
        setInstance(u);
        String ret = super.remove();
        Conversation.instance().end();
        newInstance();
        return ret;
    }

    public String getPapel() {
        if (papel != null) {
            return papel;
        }
        List<String> roles = getUserAction().getRoles();
        if (!roles.isEmpty()) {
            papel = roles.get(0);
        }
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
        List<String> list = Arrays.asList(new String[] { papel });
        getUserAction().setRoles(list);
    }

    public UsuarioLoginHome instance() {
        return ComponentUtil.getComponent(NAME);
    }

}
