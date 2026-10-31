package com.brightloop.catalog;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="customers")
public class Customer {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="full_name",nullable=false,length=160) private String fullName;
 @Column(nullable=false,unique=true,length=190) private String email;
 @Column(length=40) private String phone;
 @Column(name="company_name",length=160) private String companyName;
 @Column(name="billing_address") private String billingAddress;
 @Column(length=100) private String city; @Column(length=100) private String state; @Column(length=100) private String country;
 @Column(name="postal_code",length=30) private String postalCode;
 @Column(name="tax_id",length=80) private String taxId;
 @Column(nullable=false,length=20) private String status="ACTIVE";
 @Column(name="registered_at",nullable=false) private Instant registeredAt=Instant.now();
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected Customer(){}
 public Customer(String name,String email,String phone,String company,String address,String city,String state,String country,String postal,String tax){this.fullName=name;this.email=email.toLowerCase().trim();this.phone=phone;this.companyName=company;this.billingAddress=address;this.city=city;this.state=state;this.country=country;this.postalCode=postal;this.taxId=tax;}
 @PreUpdate void touch(){updatedAt=Instant.now();}
 public Long getId(){return id;} public String getFullName(){return fullName;} public String getEmail(){return email;} public String getPhone(){return phone;} public String getCompanyName(){return companyName;} public String getBillingAddress(){return billingAddress;} public String getCity(){return city;} public String getState(){return state;} public String getCountry(){return country;} public String getPostalCode(){return postalCode;} public String getTaxId(){return taxId;} public String getStatus(){return status;} public Instant getRegisteredAt(){return registeredAt;}
 public void update(String name,String email,String phone,String company,String address,String city,String state,String country,String postal,String tax,String status){this.fullName=name;this.email=email.toLowerCase().trim();this.phone=phone;this.companyName=company;this.billingAddress=address;this.city=city;this.state=state;this.country=country;this.postalCode=postal;this.taxId=tax;this.status=status;}
}
