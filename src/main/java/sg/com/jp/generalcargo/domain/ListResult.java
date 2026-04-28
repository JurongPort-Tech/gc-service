package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class ListResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final List<String> FIELDS = Arrays.asList("start", "limit", "sort", "dir", "total", "data");

	// called by filter in repository
	public static boolean isFieldValid(String field) {
		return field == null ? false : FIELDS.contains(field);
	}

	private Integer start;

	private Integer limit;

	private String sort;

	private String dir;

	private Integer total;

	private List<?> data;

	private Map<?, ?> mapData;

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
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

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public Map<?, ?> getMapData() {
		return mapData;
	}

	public void setMapData(Map<?, ?> data) {
		this.mapData = data;
	}
}
