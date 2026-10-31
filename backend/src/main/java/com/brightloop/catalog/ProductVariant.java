package com.brightloop.catalog;
import jakarta.persistence.*;import java.math.BigDecimal;import java.time.Instant;
@Entity @Table(name="product_variants")
public class ProductVariant {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @Column(nullable=false,length=120) private String name; @Column(name="attribute_value") private String attributeValue;
 @Column(name="additional_price",nullable=false,precision=12,scale=2) private BigDecimal additionalPrice=BigDecimal.ZERO;
 @Column(nullable=false,length=20) private String status="ACTIVE"; @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected ProductVariant(){} public ProductVariant(Product p,String name,String attr,BigDecimal price,String status){this.product=p;this.name=name;this.attributeValue=attr;this.additionalPrice=price;this.status=status;}
 public Long getId(){return id;} public Product getProduct(){return product;} public String getName(){return name;} public String getAttributeValue(){return attributeValue;} public BigDecimal getAdditionalPrice(){return additionalPrice;} public String getStatus(){return status;}
 public void update(String name,String attr,BigDecimal price,String status){this.name=name;this.attributeValue=attr;this.additionalPrice=price;this.status=status;}
}
