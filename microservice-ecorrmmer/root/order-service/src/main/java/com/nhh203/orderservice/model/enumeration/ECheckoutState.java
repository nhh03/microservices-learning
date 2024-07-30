package com.nhh203.orderservice.model.enumeration;

public enum ECheckoutState {

	COMPLETED("Completed"), PENDING("Pending"), LOCK("LOCK");

	private final String name;

	ECheckoutState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
