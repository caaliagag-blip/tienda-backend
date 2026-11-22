package com.tienda_backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(length = 20)
    @Schema(example = "SW001")
    private String code;

    @Column(nullable = false)
    @Schema(example = "Consolas")
    private String category;

    @Column(nullable = false)
    @Schema(example = "Nintendo Switch OLED")
    private String name;

    @Column(nullable = false)
    @Schema(example = "349990")
    private Integer price;

    @Column(name = "description", length = 500)
    @Schema(example = "Consola híbrida con pantalla OLED de 7 pulgadas y 64 GB de almacenamiento.")
    private String desc;

    public Product() {}

    public Product(String code, String category, String name, Integer price, String desc) {
        this.code = code;
        this.category = category;
        this.name = name;
        this.price = price;
        this.desc = desc;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
}