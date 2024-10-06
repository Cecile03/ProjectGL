package com.example.backend.dao;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByTeams_Id(int teamId);
    Optional<List<User>> findByRoles(Role role);
    List<User> findByRolesIn(List<Role> roles);
    void deleteByRoles(Role role);
}
