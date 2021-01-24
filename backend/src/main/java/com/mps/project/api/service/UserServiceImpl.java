package com.mps.project.api.service;

import com.mps.project.api.exceptions.ConflictException;
import com.mps.project.api.exceptions.PasswordRegisterException;
import com.mps.project.api.exceptions.ResourceNotFoundException;
import com.mps.project.api.model.AdminContext;
import com.mps.project.api.model.Organization;
import com.mps.project.api.model.Role;
import com.mps.project.api.model.User;
import com.mps.project.api.repository.OrganizationRepository;
import com.mps.project.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public void save(final User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // used only for login
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username " + username + " could not be found");
        }

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), new ArrayList<>());
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByOrganizationId(Long organizationId) {
        return userRepository.findByOrganization_Id(organizationId);
    }

    @Override
    public User registerUser(User user, String role) {
        // we save the password because save method encodes the password
        String passwordConfirm = user.getPasswordConfirm();

        if (!user.getPassword().equals(passwordConfirm)) {
            throw new PasswordRegisterException("Password confirm is not correct");
        }

        if (findByUsername(user.getUsername()).isPresent()) {
            throw new ConflictException("Username is already taken");
        }
        Optional<Organization> orgFromDb = organizationRepository.findByName(user.getOrganization().getName());
        if (orgFromDb.isEmpty()) {
            throw new ResourceNotFoundException("Organization with name " + user.getOrganization().getName() + " does not exist");
        }

        user.setOrganization(orgFromDb.get());
        user.setRole(role);

        save(user);

        User userToClient = new User();
        userToClient.setRole(role);
        userToClient.setOrganization(orgFromDb.get());
        userToClient.setUsername(user.getUsername());
        userToClient.setFirstName(user.getFirstName());
        userToClient.setLastName(user.getLastName());

        return userToClient;
    }

    @Override
    @Transactional
    public AdminContext createAdminAndOrganization(AdminContext adminContext) {
        Organization organizationDb = organizationRepository.save(adminContext.getOrganization());
        User userToClient = registerUser(adminContext.getUser(), Role.ADMIN.name());
        AdminContext updatedAdminContext = new AdminContext();
        updatedAdminContext.setUser(userToClient);
        updatedAdminContext.setOrganization(organizationDb);

        return updatedAdminContext;
    }
}
