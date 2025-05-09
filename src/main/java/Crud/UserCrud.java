package Crud;

import Entities.User;
import Interfaces.IUserCrud;
import Services.UserService;
import java.util.ArrayList;
import java.util.List;

public class UserCrud implements IUserCrud {
    private UserService userService;
    
    public UserCrud() {
        this.userService = new UserService();
    }
    
    @Override
    public User add(User user) {
        return userService.createUser(
            user.getFullName(), 
            user.getEmail(), 
            user.getPhone(), 
            user.getRoles(), 
            user.getPassword()
        );
    }
    
    @Override
    public User getById(int id) {
        return userService.getUserById(id);
    }
    
    @Override
    public List<User> getAll() {
        return userService.getAllUsers();
    }
    
    @Override
    public User update(User user) {
        return userService.updateUser(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.getRoles(),
            user.getPassword()
        );
    }
    
    @Override
    public boolean delete(int id) {
        return userService.deleteUser(id);
    }
    
    @Override
    public User findByEmail(String email) {
        return userService.findUserByEmail(email);
    }
    
    @Override
    public List<User> findByRole(String role) {
        return userService.findUsersByRole(role);
    }
    
    @Override
    public User authenticate(String email, String password) {
        return userService.authenticateUser(email, password);
    }
} 