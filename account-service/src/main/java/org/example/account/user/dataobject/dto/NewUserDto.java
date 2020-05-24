package org.example.account.user.dataobject.dto;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.example.microservicecommon.http.JsonData;

/**
 * User details provided in create user request body.
 */
@DataObject(generateConverter = true)
public class NewUserDto implements JsonData {

    /**
     * user name.
     */
    private String username;

    /**
     * Name.
     */
    private String name;

    /**
     * password.
     */
    private String password;

    public NewUserDto(final JsonObject json) {
        NewUserDtoConverter.fromJson(json, this);
    }

    @Override
    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        NewUserDtoConverter.toJson(this, json);

        return json;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

}
