package com.ecommerce.identity.dto.request;

import com.ecommerce.identity.utility.enumUtils.AuthenticationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String id;
    String username;
    String password;
    Boolean status;
    String nickName;
    String email;
    Boolean gender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date dateOfBirth;
    AuthenticationType authType;
    List<RoleRequest> roles;
}
