package br.com.infox.core.action.list;

public interface Pageable {
	
	public Integer getPage();
	public void setPage(Integer page);
	
	public Integer getPageCount();
	
	public boolean isPreviousExists();
	public boolean isNextExists();

}
