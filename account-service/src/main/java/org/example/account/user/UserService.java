package org.example.account.user;

import com.querydsl.core.types.Predicate;
import org.example.account.entity.User;
import org.example.account.repository.UserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserService {

    @Autowired
    private UserCrudRepository userRepository;

    @Transactional
    public Page<User> fetchAll(final Predicate filter, final Pageable pageable) {
        return userRepository.findAll(filter, pageable);
    }

}
