package br.com.infox.component.dragDrop;

import org.richfaces.event.DropEvent;

public interface DragDropBean<T> {
	void processDrop(DropEvent dropEvent);
}
