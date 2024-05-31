package com.nhh203.userservice.service;

import com.nhh203.userservice.model.entity.User;

public interface UserService {
//    public User addUser(UserDTO user);

//    public UserReponseMessage getByid(Long id);

    public Boolean checkUserExist(String email, String phone);

    void updateUser(Long id, String phone, String address);

    Boolean updatePassword(Long id, String passwordnew, String passwordOld);
}
