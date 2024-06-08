package com.nhh203.userservice.service.imple;


import com.nhh203.userservice.exception.wrapper.EmailOrUsernameNotFoundException;
import com.nhh203.userservice.exception.wrapper.PasswordNotFoundException;
import com.nhh203.userservice.exception.wrapper.PhoneNumberNotFoundException;
import com.nhh203.userservice.exception.wrapper.UserNotFoundException;
import com.nhh203.userservice.model.dto.request.ChangePasswordRequest;
import com.nhh203.userservice.model.dto.request.Login;
import com.nhh203.userservice.model.dto.request.SignUp;
import com.nhh203.userservice.model.dto.request.UserDto;
import com.nhh203.userservice.model.dto.response.InformationMessage;
import com.nhh203.userservice.model.dto.response.JwtResponseMessage;
import com.nhh203.userservice.model.entity.RoleName;
import com.nhh203.userservice.model.entity.User;
import com.nhh203.userservice.repository.RoleRepository;
import com.nhh203.userservice.repository.UserRepository;
import com.nhh203.userservice.security.jwt.JwtProvider;
import com.nhh203.userservice.security.userprinciple.UserDetailService;
import com.nhh203.userservice.security.userprinciple.UserPrinciple;
import com.nhh203.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserDetailService userDetailsService;
    private final JwtProvider jwtProvider;

//    private final WebClient.Builder webClientBuilder;


    @Override
    public Mono<User> register(SignUp signUp) {
        return Mono.defer(() -> {
            if (userRepository.existsByUsername(signUp.getUsername())) {
                return Mono.error(new EmailOrUsernameNotFoundException("The username " + signUp.getUsername() + " is existed, please try again."));
            }
            if (userRepository.existsByEmail(signUp.getEmail())) {
                return Mono.error(new EmailOrUsernameNotFoundException("The email " + signUp.getEmail() + " is existed, please try again."));
            }
            if (userRepository.existsByPhoneNumber(signUp.getPhone())) {
                return Mono.error(new PhoneNumberNotFoundException("The phone number " + signUp.getPhone() + " is existed, please try again."));
            }

            User user = modelMapper.map(signUp, User.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(signUp.getRoles().stream().map(role -> this.roleRepository.findByName(mapToRoleName(role)).orElseThrow(() -> new RuntimeException("Role not found in the database."))).collect(Collectors.toSet()));

            userRepository.save(user);
            return Mono.just(user);
        });
    }

    private RoleName mapToRoleName(String roleName) {
        return switch (roleName) {
            case "ADMIN", "admin", "Admin" -> RoleName.ADMIN;
            case "USER", "user", "User" -> RoleName.USER;
            default -> null;
        };
    }

    @Override
    public Mono<JwtResponseMessage> login(Login signInForm) {

        return Mono.fromCallable(() -> {
            String usernameOrEmail = signInForm.getUsername();
            boolean isEmail = usernameOrEmail.contains("@gmail.com");


            UserDetails userDetails;
            if (isEmail) {
                userDetails = this.userDetailsService.loadUserByEmail(usernameOrEmail);
            } else {
                userDetails = this.userDetailsService.loadUserByUsername(usernameOrEmail);
            }

            // check username
            if (userDetails == null) {
                throw new UserNotFoundException("User not found");
            }

            // Check password
            if (!passwordEncoder.matches(signInForm.getPassword(), userDetails.getPassword())) {
                throw new PasswordNotFoundException("Incorrect password");
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, signInForm.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtProvider.createToken(authentication);
            String refreshToken = jwtProvider.createRefreshToken(authentication);

            UserPrinciple userPrinciple = (UserPrinciple) userDetails;

            return JwtResponseMessage.builder().accessToken(accessToken).refreshToken(refreshToken).information(InformationMessage.builder().id(userPrinciple.id()).fullname(userPrinciple.fullname()).username(userPrinciple.username()).email(userPrinciple.email()).phone(userPrinciple.phone()).gender(userPrinciple.gender()).avatar(userPrinciple.avatar()).roles(userPrinciple.roles()).build()).build();
        }).onErrorResume(Mono::error);


    }

    @Override
    public Mono<Void> logout() {
        return Mono.defer(() -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(null);
            String currentToken = getCurrentToken();
            if (authentication != null && authentication.isAuthenticated()) {
                String updatedToken = jwtProvider.reduceTokenExpiration(currentToken);
            }
            SecurityContextHolder.clearContext();
            return Mono.empty();
        });
    }

    private String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object credentials = authentication.getCredentials();
            if (credentials instanceof String) {
                return (String) credentials;
            }
        }
        return null;
    }

    @Override
    public Mono<User> update(Long userId, SignUp update) {
        return null;
    }

    @Override
    public Mono<String> changePassword(ChangePasswordRequest request) {
        return null;
    }

    @Override
    public String delete(Long id) {
        return null;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.of(userRepository.findById(userId)).orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));
    }

    @Override
    public Optional<User> findByUsername(String userName) {
        return Optional.ofNullable(userRepository.findByUsername(userName).orElseThrow(() -> new UserNotFoundException("User not found with userName: " + userName)));
    }

    @Override
    public Page<UserDto> findAllUsers(int page, int size, String sortBy, String sortOrder) {
        return null;
    }
}
