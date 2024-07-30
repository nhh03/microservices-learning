package com.nhh203.orderservice.viewmodel.order;

import java.util.List;

public record OrderListVm(
		List<OrderBriefVm> orderList,
		long totalElements,
		int totalPages
) {

}
