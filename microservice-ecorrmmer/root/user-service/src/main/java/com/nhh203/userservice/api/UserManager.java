package com.nhh203.userservice.api;


import com.nhh203.userservice.exception.payload.TokenErrorOrAccessTimeOut;
import com.nhh203.userservice.exception.wrapper.UserNotFoundException;
import com.nhh203.userservice.http.HeaderGenerator;
import com.nhh203.userservice.model.dto.request.ChangePasswordRequest;
import com.nhh203.userservice.model.dto.request.SignUp;
import com.nhh203.userservice.model.dto.request.UserDto;
import com.nhh203.userservice.model.dto.response.ResponseMessage;
import com.nhh203.userservice.security.jwt.JwtProvider;
import com.nhh203.userservice.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users/manager")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserManager {
	ModelMapper modelMapper;
	UserService userService;
	HeaderGenerator headerGenerator;
	JwtProvider jwtProvider;

	@PutMapping("update/{id}")
	@PreAuthorize("isAuthenticated() and hasAuthority('USER')")
	public Mono<ResponseEntity<ResponseMessage>> update(@PathVariable("id") Long id, @RequestBody SignUp updateDTO) {
		return userService
				.update(id, updateDTO)
				.flatMap(user -> Mono.just(new ResponseEntity<>(new ResponseMessage("Update user: " + updateDTO.getUsername() + " successfully."), HttpStatus.OK)))
				.onErrorResume(error -> Mono.just(new ResponseEntity<>(new ResponseMessage("Update user: " + updateDTO.getUsername() + " failed " + error.getMessage()), HttpStatus.BAD_REQUEST)));
	}

	@PutMapping("/change-password")
	@PreAuthorize("isAuthenticated() and hasAuthority('USER')")
	public Mono<String> changePassword(@RequestBody ChangePasswordRequest request) {
		return userService.changePassword(request);
	}

	@DeleteMapping("delete/{id}")
	@PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
	public String delete(@PathVariable("id") Long id) {
		return userService.delete(id);
	}


	@GetMapping("/user")
	public ResponseEntity<?> getUserByUsername(@RequestParam(value = "username") String username) {
		Optional<UserDto> userDtoOptional = Optional.ofNullable(userService.findByUsername(username).map(user -> modelMapper.map(user, UserDto.class)).orElseThrow(() -> new UserNotFoundException("User not found with: " + username)));

		return userDtoOptional.map(u -> new ResponseEntity<>(u, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND));
	}

	@GetMapping("/user/{id}")
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and principal.id == #id")
	public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
		Optional<UserDto> userDTO = Optional.ofNullable(userService.findById(id).map((element) -> modelMapper.map(element, UserDto.class)).orElseThrow(() -> new UserNotFoundException("User not found with: " + id)));
		return (userDTO.isPresent()) ? new ResponseEntity<>(userDTO.get(), headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK) : new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
	}

	@GetMapping("/info")
	public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
		String token = "";
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			token = authHeader.replace("Bearer ", "");
		}
		String username = jwtProvider.getUserNameFromToken(token);
		UserDto user = userService.findByUsername(username).map((element) -> modelMapper.map(element, UserDto.class)).orElseThrow(() -> new TokenErrorOrAccessTimeOut("Token error or access timeout"));
		return (user != null) ? new ResponseEntity<>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK) : new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
	}
}
