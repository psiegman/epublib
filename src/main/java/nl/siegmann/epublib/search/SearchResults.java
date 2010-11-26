package nl.siegmann.epublib.search;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;

public class SearchResults {
	private String searchTerm;
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public List<SearchResult> getHits() {
		return hits;
	}
	public void setHits(List<SearchResult> hits) {
		this.hits = hits;
	}
	private Book book;
	private List<SearchResult> hits = new ArrayList<SearchResult>();
	public boolean isEmpty() {
		return hits.isEmpty();
	}
	public int size() {
		return hits.size();
	}
	public void addAll(List<SearchResult> searchResults) {
		hits.addAll(searchResults);
	}
}