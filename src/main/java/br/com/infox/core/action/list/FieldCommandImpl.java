package br.com.infox.core.action.list;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import org.jboss.seam.international.Messages;

import br.com.infox.core.type.Displayable;

class FieldCommandImpl implements FieldCommand {
    
    private String entityName;
    private StringBuilder messageBuilder;
    
    public FieldCommandImpl(String entityName, StringBuilder messageBuilder) {
        this.entityName = entityName;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public void execute(SearchField s, Object object) {
        String attributeLabel = "";

        if (object instanceof Boolean) {
        	if (s.getName().equals("ativo")) {
        		attributeLabel = ((Boolean) object) ? "Ativo" : "Inativo";
        	} else {
        		attributeLabel = ((Boolean) object) ? "Sim" : "Não";
        	}
        } else if (object instanceof Date) {
            attributeLabel = DateFormat.getDateInstance().format(object);
        } else if (object instanceof Displayable) {
    		attributeLabel = ((Displayable) object).getLabel();
        } else {
        	attributeLabel = object.toString();
        }
        
        messageBuilder.append(Messages.instance().get(MessageFormat.format(
                "{0}.{1}", entityName, s.getName())))
                .append(" ")
                .append(s.getCriteria())
                .append(" '")
                .append(attributeLabel)
                .append("'\n");
    }

}
