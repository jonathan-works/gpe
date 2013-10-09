package br.com.infox.core.action.list;

public interface Pageable {
	
	Integer getPage();
	void setPage(Integer page);
	
	Integer getPageCount();
	
	boolean isPreviousExists();
	boolean isNextExists();

}
