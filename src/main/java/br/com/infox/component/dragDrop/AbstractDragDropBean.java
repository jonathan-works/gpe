package br.com.infox.component.dragDrop;

import java.util.ArrayList;
import java.util.List;

import org.richfaces.component.Dropzone;
import org.richfaces.event.DropEvent;

import br.com.infox.core.action.list.EntityList;

public abstract class AbstractDragDropBean<T, Z> implements DragDropBean<Z> {
	
	private List<Z> dropList = new ArrayList<Z>();
	
	public abstract EntityList<T> getDragEntityList();
	
	public abstract Z processDrop(T obj);

	@SuppressWarnings("unchecked")
	public void processDrop(DropEvent dropEvent) {
		Dropzone dropzone = (Dropzone) dropEvent.getComponent();
		T dragValue = (T) dropEvent.getDragValue();
		Object dropType = dropzone.getDropValue();
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
