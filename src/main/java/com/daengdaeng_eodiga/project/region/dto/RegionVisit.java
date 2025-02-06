package com.daengdaeng_eodiga.project.region.dto;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RegionVisit<T> implements Serializable {
	private HashMap<String,HashMap<String,T>> visitInfo;
}
