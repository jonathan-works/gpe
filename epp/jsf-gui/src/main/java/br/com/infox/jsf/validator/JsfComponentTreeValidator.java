package br.com.infox.jsf.validator;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JsfComponentTreeValidator {

    public boolean hasInvalidComponent(UIComponent root) {
        if (root == null) {
            throw new IllegalArgumentException("O componente raiz não pode ser nulo");
        }

        Set<VisitHint> hints = new HashSet<>();
        hints.add(VisitHint.EXECUTE_LIFECYCLE);
        hints.add(VisitHint.SKIP_UNRENDERED);
        hints.add(VisitHint.SKIP_TRANSIENT);
        hints.add(VisitHint.SKIP_ITERATION);

        VisitContext context = VisitContext.createVisitContext(FacesContext.getCurrentInstance(), null, hints);
        CheckValidComponentCallback checkValidComponentCallback = new CheckValidComponentCallback();

        root.visitTree(context, checkValidComponentCallback);

        return checkValidComponentCallback.hasInvalidComponent;
    }

    private static class CheckValidComponentCallback implements VisitCallback {

        private boolean hasInvalidComponent = false;

        @Override
        public VisitResult visit(VisitContext context, UIComponent target) {
            if (target instanceof UIInput) {
                UIInput input = (UIInput) target;
                if (!input.isValid()) {
                    hasInvalidComponent = true;
                    return VisitResult.COMPLETE;
                }
            }
            return VisitResult.ACCEPT;
        }
    }
}
