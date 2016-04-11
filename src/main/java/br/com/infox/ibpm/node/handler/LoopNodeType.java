package br.com.infox.ibpm.node.handler;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.type.Displayable;

public enum LoopNodeType implements Displayable {
    NONE,STANDARD,MULTIINSTANCE;

    @Override
    public String getLabel() {
        return InfoxMessages.getInstance().get("process.def.activity.loopType."+name());
    }
}
