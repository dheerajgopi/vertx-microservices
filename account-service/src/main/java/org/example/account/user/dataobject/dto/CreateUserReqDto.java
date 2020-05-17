package org.example.account.user.dataobject.dto;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.example.account.entity.User;
import org.example.microservicecommon.exception.MissingOrEmptyKeyException;
import org.example.microservicecommon.exception.ServiceException;
import org.example.microservicecommon.http.JsonData;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for top level key in create user request body.
 * <code>{"user": {...}}</code>
 */
@DataObject(generateConverter = true)
public class CreateUserReqDto implements JsonData {

    private NewUserDto user;

    public CreateUserReqDto(final JsonObject json) {
        if (json != null) {
            CreateUserReqDtoConverter.fromJson(json, this);
        }
    }

    @Override
    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        CreateUserReqDtoConverter.toJson(this, json);

        return json;
    }

    public NewUserDto getUser() {
        return user;
    }

    public void setUser(final NewUserDto user) {
        this.user = user;
    }

    /**
     * Validate the request body and return an user entity.
     * @return {@link User}
     * @throws ServiceException if validation fails
     */
    public User validateAndBuildEntity() throws ServiceException {
        if (this.user == null) {
            throw new MissingOrEmptyKeyException("user", "missing keys: ".concat("user"));
        }

        final NewUserDto newUserDto = this.user;
        final List<String> missingKeys = new ArrayList<>();

        if (StringUtils.isBlank(newUserDto.getUsername())) {
            missingKeys.add("username");
        }

        if (StringUtils.isBlank(newUserDto.getName())) {
            missingKeys.add("name");
        }

        if (StringUtils.isBlank(newUserDto.getPassword())) {
            missingKeys.add("password");
        }

        if (!missingKeys.isEmpty()) {
            throw new MissingOrEmptyKeyException(missingKeys, "missing keys: ".concat(String.join(", ", missingKeys)));
        }

        final User user = new User(newUserDto);

        return user;
    }

}
