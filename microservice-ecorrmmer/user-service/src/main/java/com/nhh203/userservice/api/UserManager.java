package com.nhh203.userservice.api;


import com.nhh203.userservice.http.HeaderGenerator;
import com.nhh203.userservice.model.dto.request.ChangePasswordRequest;
import com.nhh203.userservice.model.dto.request.SignUp;
import com.nhh203.userservice.model.dto.response.ResponseMessage;
import com.nhh203.userservice.security.jwt.JwtProvider;
import com.nhh203.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class UserManager {
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final HeaderGenerator headerGenerator;
    private final JwtProvider jwtProvider;


    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public ResponseEntity<ResponseMessage> update(@PathVariable("id") Long id, @RequestBody SignUp updateDTO) {
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
//        return userService.changePassword(request);
        return "ok";
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public String delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }





}
