package com.brailsoft.property.management.audit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AuditRecord implements Comparable<AuditRecord> {
	private final AuditType auditType;
	private final AuditObject auditObject;
	private String description;
	private LocalDateTime timeStamp;
	private String user;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

	public AuditRecord(AuditType auditType, AuditObject auditObject) {
		super();
		this.auditType = auditType;
		this.auditObject = auditObject;
		this.timeStamp = LocalDateTime.now();
		this.user = System.getProperty("user.name");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AuditType getAuditType() {
		return auditType;
	}

	public AuditObject getAuditObject() {
		return auditObject;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (timeStamp != null) {
			builder.append(timeStamp.format(formatter));
			builder.append(" ");
		}
		if (user != null) {
			builder.append(user);
			builder.append(" ");
		}
		if (auditType != null) {
			builder.append(auditType);
			builder.append(" ");
		}
		if (auditObject != null) {
			builder.append(auditObject);
			builder.append(" ");
		}
		if (description != null) {
			builder.append(description);
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(timeStamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuditRecord that = (AuditRecord) obj;
		return Objects.equals(this.timeStamp, that.timeStamp);
	}

	@Override
	public int compareTo(AuditRecord that) {
		return this.timeStamp.compareTo(that.timeStamp);
	}

}
