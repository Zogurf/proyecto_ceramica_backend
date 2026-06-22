package com.example.backend.services;

import com.example.backend.dto.CategoryIntentAnalyticsResponse;
import com.example.backend.dto.DashboardResponse;
import com.example.backend.models.Order;
import com.example.backend.models.Product;
import com.example.backend.models.PurchaseIntent;
import com.example.backend.repositories.OrderRepository;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.PurchaseIntentRepository;
import com.example.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PurchaseIntentRepository intentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        List<Order> paid = orderRepository.findAll().stream().filter(o -> "PAID".equals(o.getStatus())).toList();
        List<Product> products = productRepository.findAll();
        LocalDate today = LocalDate.now();
        return new DashboardResponse(
                paid.stream().mapToDouble(Order::getTotal).sum(), userRepository.count(),
                products.stream().mapToLong(Product::getStock).sum(),
                orderRepository.findAll().stream().filter(o -> "PENDING".equals(o.getStatus())).count(),
                products.stream().filter(p -> p.getStock() <= 5).count(),
                revenueByHour(paid, today), revenueByDay(paid, today), revenueByMonth(paid, today),
                groupInventory(products), groupInteractions(),
                products.stream().filter(p -> p.getStock() <= 5).sorted(Comparator.comparingInt(Product::getStock))
                        .map(p -> new DashboardResponse.LowStockProduct(p.getId(), p.getName(), p.getStock(),
                                p.getCategory() != null ? p.getCategory().getName() : "Sin categoria")).toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryIntentAnalyticsResponse> categoryIntentions(LocalDate start, LocalDate end) {
        LocalDate from = start != null ? start : LocalDate.now().minusDays(30);
        LocalDate to = end != null ? end : LocalDate.now();
        List<PurchaseIntent> intents = intentRepository.findAllByViewedAtBetweenOrderByViewedAtDesc(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));
        return intents.stream().collect(Collectors.groupingBy(i -> i.getProduct().getCategory(), LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream().map(entry -> {
                    List<PurchaseIntent> values = entry.getValue();
                    Map<Product, Long> top = values.stream().collect(Collectors.groupingBy(PurchaseIntent::getProduct, Collectors.counting()));
                    return new CategoryIntentAnalyticsResponse(entry.getKey().getId(), entry.getKey().getName(), values.size(),
                            values.stream().map(i -> i.getUser().getId()).collect(Collectors.toSet()).size(),
                            top.entrySet().stream().sorted(Map.Entry.<Product, Long>comparingByValue().reversed()).limit(5)
                                    .map(e -> new CategoryIntentAnalyticsResponse.ProductInteractionResponse(e.getKey().getId(), e.getKey().getName(), e.getValue())).toList());
                }).sorted(Comparator.comparingLong(CategoryIntentAnalyticsResponse::interactions).reversed()).toList();
    }

    private List<DashboardResponse.MetricPoint> revenueByHour(List<Order> orders, LocalDate date) {
        List<DashboardResponse.MetricPoint> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour += 3) {
            int h = hour;
            double value = orders.stream().filter(o -> o.getRegisterDate().toLocalDate().equals(date)
                    && o.getRegisterDate().getHour() >= h && o.getRegisterDate().getHour() < h + 3).mapToDouble(Order::getTotal).sum();
            result.add(new DashboardResponse.MetricPoint(String.format("%02d:00", hour), value));
        }
        return result;
    }

    private List<DashboardResponse.MetricPoint> revenueByDay(List<Order> orders, LocalDate today) {
        List<DashboardResponse.MetricPoint> result = new ArrayList<>();
        for (int days = 6; days >= 0; days--) {
            LocalDate date = today.minusDays(days);
            double value = orders.stream().filter(o -> o.getRegisterDate().toLocalDate().equals(date)).mapToDouble(Order::getTotal).sum();
            result.add(new DashboardResponse.MetricPoint(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "PE")), value));
        }
        return result;
    }

    private List<DashboardResponse.MetricPoint> revenueByMonth(List<Order> orders, LocalDate today) {
        List<DashboardResponse.MetricPoint> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            int m = month;
            double value = orders.stream().filter(o -> o.getRegisterDate().getYear() == today.getYear() && o.getRegisterDate().getMonthValue() == m).mapToDouble(Order::getTotal).sum();
            result.add(new DashboardResponse.MetricPoint(java.time.Month.of(month).getDisplayName(TextStyle.SHORT, new Locale("es", "PE")), value));
        }
        return result;
    }

    private List<DashboardResponse.MetricPoint> groupInventory(List<Product> products) {
        Map<String, Integer> grouped = products.stream().collect(Collectors.groupingBy(
                p -> p.getCategory() != null ? p.getCategory().getName() : "Sin categoria", LinkedHashMap::new, Collectors.summingInt(Product::getStock)));
        return grouped.entrySet().stream().map(e -> new DashboardResponse.MetricPoint(e.getKey(), e.getValue())).toList();
    }

    private List<DashboardResponse.MetricPoint> groupInteractions() {
        Map<String, Long> grouped = intentRepository.findAll().stream().collect(Collectors.groupingBy(
                i -> i.getProduct().getCategory().getName(), Collectors.counting()));
        return grouped.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new DashboardResponse.MetricPoint(e.getKey(), e.getValue())).toList();
    }
}
