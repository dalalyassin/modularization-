package com.example;
import com.example.service.UserService;
import org.modelmapper.ModelMapper;
import com.example.JWTSecurity.JwtUtil;
import com.example.aggregate.Users;
import com.example.authentication.Request;
import com.example.authentication.Response;
import com.example.entity.UserEntity;
import com.example.jpa.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

@RestController

public class UserController {
@Autowired
    ModelMapper modelMapper;
    @Autowired
    private UserService userservice;
    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userservice) {

        this.userservice = userservice;
    }


    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        userservice.deleteUser(id);
        return "user has been deleted";
    }

    //signup into a user
    @PostMapping("/register")
    public Users createNewUser(@RequestBody Users newUser)  {
        userservice.updateUser(newUser);
        return newUser;
    }

    //handling the authentication process and returning a JWT as a Response object.
    @PostMapping("/login")
    public Response createAuthenticationToken(@RequestBody Request authenticationRequest)
            throws BadCredentialsException {
        return  userservice.createAuthenticationToken(authenticationRequest);
    }

    @PutMapping("/{id}")
    Users updateUser(@RequestBody Users User, @PathVariable int id, HttpServletRequest request) throws AccessDeniedException {
        // Get the user ID of the currently logged-in user
        int currentUserId = userservice.getCurrentUserId(request);
        // If the current user is not the user whose information is being updated, return an error
        if (currentUserId != id) {
            throw new AccessDeniedException("id not found");
        }
        // Otherwise, update the user information
        return userservice.updateUser(User);
    }
    @PostMapping()
    public Users createTask(@RequestBody Users User)
    {
        return userservice.createUser(User);
    }

    @GetMapping ("/{id}")
    public Users getById(@PathVariable int id) {
        return userservice.getById(id);
    }

    //handles the logout process for a user by
    // passing the extracted user ID. and delete all the
    // tokens related to the user with the given ID from the database.
    @PostMapping("/user/logout")
    public String logOutAll(@RequestHeader("Authorization") String token){
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Integer userId = jwtUtil.getUserIdFromToken(httpServletRequest);
        tokenRepository.deleteAllByUserId(userId);
        return "You're logged out from all devices";
    }
}
