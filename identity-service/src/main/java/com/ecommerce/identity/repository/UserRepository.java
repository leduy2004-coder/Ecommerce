package com.ecommerce.identity.repository;

import com.ecommerce.identity.entity.UserEntity;
import com.ecommerce.identity.utility.enumUtils.AuthenticationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    @EntityGraph(attributePaths = {"musics", "avatars"})
    Optional<UserEntity> findById(String id);

    @EntityGraph(attributePaths = {"roles"})
    Optional<UserEntity> findByUsername(String username);

    @Query("UPDATE UserEntity u SET u.authType = ?2 WHERE u.username = ?1")
    void updateAuthenticationType(String username, AuthenticationType authType);

}
