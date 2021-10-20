package com.brailsoft.property.management.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Address implements Comparable<Address> {
	private List<String> linesOfAddress = new ArrayList<>();
	private PostCode postCode = null;
	private String comma = "";

	public Address(PostCode postCode, String[] linesOfAddress) {
		if (postCode == null) {
			throw new IllegalArgumentException("Address: post code must be specified");
		}
		if (linesOfAddress == null) {
			throw new IllegalArgumentException("Address: lines of address must be specified");
		}
		if (linesOfAddress.length == 0) {
			throw new IllegalArgumentException("Address: lines of address must have contents");
		}
		this.postCode = postCode;
		for (int i = 0; i < linesOfAddress.length; i++) {
			this.linesOfAddress.add(linesOfAddress[i]);
		}
	}

	public Address(Address that) {
		if (that == null) {
			throw new IllegalArgumentException("Address: address must be specified");
		}
		this.postCode = new PostCode(that.postCode);
		that.linesOfAddress.stream().forEach(line -> {
			this.linesOfAddress.add(line);
		});
	}

	public String[] getLinesOfAddress() {
		String[] lines = new String[linesOfAddress.size()];
		int index = 0;
		for (String line : linesOfAddress) {
			lines[index++] = line;
		}
		return lines;
	}

	public PostCode getPostCode() {
		return new PostCode(postCode);
	}

	@Override
	public int compareTo(Address that) {
		return this.toString().compareTo(that.toString());
	}

	@Override
	public int hashCode() {
		return Objects.hash(linesOfAddress, postCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address that = (Address) obj;
		return Objects.equals(linesOfAddress, that.linesOfAddress) && Objects.equals(postCode, that.postCode);
	}

	@Override
	public String toString() {
		comma = "";
		StringBuilder builder = new StringBuilder();
		linesOfAddress.stream().forEach(line -> {
			builder.append(comma).append(line);
			comma = ", ";
		});
		builder.append(" ").append(postCode);
		return builder.toString();
	}

}
