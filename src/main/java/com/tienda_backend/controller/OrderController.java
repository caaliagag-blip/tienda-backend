package com.tienda_backend.controller;

import com.tienda_backend.model.Order;
import com.tienda_backend.model.OrderItem;
import com.tienda_backend.model.Product;
import com.tienda_backend.repository.OrderRepository;
import com.tienda_backend.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Órdenes de Compra", description = "Registro de compras y consulta de historial de pedidos")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    @SuppressWarnings("unchecked")
    @Operation(summary = "Crear y confirmar una orden de compra")
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> body, Authentication authentication) {
        Order order = new Order();

        String email = (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : String.valueOf(body.getOrDefault("customerEmail", "invitado@levelupgamer.cl"));

        order.setCustomerEmail(email);
        order.setPaymentMethod(String.valueOf(body.getOrDefault("paymentMethod", "Webpay Débito")));

        int calculatedSubtotal = 0;

        Object itemsObj = body.get("items");
        if (itemsObj instanceof List<?> rawItems) {
            for (Object itemEntry : rawItems) {
                if (itemEntry instanceof Map<?, ?>) {
                    Map<String, Object> itemMap = (Map<String, Object>) itemEntry;

                    String code = itemMap.get("productCode") != null
                            ? String.valueOf(itemMap.get("productCode"))
                            : String.valueOf(itemMap.getOrDefault("code", ""));
                    String name = itemMap.get("productName") != null
                            ? String.valueOf(itemMap.get("productName"))
                            : String.valueOf(itemMap.getOrDefault("name", "Producto"));
                    int unitPrice = itemMap.get("unitPrice") instanceof Number n1
                            ? n1.intValue()
                            : (itemMap.get("price") instanceof Number n2 ? n2.intValue() : 0);
                    int quantity = itemMap.get("quantity") instanceof Number q ? q.intValue() : 1;

                    if (unitPrice == 0 && !code.isEmpty()) {
                        Optional<Product> prodOpt = productRepository.findById(code);
                        if (prodOpt.isPresent()) {
                            unitPrice = prodOpt.get().getPrice();
                            if ("Producto".equals(name)) {
                                name = prodOpt.get().getName();
                            }
                        }
                    }

                    calculatedSubtotal += unitPrice * quantity;
                    order.addItem(new OrderItem(code, name, unitPrice, quantity));
                }
            }
        }

        int subtotal = body.get("subtotal") instanceof Number s && s.intValue() > 0
                ? s.intValue()
                : calculatedSubtotal;

        boolean hasDuocDiscount = email.toLowerCase().endsWith("@duocuc.cl")
                || email.toLowerCase().endsWith("@profesor.duoc.cl");

        int discount = body.get("discount") instanceof Number d && d.intValue() > 0
                ? d.intValue()
                : (hasDuocDiscount ? (int) Math.round(subtotal * 0.20) : 0);

        int total = body.get("total") instanceof Number t && t.intValue() > 0
                ? t.intValue()
                : Math.max(0, subtotal - discount);

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/my")
    @Operation(summary = "Obtener historial de pedidos del usuario autenticado")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderRepository.findByCustomerEmailOrderByCreatedAtDesc(email));
    }

    @GetMapping
    @Operation(summary = "Listar todas las órdenes")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }
}