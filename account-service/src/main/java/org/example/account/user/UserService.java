package org.example.account.user;

import com.querydsl.core.types.Predicate;
import org.example.account.entity.User;
import org.example.account.repository.UserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("userService")
public class UserService {

    @Autowired
    private UserCrudRepository userRepository;

    @Transactional
    public List<User> fetchAll(final Predicate filter) {
        final List<User> userList = new ArrayList<>();
        userRepository.findAll(filter).forEach(user -> userList.add(user));

        return userList;
    }

}
