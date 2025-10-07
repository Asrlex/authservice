package dev.api.auth.authservice.common.entities.search;

public class SearchCriteria {

	public record Filter(String field, SqlParameters.SqlOperator operator, Object value) {}

	public record Sorter(String field, SqlParameters.SqlOrder operator) {}

	private final Filter[] filters;
	private final Sorter[] sorters;
	private final Integer page;
	private final Integer pageSize;

	public SearchCriteria() {
		super();
		this.filters = new Filter[] {};
		this.sorters = new Sorter[] {};
		this.page = 0;
		this.pageSize = 10;
	}

	public SearchCriteria(Filter[] filters, Sorter[] sorters, Integer page, Integer pageSize) {
		super();
		this.filters = filters;
		this.sorters = sorters;
		this.page = page;
		this.pageSize = pageSize;
	}

	public Filter[] getFilters() {
		return filters;
	}
	public Sorter[] getSorters() {
		return sorters;
	}
	public Integer getPage() {
		return page;
	}
	public Integer getPageSize() {
		return pageSize;
	}
}
