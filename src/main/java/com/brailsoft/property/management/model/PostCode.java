package com.brailsoft.property.management.model;

import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PostCode implements Comparable<PostCode> {
	public static String postCodeRegularExpression = "^[A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}$";
	private StringProperty value = new SimpleStringProperty(this, "value", "");

	public PostCode(String value) {
		if (value == null) {
			throw new IllegalArgumentException("PostCode: value must be specified");
		}
		if (!value.matches(postCodeRegularExpression)) {
			throw new IllegalArgumentException(" is not a valid sort code");
		}
		this.value.set(value);
	}

	public PostCode(PostCode that) {
		if (that == null) {
			throw new IllegalArgumentException("PostCode: value must be specified");
		}
		this.value.set(that.value.get());
	}

	public String getValue() {
		return value.get();
	}

	public StringProperty valueProperty() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value.get());
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
		return Objects.equals(value.get(), that.value.get());
	}

	@Override
	public String toString() {
		return value.get();
	}

	@Override
	public int compareTo(PostCode that) {
		return this.value.get().compareTo(that.value.get());
	}

}
