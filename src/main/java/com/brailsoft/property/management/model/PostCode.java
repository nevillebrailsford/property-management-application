package com.brailsoft.property.management.model;

import java.util.Objects;

public class PostCode implements Comparable<PostCode> {
	private static String postCodeRegularExpression = "^[A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}$";
	private String value;

	public PostCode(String value) {
		if (value == null) {
			throw new IllegalArgumentException("PostCode: value must be specified");
		}
		if (!value.matches(postCodeRegularExpression)) {
			throw new IllegalArgumentException(" is not a valid sort code");
		}
		this.value = value;
	}

	public PostCode(PostCode that) {
		if (that == null) {
			throw new IllegalArgumentException("PostCode: value must be specified");
		}
		this.value = that.value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostCode that = (PostCode) obj;
		return Objects.equals(value, that.value);
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public int compareTo(PostCode that) {
		return this.value.compareTo(that.value);
	}

}
