package Interfaces;

import Entities.User;
import java.util.List;

public interface IUserCrud extends ICrud<User> {
    User findByEmail(String email);
    List<User> findByRole(String role);
    User authenticate(String email, String password);
} 