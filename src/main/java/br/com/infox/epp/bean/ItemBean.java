package br.com.infox.epp.bean;

import br.com.infox.ibpm.entity.Item;

public class ItemBean {

	private Item item;
	private boolean checked;
	
	public ItemBean(Item item) {
		this.item = item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
}