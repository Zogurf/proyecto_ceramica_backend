package com.example.backend.dto;

import java.util.List;

public record DashboardResponse(
        double totalRevenue,
        long userCount,
        long inventoryUnits,
        long pendingOrders,
        long lowStockProducts,
        List<MetricPoint> revenueToday,
        List<MetricPoint> revenueWeekly,
        List<MetricPoint> revenueMonthly,
        List<MetricPoint> inventoryByCategory,
        List<MetricPoint> interactionsByCategory,
        List<LowStockProduct> lowStock
) {
    public record MetricPoint(String label, double value) {}
    public record LowStockProduct(Long id, String name, int stock, String categoryName) {}
}
