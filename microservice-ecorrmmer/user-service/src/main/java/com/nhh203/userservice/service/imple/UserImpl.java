package com.nhh203.userservice.service.imple;


import com.google.gson.Gson;
import com.nhh203.userservice.constant.KafkaConstant;
import com.nhh203.userservice.event.EventProducer;
import com.nhh203.userservice.exception.wrapper.*;
import com.nhh203.userservice.model.dto.request.*;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserImpl implements UserService {

    UserRepository userRepository;
    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserDetailService userDetailsService;
    JwtProvider jwtProvider;
    EventProducer eventProducer;
    Gson gson;


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
            String currentToken = getCurrentToken();
            SecurityContextHolder.getContext().setAuthentication(null);
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
    public Mono<User> update(Long id, SignUp updateDTO) {
        try {
            User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found userId: " + id + " for update"));
            modelMapper.map(updateDTO, existingUser);
            existingUser.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
            return Mono.just(userRepository.save(existingUser));
        } catch (Exception exception) {
            return Mono.error(exception);
        }
    }

    @Override
    public Mono<String> changePassword(ChangePasswordRequest request) {
        try {
            UserDetails userDetails = getCurrentUserDetails();
            String username = userDetails.getUsername();
            User existingUser = findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));
            if (passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
                if (validateNewPassword(request.getNewPassword(), request.getConfirmPassword())) {
                    existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    userRepository.save(existingUser);

                    // send email through kafka client
                    EmailDetails emailDetails = emailDetailsConfig(username);
                    eventProducer.send(KafkaConstant.PROFILE_ONBOARDING_TOPIC, gson.toJson(emailDetails));

                }
                return Mono.just("Password changed failed.");
            } else {
                return Mono.error(new PasswordNotFoundException("Incorrect password"));
            }
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return Mono.error(new UserNotAuthenticatedException("Transaction silently rolled back"));
        }
    }

    private EmailDetails emailDetailsConfig(String username) {
        return EmailDetails.builder()
                .recipient("nguyenhuyhoa2003@gmail.com")
                .msgBody(textSendEmailChangePasswordSuccessfully(username))
                .subject("Password Change Successful: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .attachment("Please be careful, don't let this information leak")
                .build();
    }

    public String textSendEmailChangePasswordSuccessfully(String username) {
        return "Hey " + username + "!\n\n" +
                "This is a confirmation that your password has been successfully changed.\n" +
                " If you did not initiate this change, please contact our support team immediately.\n" +
                "If you have any questions or concerns, feel free to reach out to us.\n\n" +
                "Best regards:\n\n" +
                "Contact: nguyenhuyhoa2003@gmail.com\n" +
                "Fanpage: https://github.com/nguyenhuyhoa11102003/";
    }


    private boolean validateNewPassword(String newPassword, String confirmPassword) {
        return Objects.equals(newPassword, confirmPassword);
    }

    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        } else {
            throw new UserNotAuthenticatedException("User not authenticated.");
        }
    }


    @Override
    public String delete(Long id) {
        userRepository.findById(id)
                .ifPresentOrElse(
                        user -> {
                            try {
                                userRepository.delete(user);
                            } catch (DataAccessException e) {
                                throw new RuntimeException("Error deleting user with userId: " + id, e);
                            }
                        },
                        () -> {
                            throw new UserNotFoundException("User not found for userId: " + id);
                        }
                );
        return "User with id " + id + " deleted successfully.";
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.of(userRepository
                        .findById(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));
    }

    @Override
    public Optional<User> findByUsername(String userName) {
        return Optional.ofNullable(userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found with userName: " + userName)));
    }

    @Override
    public Page<UserDto> findAllUsers(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageRequest);
        return usersPage.map(user -> modelMapper.map(user, UserDto.class));
    }
}
