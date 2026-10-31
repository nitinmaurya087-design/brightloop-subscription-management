package com.brightloop.catalog;
import java.util.List;import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.server.ResponseStatusException;
@Service public class ProductService {
 private final ProductRepository products;private final ProductVariantRepository variants;public ProductService(ProductRepository p,ProductVariantRepository v){products=p;variants=v;}
 @Transactional(readOnly=true) public List<CatalogDtos.ProductResponse> list(){return products.findAll().stream().map(p->CatalogDtos.ProductResponse.of(p,variants.findByProductId(p.getId()))).toList();}
 @Transactional(readOnly=true) public CatalogDtos.ProductResponse get(Long id){var p=find(id);return CatalogDtos.ProductResponse.of(p,variants.findByProductId(id));}
 @Transactional public CatalogDtos.ProductResponse create(CatalogDtos.ProductRequest r){var p=products.save(new Product(r.name().trim(),r.productType(),r.description(),r.salesPrice(),r.costPrice(),r.notes(),r.status()==null?"ACTIVE":r.status()));return CatalogDtos.ProductResponse.of(p,List.of());}
 @Transactional public CatalogDtos.ProductResponse update(Long id,CatalogDtos.ProductRequest r){var p=find(id);p.update(r.name().trim(),r.productType(),r.description(),r.salesPrice(),r.costPrice(),r.notes(),r.status()==null?"ACTIVE":r.status());return get(id);}
 @Transactional public CatalogDtos.VariantResponse addVariant(Long id,CatalogDtos.VariantRequest r){var v=variants.save(new ProductVariant(find(id),r.name(),r.attributeValue(),r.additionalPrice(),r.status()==null?"ACTIVE":r.status()));return CatalogDtos.VariantResponse.of(v);}
 @Transactional public CatalogDtos.VariantResponse updateVariant(Long id,Long variantId,CatalogDtos.VariantRequest r){var v=variants.findById(variantId).filter(x->x.getProduct().getId().equals(id)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Variant not found"));v.update(r.name(),r.attributeValue(),r.additionalPrice(),r.status()==null?"ACTIVE":r.status());return CatalogDtos.VariantResponse.of(v);}
 @Transactional public void archive(Long id){var p=find(id);p.update(p.getName(),p.getProductType(),p.getDescription(),p.getSalesPrice(),p.getCostPrice(),p.getNotes(),"ARCHIVED");}
 private Product find(Long id){return products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));}
}
