package org.example.account.service;

import org.example.account.entity.User;
import org.example.account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("userService")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<User> fetchAll() {
        final List<User> userList = new ArrayList<>();
        userRepository.findAll().forEach(user -> userList.add(user));

        return userList;
    }

}
