package com.example.service;
import org.modelmapper.ModelMapper;
import com.example.JWTSecurity.JwtUtil;
import com.example.aggregate.Users;
import com.example.authentication.Request;
import com.example.authentication.Response;
import com.example.entity.TokenEntity;
import com.example.entity.UserEntity;
import com.example.exceptions.ExceptionNotFound;
import com.example.jpa.TaskRepository;
import com.example.jpa.TokenRepository;
import com.example.jpa.UserRepository;
import com.example.security.IUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;



@Service
@Component
public class UserService implements IUserService {
    private final IUserDetailsService userDetailsService;
    private final JwtUtil jwtTokenUtil;
    @Autowired
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;


    public final Logger LOGGER = LoggerFactory.getLogger(TaskService.class.getName());

    @Autowired
    private UserRepository UserRepo;

    public UserService(IUserDetailsService userDetailsService, JwtUtil jwtTokenUtil, TaskRepository taskRepository, UserRepository userRepository, TokenRepository tokenRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserRepository userRepo) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        UserRepo = userRepo;
    }


    @Override
    public Users createUser(Users User) {
        LOGGER.trace("creating user");
        UserEntity userEntity = convertToEntity(User);
        return convertToModel(UserRepo.save(userEntity));
    }

    //retrieve the user ID of the currently logged-in user. It takes a single parameter
    @Override
    public int getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(); // retrieve the current session,
        Object userId = session.getAttribute("userId"); //to retrieve the "userId" attribute from the session
        if (userId == null) {
            throw new ExceptionNotFound("id not found");
        }
        return (int) userId;
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        LOGGER.trace("deleting user");
        if (UserRepo.findById(id).isEmpty()) {
            throw new ExceptionNotFound("This user cant be deleted because id doesnt exist");
        }
        UserRepo.deleteById(id);
        LOGGER.warn("id doesnt exist");
    }


    @Override
    @Transactional
    public Users updateUser(Users user) {
        LOGGER.trace("updating user");
        UserEntity userEntity = convertToEntity(user);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userEntity.setUsername(userEntity.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return convertToModel(userRepository.save(userEntity));
    }


    @Override
    @Transactional
    public Users getById(int id) {
        LOGGER.trace("getting user by id");
        if (UserRepo.findById(id).isEmpty()) {

            throw new ExceptionNotFound("This id doesn't exist");
        }
        return convertToModel(UserRepo.findById(id).get());
    }


    public Response createAuthenticationToken(Request authenticationRequest) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {

            throw new BadCredentialsException("The username or password are incorrect", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        TokenEntity token = new TokenEntity();
        UserEntity user = (UserEntity) userDetails;
        token.setUser(user);
        token.setJwtToken(jwt);
        tokenRepository.save(token); //Saves the Tokens object in the database
        jwtTokenUtil.addToken(token);
        user.addToken(token);
        userRepository.save(user); //Adds the Tokens object to the list of tokens for the user and saves the user in the database using the save method of the userRepository.
        return new Response(jwt);
    } //Returns a Response object containing the JWT.

    private UserEntity convertToEntity(Users user) {

        return modelMapper.map(user, UserEntity.class);
    }

    private Users convertToModel(UserEntity userEntity) {

        return modelMapper.map(userEntity, Users.class);
    }
}



