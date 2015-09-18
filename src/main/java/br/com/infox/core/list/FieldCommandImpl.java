package br.com.infox.core.list;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.type.Displayable;
import br.com.infox.seam.util.ComponentUtil;

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

        InfoxMessages infoxMessages = ComponentUtil.getComponent(InfoxMessages.NAME);
        messageBuilder.append(infoxMessages.get(MessageFormat.format("{0}.{1}", entityName, s.getName()))).append(" ").append(s.getCriteria()).append(" '").append(attributeLabel).append("'\n");
    }

}
