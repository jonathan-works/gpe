package br.com.infox.core.comparators;

import java.util.Comparator;

import javax.faces.model.SelectItem;

public class Comparators {

	private Comparators(){ }

	public static final Comparator<SelectItem> bySelectItemLabelAsc(){
		return new Comparator<SelectItem>() {
			@Override
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		};
	}

	public static final Comparator<SelectItem> bySelectItemLabelDesc(){
		return new Comparator<SelectItem>() {
			@Override
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		};
	}
	
}
