package ru.organizilla.auth.controller;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.organizilla.auth.dto.*;
import ru.organizilla.auth.exception.AccountConfirmationException;
import ru.organizilla.auth.exception.AccountStatusException;
import ru.organizilla.auth.service.AuthService;
import ru.organizilla.auth.util.CookieUtil;

import javax.management.openmbean.KeyAlreadyExistsException;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login/username")
    public ResponseEntity<AccessTokenDto> authenticateUsername(@RequestBody @Valid AuthUsernameDto authUsernameDto) {
        var tokens = authService.authenticateUsername(authUsernameDto);
        var accessToken = new AccessTokenDto();
        accessToken.setAccessToken(tokens.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(tokens.getRefreshToken()).toString());

        return ok().headers(headers).body(accessToken);
    }

    @PostMapping("/login/email")
    public ResponseEntity<AccessTokenDto> authenticateEmail(@RequestBody @Valid AuthEmailDto authEmailDto) {
        var tokens = authService.authenticateEmail(authEmailDto);
        var accessToken = new AccessTokenDto();
        accessToken.setAccessToken(tokens.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(tokens.getRefreshToken()).toString());

        return ok().headers(headers).body(accessToken);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterUserDto registerUserDto) {
        authService.registerUser(registerUserDto);
        return ok().body("Account registered. Needed email confirmation");
    }

    @PostMapping("/send-confirmation-email")
    public ResponseEntity<String> sendConfirmationEmail(@RequestBody @Valid SendEmailDto sendEmailDto) {
        authService.sendRegisterEmail(sendEmailDto);
        return ok().body("Email sent");
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<AccessTokenDto> confirmEmail(@RequestBody @Valid EmailConfirmationDto emailConfirmationDto) {
        var tokens = authService.confirmRegistration(emailConfirmationDto);
        var accessToken = new AccessTokenDto();
        accessToken.setAccessToken(tokens.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(tokens.getRefreshToken()).toString());

        return ok().headers(headers).body(accessToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenDto> refresh(@CookieValue("organizilla_refreshtoken")
                                                  String refreshToken) {

        var tokens = authService.refreshToken(refreshToken);
        var accessToken = new AccessTokenDto();
        accessToken.setAccessToken(tokens.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(tokens.getRefreshToken()).toString());

        return ok().headers(headers).body(accessToken);
    }

    @ExceptionHandler(KeyAlreadyExistsException.class)
    public ResponseEntity<String> handleKeyAlreadyExistsException(KeyAlreadyExistsException ex) {
        return badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return badRequest().body("Invalid credentials");
    }

    @ExceptionHandler(AccountConfirmationException.class)
    public ResponseEntity<String> handleAccountConfirmationException(AccountConfirmationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<String> handleAccountStatusException(AccountStatusException ex) {
        return badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        return badRequest().body("Invalid token");
    }
}
