package com.nhh203.userservice.service.imple;


import com.nhh203.userservice.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserImpl implements UserService {
    @Override
    public Boolean checkUserExist(String email, String phone) {
        return null;
    }

    @Override
    public void updateUser(Long id, String phone, String address) {

    }

    @Override
    public Boolean updatePassword(Long id, String passwordnew, String passwordOld) {
        return null;
    }
}
