package br.com.infox.core.action.list;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import org.jboss.seam.international.Messages;

class FieldCommandImpl implements FieldCommand {
    
    private String entityName;
    private StringBuilder messageBuilder;
    
    public FieldCommandImpl(String entityName, StringBuilder messageBuilder) {
        this.entityName = entityName;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public void execute(SearchField s, Object object) {

        // Trata os tipos Booleanos
        String atributeLabel = "";
        if (object instanceof Boolean) {
            atributeLabel = Messages.instance().get(MessageFormat.format(
                    "{0}.{1}.{2}", entityName, s.getName(), (Boolean)object));
        } else if (object instanceof Date) {
            atributeLabel = DateFormat.getDateInstance().format(object);
        } else {
            // Caso não for booleano
            atributeLabel = object.toString();
        }

        
        messageBuilder.append(Messages.instance().get(MessageFormat.format(
                "{0}.{1}", entityName, s.getName())))
                .append(" ")
                .append(s.getCriteria())
                .append(" '")
                .append(atributeLabel)
                .append("'\n");
    }

}
