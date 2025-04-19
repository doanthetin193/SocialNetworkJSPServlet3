package org.example.DAO;

import java.util.List;

import org.example.model.CustomUser;
import org.example.model.User;

public interface UserDAO {
    User findByUsernameAndPassword(String username, String password);
    void save(User user);
    User findById(Long id);
    List<User> findAll();
    User findByUsername(String username);
    boolean isEmailExists(String email);
    void update(CustomUser user);
}
