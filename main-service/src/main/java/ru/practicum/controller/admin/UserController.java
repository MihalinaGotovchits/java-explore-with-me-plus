package ru.practicum.controller.admin;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) List<Long> userIds,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer size){
        log.info("/admin/users/GET/getUsers");
        List<UserDto> users= userService.getListUsers(userIds, from, size);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody NewUserRequest user){
        log.info("/admin/users/POST/registerUser - {}", user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    @DeleteMapping("{/id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("/admin/users/DELETE/delete - {}", id);
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
