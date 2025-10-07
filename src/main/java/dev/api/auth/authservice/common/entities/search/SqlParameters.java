package dev.api.auth.authservice.common.entities.search;

public class SqlParameters {

	public static class SqlOperator {
		public static final String EQUALS = "=";
		public static final String NOT_EQUALS = "<>";
		public static final String GREATER_THAN = ">";
		public static final String LESS_THAN = "<";
		public static final String GREATER_THAN_OR_EQUALS = ">=";
		public static final String LESS_THAN_OR_EQUALS = "<=";
		public static final String LIKE = "LIKE";
		public static final String IN = "IN";
		public static final String NOT_IN = "NOT IN";
		public static final String IS_NULL = "IS NULL";
		public static final String IS_NOT_NULL = "IS NOT NULL";
		public static final String BETWEEN = "BETWEEN";
		public static final String AND = "AND";
		public static final String OR = "OR";
	}

	public static class SqlOrder {
		public static final String ASC = "ASC";
		public static final String DESC = "DESC";
	}
}
