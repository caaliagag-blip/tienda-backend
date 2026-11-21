package com.tienda_backend.config;

import com.tienda_backend.model.Product;
import com.tienda_backend.model.User;
import com.tienda_backend.repository.ProductRepository;
import com.tienda_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ProductRepository productRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            productRepository.saveAll(List.of(
                new Product("JM001", "Juegos de Mesa", "Catan", 29990, "Un clásico juego de estrategia donde los jugadores compiten por colonizar la isla de Catan."),
                new Product("JM002", "Juegos de Mesa", "Carcassonne", 24990, "Un juego de colocación de fichas donde los jugadores construyen el paisaje medieval."),
                new Product("AC001", "Accesorios", "Controlador Inalámbrico Xbox Series X", 59990, "Ofrece experiencia cómoda con botones mapeables y respuesta táctil mejorada."),
                new Product("AC002", "Accesorios", "Auriculares Gamer HyperX Cloud II", 79990, "Sonido envolvente de calidad con micrófono desmontable y almohadillas confortables."),
                new Product("CO001", "Consolas", "PlayStation 5", 549990, "Consola de última generación de Sony con gráficos impresionantes y carga ultrarrápida."),
                new Product("CG001", "Computadores Gamers", "PC Gamer ASUS ROG Strix", 1299990, "Potente equipo para gamers exigentes equipado con los últimos componentes."),
                new Product("SG001", "Sillas Gamers", "Silla Gamer Secretlab Titan", 349990, "Máximo confort y soporte ergonómico para largas sesiones de juego."),
                new Product("MS001", "Mouse", "Mouse Gamer Logitech G502 HERO", 49990, "Sensor de alta precisión y botones personalizables para un control exacto."),
                new Product("MP001", "Mousepad", "Mousepad Razer Goliathus Extended Chroma", 29990, "Área de juego amplia con iluminación RGB personalizable."),
                new Product("PP001", "Poleras Personalizadas", "Polera Gamer Personalizada Level-Up", 149990, "Camiseta cómoda con opción de personalización con tu Gamer Tag.")
            ));
        }

        if (!userRepository.existsByEmail("admin@duocuc.cl")) {
            userRepository.save(new User("Admin LevelUp", "admin@duocuc.cl", passwordEncoder.encode("admin123"), "ROLE_ADMIN", true));
        }
        if (!userRepository.existsByEmail("gamer@duocuc.cl")) {
            userRepository.save(new User("PlayerOne", "gamer@duocuc.cl", passwordEncoder.encode("gamer123"), "ROLE_USER", true));
        }
    }
}