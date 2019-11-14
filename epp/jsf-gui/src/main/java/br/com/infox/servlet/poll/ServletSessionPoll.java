package br.com.infox.servlet.poll;

import java.io.IOException;
import java.util.UUID;

import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.ConversationEntry;

import com.sun.faces.context.ExternalContextImpl;
import com.sun.faces.context.FacesContextImpl;
import com.sun.faces.lifecycle.LifecycleImpl;
import com.sun.faces.util.Util;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.cdi.viewscoped.ViewScopeManager;

@WebServlet(urlPatterns = "/sessionPoll")
public class ServletSessionPoll extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if ( session != null ) {
            if ( !StringUtil.isEmpty(getConversationId(req)) ) {
                revalidateSeamConversation(session, req);
            }
            //TODO:Reavalidação do View State retirada visto que quando o usuário está algumas páginas do sistema, 
            //por exemplo Fluxo/listView.xhtml, não está sendo possível revalidar o viewState
//            if ( !StringUtil.isEmpty(getViewStateId(req)) ) {
//                revalidateViewState(session, req, resp);
//            }
        }
    }
    
    private void revalidateSeamConversation(HttpSession session, HttpServletRequest req) {
        ConversationEntries conversationEntries = (ConversationEntries) session.getAttribute("org.jboss.seam.core.conversationEntries");
        if (conversationEntries != null) {
            ConversationEntry conversationEntry = conversationEntries.getConversationEntry(getConversationId(req));
            if (conversationEntry != null) {
                conversationEntry.setLastRequestTime(System.currentTimeMillis());
            }
        }
    }

    private void revalidateViewState(HttpSession session, HttpServletRequest req, HttpServletResponse resp) {
        FacesContext facesContext = null;
        try {
            facesContext = createFacesContext(req, resp);
            UIViewRoot viewRoot = restoreViewRoot(req);
            UUID beanStoreId = (UUID) viewRoot.getViewMap(false).get(ViewScopeManager.class.getName());
            ViewScopeManager viewScopeManager = Beans.getReference(ViewScopeManager.class);
            viewScopeManager.getActiveViewScope(beanStoreId);
        } finally {
            if ( facesContext != null ) {
                facesContext.release();
            }
        }
    }

    private FacesContext createFacesContext(HttpServletRequest req, HttpServletResponse resp) {
        ExternalContext externalContext = new ExternalContextImpl(getServletContext(), req, resp);
        Lifecycle lifecycle = new LifecycleImpl();
        FacesContext facesContext = new FacesContextImpl(externalContext, lifecycle);
        UIViewRoot viewRoot = new UIViewRoot();
        viewRoot.setViewId(getViewId(req));
        facesContext.setViewRoot(viewRoot);
        facesContext.setCurrentPhaseId(PhaseId.RESTORE_VIEW);
        return facesContext;
    }

    private UIViewRoot restoreViewRoot(HttpServletRequest req) {
        ViewHandler outerViewHandler = FacesContext.getCurrentInstance().getApplication().getViewHandler();
        String renderKitId = outerViewHandler.calculateRenderKitId(FacesContext.getCurrentInstance());
        StateManager stateManager = Util.getStateManager(FacesContext.getCurrentInstance());
        UIViewRoot viewRoot = stateManager.restoreView(FacesContext.getCurrentInstance(), getViewId(req), renderKitId);
        return viewRoot;
    }
    
    private String getViewStateId(HttpServletRequest req) {
        return req.getParameter("javax.faces.ViewState");
    }
    
    private String getConversationId(HttpServletRequest req) {
        return req.getParameter("conversationId");
    }
    
    private String getViewId(HttpServletRequest req) {
        return req.getParameter("viewId");
    }
    
}
	