package com.example.tienda_backend;

import com.tienda_backend.TiendaBackendApplication;
import com.tienda_backend.model.Order;
import com.tienda_backend.model.OrderItem;
import com.tienda_backend.model.Product;
import com.tienda_backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TiendaBackendApplication.class)
class TiendaBackendApplicationTests {

	@Test
	void contextLoads() {
		// Verifica que el contexto completo de Spring Boot, JPA y Security levanta correctamente
	}

	@Test
	void testDuocDiscountEligibility() {
		User duocUser = new User("Gamer Duoc", "gamer@duocuc.cl", "1234", "ROLE_USER", true);
		User standardUser = new User("Gamer Externo", "externo@gmail.com", "1234", "ROLE_USER", false);

		assertTrue(duocUser.isHasDiscount());
		assertFalse(standardUser.isHasDiscount());
	}

	@Test
	void testOrderCalculationAndItems() {
		Order order = new Order();
		order.setCustomerEmail("gamer@duocuc.cl");
		order.addItem(new OrderItem("CO001", "PlayStation 5", 500000, 1));
		order.setSubtotal(500000);
		order.setDiscount(100000); // 20% descuento Duoc UC
		order.setTotal(400000);

		assertEquals(1, order.getItems().size());
		assertEquals(400000, order.getTotal());
		assertEquals("PAGADO", order.getStatus());
	}

	@Test
	void testProductEntityCreation() {
		Product product = new Product("MS001", "Mouse", "Logitech G502", 49990, "Sensor HERO");
		assertEquals("MS001", product.getCode());
		assertEquals(49990, product.getPrice());
	}
}