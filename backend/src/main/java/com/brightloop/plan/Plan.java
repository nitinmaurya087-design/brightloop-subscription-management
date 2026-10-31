package com.brightloop.plan;
import com.brightloop.catalog.Product;import jakarta.persistence.*;import java.math.BigDecimal;import java.time.Instant;import java.time.LocalDate;
@Entity @Table(name="plans")
public class Plan {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @Column(nullable=false,length=160) private String name;@Column(columnDefinition="TEXT") private String description;
 @Column(name="billing_price",nullable=false,precision=12,scale=2) private BigDecimal billingPrice;
 @Enumerated(EnumType.STRING) @Column(name="billing_frequency",nullable=false,length=20) private BillingFrequency billingFrequency;
 @Column(name="minimum_quantity",nullable=false) private int minimumQuantity=1;
 @Column(name="available_from") private LocalDate availableFrom;@Column(name="available_until") private LocalDate availableUntil;
 @Column(name="auto_close_on_expiry",nullable=false) private boolean autoCloseOnExpiry=true;@Column(name="allow_early_closure",nullable=false) private boolean allowEarlyClosure=true;
 @Column(name="allow_pause",nullable=false) private boolean allowPause=true;@Column(name="allow_renewal",nullable=false) private boolean allowRenewal=true;
 @Column(nullable=false,length=20) private String status="ACTIVE";
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();@Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected Plan(){} public Plan(Product product,String name,String description,BigDecimal price,BillingFrequency frequency,int quantity,LocalDate from,LocalDate until,boolean autoClose,boolean early,boolean pause,boolean renew,String status){this.product=product;update(name,description,price,frequency,quantity,from,until,autoClose,early,pause,renew,status);}
 @PreUpdate void touch(){updatedAt=Instant.now();}
 public void update(String name,String description,BigDecimal price,BillingFrequency frequency,int quantity,LocalDate from,LocalDate until,boolean autoClose,boolean early,boolean pause,boolean renew,String status){this.name=name;this.description=description;this.billingPrice=price;this.billingFrequency=frequency;this.minimumQuantity=quantity;this.availableFrom=from;this.availableUntil=until;this.autoCloseOnExpiry=autoClose;this.allowEarlyClosure=early;this.allowPause=pause;this.allowRenewal=renew;this.status=status;}
 public void changeProduct(Product product){this.product=product;}
 public Long getId(){return id;}public Product getProduct(){return product;}public String getName(){return name;}public String getDescription(){return description;}public BigDecimal getBillingPrice(){return billingPrice;}public BillingFrequency getBillingFrequency(){return billingFrequency;}public int getMinimumQuantity(){return minimumQuantity;}public LocalDate getAvailableFrom(){return availableFrom;}public LocalDate getAvailableUntil(){return availableUntil;}public boolean isAutoCloseOnExpiry(){return autoCloseOnExpiry;}public boolean isAllowEarlyClosure(){return allowEarlyClosure;}public boolean isAllowPause(){return allowPause;}public boolean isAllowRenewal(){return allowRenewal;}public String getStatus(){return status;}
}
