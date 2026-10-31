package com.brightloop.catalog;
import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/products") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class ProductController {
 private final ProductService service;public ProductController(ProductService s){service=s;}
 @GetMapping public java.util.List<CatalogDtos.ProductResponse> list(){return service.list();}
 @GetMapping("/{id}") public CatalogDtos.ProductResponse get(@PathVariable Long id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public CatalogDtos.ProductResponse create(@Valid @RequestBody CatalogDtos.ProductRequest r){return service.create(r);}
 @PutMapping("/{id}") public CatalogDtos.ProductResponse update(@PathVariable Long id,@Valid @RequestBody CatalogDtos.ProductRequest r){return service.update(id,r);}
 @PostMapping("/{id}/variants") @ResponseStatus(HttpStatus.CREATED) public CatalogDtos.VariantResponse addVariant(@PathVariable Long id,@Valid @RequestBody CatalogDtos.VariantRequest r){return service.addVariant(id,r);}
 @PutMapping("/{id}/variants/{variantId}") public CatalogDtos.VariantResponse updateVariant(@PathVariable Long id,@PathVariable Long variantId,@Valid @RequestBody CatalogDtos.VariantRequest r){return service.updateVariant(id,variantId,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archive(@PathVariable Long id){service.archive(id);}
}
