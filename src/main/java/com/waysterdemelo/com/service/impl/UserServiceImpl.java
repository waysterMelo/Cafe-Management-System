package com.waysterdemelo.com.service.impl;

import com.waysterdemelo.com.constents.CafeConstants;
import com.waysterdemelo.com.dao.UserRepository;
import com.waysterdemelo.com.jwt.CustomerUserDetailsService;
import com.waysterdemelo.com.jwt.JwtFilter;
import com.waysterdemelo.com.jwt.JwtUtil;
import com.waysterdemelo.com.pojo.User;
import com.waysterdemelo.com.service.UserService;
import com.waysterdemelo.com.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> user) {
        log.info("Inside Signup {}", user);

       try {
           //se a validacao passar
           if (validateSignUpMap(user)){
               //pega o email
               User userFinded = userRepository.findByEmailId(user.get("email"));
               //verifica se e nulo
               if (Objects.isNull(userFinded)){
                   userRepository.save(getUserFromMap(user));

                   return CafeUtils.getResponseEntity("Succesfully registered", HttpStatus.CREATED);

               }else{
                   return CafeUtils.getResponseEntity("Email already exists ", HttpStatus.BAD_REQUEST);
               }
           }else {
               //se a validacao nao passar
               return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
           }
       }catch (Exception e){
            e.printStackTrace();
       }
       return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
                && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setRole("user");
        user.setStatus("false");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> user) {
        log.info("Inside Login");
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.get("email"), user.get("password")));

            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                            customerUserDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "wait for admin approval." + "\"}", HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception e) {
           log.error("{}", e);
        }
        return new ResponseEntity<String>("{\"message\":\"" + " bad credentials. " + "\"}", HttpStatus.BAD_REQUEST);
    }
}
