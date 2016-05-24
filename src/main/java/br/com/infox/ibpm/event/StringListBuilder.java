package br.com.infox.ibpm.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringListBuilder {
	private List<String> list = new ArrayList<>();
	
	public StringListBuilder add(String str) {
		if (str != null) {
			list.add(str);
		}
		return this;
	}
	
	public StringListBuilder add(String[] strArray) {
		if (strArray != null) {
			for (String str : strArray) {
				list.add(str);
			}
		}
		return this;
	}
	
	public StringListBuilder add(Collection<String> strCollection) {
		if (strCollection != null) {
			list.addAll(strCollection);
		}
		return this;
	}
	
	public List<String> build() {
		List<String> result = list;
		list = new ArrayList<>();
		return result;
	}
}
