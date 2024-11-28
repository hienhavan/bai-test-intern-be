package org.example.testapi.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public ProductController(ProductService productService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("api/admin/v1/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping("api/admin/v1/product")
    public ResponseEntity<?> addProduct(@RequestBody AddProductResponse product) {
        productService.addProduct(product);
        kafkaTemplate.send("ok", product);
        return ResponseEntity.status(HttpStatus.CREATED).body("thêm thành công" + product);
    }

    @PutMapping("api/admin/v1/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        if (updatedProduct == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thất bại");
        }
        return ResponseEntity.ok("cập nhật thành công");
    }

    @DeleteMapping("api/admin/v1/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        boolean isDeleted = productService.deleteProduct(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thất bại");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("api/admin/v1/product/search")
    public ResponseEntity<?> findFriendsByName(@RequestParam(name = "name", required = false) String name) {
        try {
            List<FindProductResponse> findProductByName = productService.findByName(name);
            if (findProductByName.isEmpty()) {
                return ResponseEntity.ok("Không tìm thấy người dùng.");
            }
            return ResponseEntity.ok(findProductByName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Tìm kiếm bạn bè thất bại.");
        }
    }
    @KafkaListener(topics = "ok", groupId = "group-1")
    public void listen(Object message) {
        System.out.println("Received message: " + message);
    }
}
