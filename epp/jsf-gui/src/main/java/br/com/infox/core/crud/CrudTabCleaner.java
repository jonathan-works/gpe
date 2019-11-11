package br.com.infox.core.crud;

import java.util.HashSet;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

import org.richfaces.component.UIDataTable;

import br.com.infox.componentes.tabs.Tab;
import br.com.infox.componentes.tabs.TabChangeEvent;
import br.com.infox.componentes.tabs.TabChangeListener;
import br.com.infox.componentes.tabs.TabPanel;

@ManagedBean
@RequestScoped
public class CrudTabCleaner implements TabChangeListener {

    private static final String SEARCH_TAB_NAME = "search";

    @Override
    public void processTabChange(TabChangeEvent event) {
        Tab newTab = event.getNewTab();
        if (newTab.getName().equals(SEARCH_TAB_NAME)) {
            clearValueHolders(newTab.getTabPanel());
        }
    }

    private void clearValueHolders(TabPanel tabPanel) {
        Set<VisitHint> hints = new HashSet<>();
        hints.add(VisitHint.SKIP_TRANSIENT);
        hints.add(VisitHint.SKIP_ITERATION);

        VisitContext context = VisitContext.createVisitContext(FacesContext.getCurrentInstance(), null, hints);
        ClearValueHoldersCallback clearValueHoldersCallback = new ClearValueHoldersCallback();

        for (Tab tab : tabPanel.getTabs()) {
            if (!tab.getName().equals(SEARCH_TAB_NAME)) {
                tab.visitTree(context, clearValueHoldersCallback);
            }
        }
    }

    private static class ClearValueHoldersCallback implements VisitCallback {

        @Override
        public VisitResult visit(VisitContext context, UIComponent target) {
            if (target instanceof EditableValueHolder) {
                EditableValueHolder valueHolder = (EditableValueHolder) target;
                valueHolder.resetValue();
            } else if (target instanceof UIDataTable || target instanceof UIData) {
            	return VisitResult.REJECT;
            }
            return VisitResult.ACCEPT;
        }
    }
}
