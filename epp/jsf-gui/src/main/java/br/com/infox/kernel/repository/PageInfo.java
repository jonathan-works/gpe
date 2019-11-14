package br.com.infox.kernel.repository;

public class PageInfo {
	private int lowerBound;
	private int maxResults;
	
	public PageInfo(int lowerBound, int maxResults) {
		this.lowerBound = lowerBound;
		this.maxResults = maxResults;
	}
	
	public int getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
}
