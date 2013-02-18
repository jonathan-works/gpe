package br.com.infox.component.dragDrop;

import java.util.ArrayList;
import java.util.List;

import org.richfaces.event.DropEvent;

import br.com.infox.core.action.list.EntityList;

public abstract class AbstractDragDropBean<T, Z> implements DragDropBean<Z> {
	
	private List<Z> dropList = new ArrayList<Z>();
	
	public abstract EntityList<T> getDragEntityList();
	
	public abstract Z processDrop(T obj);

	@Override
	public void processDrop(DropEvent dropEvent) {
		T dragValue = (T) dropEvent.getDragValue();
		Object dropType = dropEvent.getDropValue();
		if ("TYPE".equals(dropType)) {
			Z dropValue = processDrop(dragValue);
			if (dropValue != null) {
				dropList.add(dropValue);
			}
		}
	}
	
	public List<Z> getDropList() {
		return dropList;
	}
}
