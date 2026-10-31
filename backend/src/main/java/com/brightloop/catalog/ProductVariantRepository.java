package com.brightloop.catalog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long>{java.util.List<ProductVariant> findByProductId(Long productId);}
