package com.example.backend.jobs;

import com.example.backend.models.Order;
import com.example.backend.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class DailySalesReportJob {

    private final OrderRepository orderRepository;

    @Value("${app.reports.daily-sales.zone:America/Lima}")
    private String zone;

    @Scheduled(cron = "${app.reports.daily-sales.cron:0 5 0 * * *}", zone = "${app.reports.daily-sales.zone:America/Lima}")
    @Transactional(readOnly = true)
    public void generateDailySalesReport() {
        LocalDate reportDate = LocalDate.now(java.time.ZoneId.of(zone)).minusDays(1);
        LocalDateTime start = reportDate.atStartOfDay();
        LocalDateTime end = reportDate.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository
                .findByRegisterDateGreaterThanEqualAndRegisterDateLessThan(start, end);

        double totalSales = orders.stream()
                .map(Order::getTotal)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
        int unitsSold = orders.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity())
                .sum();

        log.info("REPORTE_DIARIO fecha={} pedidos={} unidades={} totalVentas=S/ {}",
                reportDate, orders.size(), unitsSold,
                String.format(Locale.US, "%.2f", totalSales));
    }
}
