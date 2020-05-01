package org.example.account.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.example.microservicecommon.http.AbstractDataObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * User entity.
 */
@DataObject(generateConverter = true)
@Entity
@Table(name = "users")
public class User extends AbstractDataObject {

    /**
     * Identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * user name.
     */
    private String username;

    /**
     * name of the user.
     */
    private String name;

    /**
     * password.
     */
    private String password;

    /**
     * status of user.
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * when user is created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * last updated time of user.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(final JsonObject json) {
        UserConverter.fromJson(json, this);
    }

    @Override
    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        UserConverter.toJson(this, json);

        return json;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
