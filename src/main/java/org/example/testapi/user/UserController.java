package org.example.testapi.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<?> register(@RequestBody AddUserRequest request) {
        try {
            userService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PutMapping("api/admin/v1/users/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Integer id) {
        userService.updateActive(id);
        return ResponseEntity.ok("Đã chặn người dùng.");
    }

    @GetMapping("/api/admin/v1/users")
    public ResponseEntity<?> findFriendsByName(@RequestParam(name = "name", required = false) String name) {
        try {
            List<FindUserResponse> findUsersByName = userService.findByName(name);
            if (findUsersByName.isEmpty()) {
                return ResponseEntity.ok("Không tìm thấy người dùng.");
            }
            return ResponseEntity.ok(findUsersByName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Tìm kiếm bạn bè thất bại.");
        }
    }
}
