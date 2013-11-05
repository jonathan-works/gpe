package br.com.infox.epp.fluxo.bean;

import br.com.infox.epp.fluxo.entity.Item;

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