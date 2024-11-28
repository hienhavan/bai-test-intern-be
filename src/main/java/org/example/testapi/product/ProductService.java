package org.example.testapi.product;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ProductService(ProductRepository productRepository, RedisTemplate<String, Object> redisTemplate) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    public  List<Product> getAllProducts() {
        if (redisTemplate.hasKey("products")) {
            return (List<Product>) redisTemplate.opsForValue().get("products");
        } else {
            List<Product> products = productRepository.findAll();
            redisTemplate.opsForValue().set("products", products);
            return products;
        }
    }

    public void addProduct(AddProductResponse request) {
        var product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        productRepository.save(product);
    }

    public Product updateProduct(Integer id, Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }

    private FindProductResponse convertToFindUserRequest(Product product) {
        return FindProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public List<FindProductResponse> findByName(String name) throws Exception {
        if (name == null || name.isEmpty()) {
            List<Product> products = productRepository.findAll();
            return products.stream()
                    .map(this::convertToFindUserRequest)
                    .collect(Collectors.toList());
        } else {
            List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
            if (products == null || products.isEmpty()) {
                throw new Exception("User not found");
            }
            return products.stream()
                    .map(this::convertToFindUserRequest)
                    .collect(Collectors.toList());
        }
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
