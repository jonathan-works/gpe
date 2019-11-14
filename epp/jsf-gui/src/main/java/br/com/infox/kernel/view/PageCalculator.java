package br.com.infox.kernel.view;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class PageCalculator {
	public int getPageCount(int pageSize, int resultCount) {
		int pageCount = resultCount / pageSize;
		if (resultCount == pageSize) {
			return pageCount;
		}
		return pageCount + 1;
	}
	
	public int getLowerBound(int pageSize, int page) {
		return (page - 1) * pageSize;
	}
}
