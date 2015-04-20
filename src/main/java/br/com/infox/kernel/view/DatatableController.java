package br.com.infox.kernel.view;

import java.util.List;

import javax.inject.Inject;

public abstract class DatatableController<T> implements Pageable {

	@Inject
	protected PageCalculator pageCalculator;
	private int page = 1;
	private Integer pageCount;
	private int pageSize = 15;
	private Integer resultCount;
	
	@Override
	public int getResultCount() {
		if (resultCount == null) {
		}
		return resultCount;
	}

	@Override
	public int getPageCount() {
		if (pageCount == null) {
			pageCount = pageCalculator.getPageCount(pageSize, getResultCount());
		}
		return pageCount;
	}

	@Override
	public int getPage() {
		return this.page;
	}

	@Override
	public void setPage(int page) {
		if (page < 1 || page > pageCount) {
			throw new IllegalArgumentException("Página " + page + " inexistente");
		}
		this.page = page;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public abstract List<T> getResults();
}
