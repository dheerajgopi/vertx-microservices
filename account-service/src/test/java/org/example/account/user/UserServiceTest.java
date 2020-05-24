package org.example.account.user;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.example.account.entity.QUser;
import org.example.account.entity.User;
import org.example.account.repository.UserCrudRepository;
import org.example.microservicecommon.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserCrudRepository userCrudRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFetchAll() {
        final Page<User> expected = new PageImpl<>(new ArrayList<>());
        Mockito.when(userCrudRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenReturn(expected);

        final Page<User> actual = userService.fetchAll(QUser.user.isActive.isTrue(), PageRequest.of(1, 1));

        assertEquals(expected, actual);
    }

    @Test
    void testCreateUserForConflict() {
        final User user = new User();
        user.setUsername("test");
        Mockito.when(userCrudRepository.findOne(Mockito.any(BooleanExpression.class)))
                .thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> {
            userService.createUser(user);
        });
    }

    @Test
    void testCreateUser() {
        final User user = new User();
        user.setUsername("test");
        Mockito.when(userCrudRepository.findOne(Mockito.any(BooleanExpression.class)))
                .thenReturn(Optional.empty());

        Mockito.when(userCrudRepository.save(user)).thenReturn(user);
        final User actual = userService.createUser(user);

        assertEquals(user, actual);
    }
}
