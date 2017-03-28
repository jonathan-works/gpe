package br.com.infox.epp.menu;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.security.SecurityUtil;
import br.com.infox.seam.util.ComponentUtil;

@Named
@SessionScoped
public class MenuNavigation implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<MenuItemDTO> dropMenus;
    private boolean pagesChecked;

    @Inject private SecurityUtil securityUtil;
    @Inject private MenuService menuService;

    @PostConstruct
    public void init(){
        pagesChecked=false;
        refresh();
    }
    
    @ExceptionHandled
    public void refresh() {
        dropMenus = null;
    }
    
    public List<MenuItemDTO> getActionMenu() {
        if (dropMenus == null){
            if (!pagesChecked && securityUtil.isLoggedIn()) {
                pagesChecked = true;
            }
            this.dropMenus = menuService.getMenuItemList();
        }
        return dropMenus;
    }

    public String getActionMenuJson() {
        //FIXME: SOLUÇÃO PALEATIVA. RESOLVER EM REFATORAÇÃO DO MENU
        return new Gson().toJson(getActionMenu()).replaceAll("\"/([^\"]+)\\.xhtml\"", String.format("%s/$1.seam", ComponentUtil.<PathResolver>getComponent(PathResolver.NAME).getContextPath()));
    }

    @ExceptionHandled
    public boolean isShowMenu() {
        return securityUtil.isLoggedIn() && Authenticator.getUsuarioLogado().getAtivo()
                && !Authenticator.getUsuarioLogado().getBloqueio() && !Authenticator.getUsuarioLogado().getProvisorio();
    }

}
