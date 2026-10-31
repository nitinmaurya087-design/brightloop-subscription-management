package com.brightloop.catalog;
import jakarta.persistence.*;import java.math.BigDecimal;import java.time.Instant;
@Entity @Table(name="products")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=160) private String name; @Column(name="product_type",nullable=false,length=80) private String productType;
 @Column(columnDefinition="TEXT") private String description; @Column(name="sales_price",nullable=false,precision=12,scale=2) private BigDecimal salesPrice;
 @Column(name="cost_price",precision=12,scale=2) private BigDecimal costPrice; @Column(columnDefinition="TEXT") private String notes;
 @Column(nullable=false,length=20) private String status="ACTIVE";
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now(); @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected Product(){} public Product(String name,String type,String desc,BigDecimal sales,BigDecimal cost,String notes,String status){this.name=name;this.productType=type;this.description=desc;this.salesPrice=sales;this.costPrice=cost;this.notes=notes;this.status=status;}
 @PreUpdate void touch(){updatedAt=Instant.now();} public Long getId(){return id;} public String getName(){return name;} public String getProductType(){return productType;} public String getDescription(){return description;} public BigDecimal getSalesPrice(){return salesPrice;} public BigDecimal getCostPrice(){return costPrice;} public String getNotes(){return notes;} public String getStatus(){return status;}
 public void update(String name,String type,String desc,BigDecimal sales,BigDecimal cost,String notes,String status){this.name=name;this.productType=type;this.description=desc;this.salesPrice=sales;this.costPrice=cost;this.notes=notes;this.status=status;}
}
