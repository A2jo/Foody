package com.my.foody.global.jwt;

import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;

@Getter
public class TokenSubject {
    private final Long id;
    private final UserType userType;

    @ConstructorProperties({"id", "userType"})
    public TokenSubject(Long id, UserType userType) {
        this.id = id;
        this.userType = userType;
    }

    public static TokenSubject of(User user){
        return new TokenSubject(user.getId(), UserType.USER);
    }
    public static TokenSubject of(Owner owner){
        return new TokenSubject(owner.getId(), UserType.OWNER);
    }
}
