package org.example.account.user.dataobject.dto;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.example.account.entity.User;
import org.example.microservicecommon.http.JsonData;

/**
 * Container for user payload in REST API response.
 */
@DataObject(generateConverter = true)
public class UserDto implements JsonData {

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

    public UserDto(final JsonObject json) {
        UserDtoConverter.fromJson(json, this);
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
        final JsonObject json = new JsonObject();
        UserDtoConverter.toJson(this, json);

        return json;
    }
}
