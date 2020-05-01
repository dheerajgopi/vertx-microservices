package org.example.account.repository;

import org.example.account.entity.User;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for user.
 */
@Repository
public interface UserCrudRepository extends CrudRepository<User, Long>, QuerydslPredicateExecutor<User> {
}
