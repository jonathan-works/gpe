package br.com.infox.kernel.view;

public interface Pageable {
	int getResultCount();
	int getPageCount();
	int getPage();
	void setPage(int page);
}
