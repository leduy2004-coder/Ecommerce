package com.ecommerce.identity.entity;

import com.ecommerce.identity.utility.enumUtils.AuthenticationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class UserEntity extends BaseEntity {

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "status")
    Boolean status;

    @Enumerated(EnumType.STRING)
    AuthenticationType authType;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    List<RoleEntity> roles;


}
