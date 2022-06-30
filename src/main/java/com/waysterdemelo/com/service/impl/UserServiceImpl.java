package com.waysterdemelo.com.service.impl;

import com.waysterdemelo.com.constents.CafeConstants;
import com.waysterdemelo.com.dao.UserRepository;
import com.waysterdemelo.com.pojo.User;
import com.waysterdemelo.com.service.UserService;
import com.waysterdemelo.com.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Autowired
    UserRepository userRepository;


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside Signup {}", requestMap);
      try {
          if (validateSignUpMap(requestMap)){
              User user =userRepository.findByEmailId(requestMap.get("email"));

              if (Objects.isNull(user)){

                  userRepository.save(getUserFromMap(requestMap));
                  return CafeUtils.getResponseEntity("Succesfully registered", HttpStatus.OK);

              }else{
                  return CafeUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
              }

          }else{
              return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
          }
      }catch (Exception e ){
          e.printStackTrace();
      }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap){
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
                && requestMap.containsKey("password")){
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setRole("user");
        user.setStatus("false");
        return user;
    }
}
