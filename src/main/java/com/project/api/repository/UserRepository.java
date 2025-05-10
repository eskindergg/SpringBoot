package com.project.api.repository;
import com.project.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Procedure(name = "getUsers")
    List<User> getUsers();

    @Procedure(name = "users_bulk_upsert")
    List<User> users_bulk_upsert(@Param("json_users") String usersJson);
}
