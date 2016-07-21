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
import br.com.infox.seam.security.SecurityUtil;

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
                menuService.discoverAndCreateRoles();
                pagesChecked = true;
            }
            this.dropMenus = menuService.getMenuItemList();
        }
        return dropMenus;
    }

    public String getActionMenuJson() {
        return new Gson().toJson(getActionMenu());
    }

    @ExceptionHandled
    public boolean isShowMenu() {
        return securityUtil.isLoggedIn() && Authenticator.getUsuarioLogado().getAtivo()
                && !Authenticator.getUsuarioLogado().getBloqueio() && !Authenticator.getUsuarioLogado().getProvisorio();
    }

}
