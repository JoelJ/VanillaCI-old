package com.joelj.vanillaci.run;

import com.joelj.vanillaci.util.Confirm;

/**
 * User: Joel Johnson
 * Date: 12/11/12
 * Time: 8:51 PM
 */
public enum Status {
	Success,
	Unstable,
	Failure_Continue,
	Failure,
	Error,
	Aborted,
	Unknown;

	public boolean isWorseThan(Status status) {
		Confirm.notNull("status", status);
		return (this.ordinal() > status.ordinal());
	}

	public boolean isBetterThan(Status status) {
		Confirm.notNull("status", status);
		return (this.ordinal() < status.ordinal());
	}

	public static Status bestValueOf(String value) {
		for (Status status : Status.values()) {
			if(status.toString().equalsIgnoreCase(value)) {
				return status;
			}
		}
		return Unknown;
	}
}
