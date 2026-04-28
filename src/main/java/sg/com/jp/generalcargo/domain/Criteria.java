package sg.com.jp.generalcargo.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Revision History
 * ------------------------------------------------------------------------------------------------------
 * Author			Description												Version			Date
 * ------------------------------------------------------------------------------------------------------
 * MC Consulting	First Version											1.0				27-Mar-2019
 */

public class Criteria {

	private int start = 0;

	private int limit = 10;

	private String sort;

	private String dir;

	private Map<String, String> predicates = new HashMap<String, String>();

	private boolean audit = false;

	public Criteria() {
	}

	public boolean isPaginated() {
		return this.start >= 0 && this.limit > 0;
	}

	public void addPredicate(String key, String value) {
		this.predicates.put(key, value);
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public Map<String, String> getPredicates() {
		return predicates;
	}

	public void setPredicates(Map<String, String> predicates) {
		this.predicates = predicates;
	}

	public boolean isAudit() {
		return audit;
	}

	public void setAudit(boolean audit) {
		this.audit = audit;
	}

}
