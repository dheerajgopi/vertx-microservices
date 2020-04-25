package org.example.account.user.dto;

import io.vertx.core.json.JsonObject;
import org.example.account.entity.User;
import org.example.microservicecommon.http.JsonResponse;

/**
 * Container for user payload in REST API response.
 */
public class UserDto implements JsonResponse {

    private Long id;

    private String username;

    private String name;

    private Boolean isActive;

    public UserDto(final User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.isActive = user.getIsActive();
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
