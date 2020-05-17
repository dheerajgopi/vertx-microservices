package org.example.account.user;

import com.querydsl.core.types.Predicate;
import org.example.account.entity.QUser;
import org.example.account.entity.User;
import org.example.account.repository.UserCrudRepository;
import org.example.microservicecommon.exception.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Core business logic for user operations.
 */
@Service("userService")
public class UserService {

    /**
     * Repository for user.
     */
    @Autowired
    private UserCrudRepository userRepository;

    /**
     * Fetch paginated list of users.
     * @param filter filters to be applied
     * @param pageable pagination info
     * @return paginated list of users
     */
    @Transactional
    public Page<User> fetchAll(final Predicate filter, final Pageable pageable) {
        return userRepository.findAll(filter, pageable);
    }

    /**
     * Persists an user to the DB.
     * @param user user entity to be persisted
     * @return user entity
     * @throws {@link ConflictException} if username already exists
     */
    @Transactional
    public User createUser(final User user) {
        final Optional<User> duplicateUser = userRepository
                .findOne(QUser.user.username.equalsIgnoreCase(user.getUsername()));

        if (duplicateUser.isPresent()) {
            throw new ConflictException("username", "username already exists");
        }

        return userRepository.save(user);
    }

}
