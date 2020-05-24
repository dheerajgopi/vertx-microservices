package org.example.account.user.dataobject.dto;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.example.microservicecommon.http.JsonData;

/**
 * Wrapper for top level key in create user response payload.
 * <code>{"user": {...}}</code>
 */
@DataObject(generateConverter = true)
public class CreateUserResDto implements JsonData {

    private UserDto user;

    public CreateUserResDto(final UserDto user) {
        this.user = user;
    }

    public CreateUserResDto(final JsonObject json) {
        CreateUserResDtoConverter.fromJson(json, this);
    }

    @Override
    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        CreateUserResDtoConverter.toJson(this, json);

        return json;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
