package br.com.infox.core.type;

import java.util.Comparator;

public class DisplayableComparator implements Comparator<Displayable> {
	
	private Order order;
	
	public DisplayableComparator() {
		this(Order.ASC);
	}
	
	public DisplayableComparator(Order order) {
		if (order == null) {
			order = Order.ASC;
		}
		this.order = order;
	}
	
	@Override
	public int compare(Displayable o1, Displayable o2) {
		if (order == Order.ASC) {
			return o1.getLabel().compareToIgnoreCase(o2.getLabel());
		} else {
			return o2.getLabel().compareToIgnoreCase(o1.getLabel());
		}
	}
	
	public static enum Order {
		ASC, DESC;
	}
}
