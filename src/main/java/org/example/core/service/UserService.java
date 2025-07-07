package org.example.core.service;

import org.example.core.dto.CreateProfessionalRequest;
import org.example.core.dto.CreateUserRequest;
import org.example.core.dto.UpdateUserProfileRequest;
import org.example.core.dto.UserResponse;
import org.example.core.dto.reserve.ProfessionalProfileDto;
import org.example.core.model.Professional;
import org.example.core.model.Role;
import org.example.core.model.User;
import org.example.core.model.enums.UserType;
import org.example.core.repository.PermissionRepository;
import org.example.core.repository.RoleRepository;
import org.example.core.repository.UserRepository;
import org.example.core.repository.reserve.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository; // Still needed for createUser if it involves fetching permissions directly
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfessionalRepository professionalRepository;

    /**
     * Creates a default admin user if no users exist in the database.
     * This method assumes that roles (including 'ROLE_ADMIN') have already been created
     * by the DataInitializer.
     */
    public void createDefaultAdminIfNotExists() {
        if (userRepository.count() == 0) { //
            System.out.println("✅ Creating default admin user...");

            // Find the 'ROLE_ADMIN' role, which is expected to be created by DataInitializer
            Optional<Role> adminRoleOpt = roleRepository.findByName("ROLE_ADMIN");
            if (!adminRoleOpt.isPresent()) {
                System.err.println("❌ Setup Error: 'ROLE_ADMIN' role not found. Cannot create default admin user.");
                throw new IllegalStateException("ROLE_ADMIN not found during default admin creation. Ensure DataInitializer runs first.");
            }
            Role adminRole = adminRoleOpt.get();

            // Create the admin user
            User admin = new User();
            admin.setUsername("admin"); //
            admin.setPassword(passwordEncoder.encode("admin")); // Hash the password
            admin.setEnabled(true); //
            admin.setRoles(Collections.singleton(adminRole)); // Assign the admin role
            admin.setUserType(UserType.ADMIN);
            userRepository.save(admin); //

            System.out.println("✅ Default admin user created: admin / admin");
        }
    }

    /**
     * Creates a new user based on the provided request.
     *
     * @param request CreateUserRequest object containing username, password, and role names.
     */
    public void createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username " + request.getUsername() + " already exists.");
        }

        // ۱. ساختن و مقداردهی آبجکت User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEnabled(true);
        user.setUserType(request.getUserType());

        Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(request.getRoles()));
        user.setRoles(roles);

        // ۲. ذخیره کردن کاربر در دیتابیس
        User savedUser = userRepository.save(user);

        // ۳. **بخش کلیدی:** اگر کاربر یک متخصص بود، پروفایل او را نیز ایجاد می‌کنیم
        if (savedUser.getUserType() == UserType.PROFESSIONAL) {
            Professional professional = new Professional();
            professional.setUser(savedUser); // برقراری رابطه

            if (request instanceof CreateProfessionalRequest profRequest) {
                professional.setSpecialty(profRequest.getSpecialty());
                professional.setBio(profRequest.getBio());
            }

            professionalRepository.save(professional);
        }
    }
    /**
     * Retrieves a list of all users with their role names.
     *
     * @return List of UserResponse objects.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> { //
            List<String> roleNames = user.getRoles().stream() //
                    .map(Role::getName).collect(Collectors.toList()); //
            return new UserResponse(user.getId(), user.getUsername(), roleNames); //
        }).collect(Collectors.toList());
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with ID " + id + " not found.");
        }
        userRepository.deleteById(id); //
    }

    /**
     * Retrieves the names of roles assigned to a specific user.
     * Used by UserController to display roles for a selected user in the frontend.
     *
     * @param userId The ID of the user.
     * @return List of role names.
     * @throws NoSuchElementException if the user is not found.
     */
    public List<String> getUserRoleNames(Long userId) {
        User user = userRepository.findById(userId) //
                .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found."));
        return user.getRoles().stream() //
                .map(Role::getName) //
                .collect(Collectors.toList());
    }

    /**
     * Adds a specific role to a user.
     * Used by UserController to assign roles to users in the frontend.
     *
     * @param userId The ID of the user.
     * @param roleName The name of the role to add.
     * @throws NoSuchElementException if the user or role is not found.
     * @throws IllegalArgumentException if the role is already assigned to the user.
     */
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId) //
                .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found."));
        Role roleToAdd = roleRepository.findByName(roleName) //
                .orElseThrow(() -> new NoSuchElementException("Role with name " + roleName + " not found."));

        if (user.getRoles().contains(roleToAdd)) { //
            throw new IllegalArgumentException("Role '" + roleName + "' is already assigned to user '" + user.getUsername() + "'.");
        }
        user.getRoles().add(roleToAdd); //
        userRepository.save(user); //
    }

    // Optional: Method to remove a role from a user, if needed
     public void removeRoleFromUser(Long userId, String roleName) {
         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found."));
         Role roleToRemove = roleRepository.findByName(roleName)
                 .orElseThrow(() -> new NoSuchElementException("Role with name " + roleName + " not found."));

         if (!user.getRoles().contains(roleToRemove)) {
             throw new IllegalArgumentException("Role '" + roleName + "' is not assigned to user '" + user.getUsername() + "'.");
         }
         user.getRoles().remove(roleToRemove);
         userRepository.save(user);
     }

    // Optional: Method to update (replace) all roles for a user, if needed
     public void updateUserRoles(Long userId, List<String> newRoleNames) {
         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found."));

         Set<Role> rolesToSet = newRoleNames.stream()
                 .map(roleName -> roleRepository.findByName(roleName)
                         .orElseThrow(() -> new IllegalArgumentException("Role with name " + roleName + " not found.")))
                 .collect(Collectors.toSet());
         user.setRoles(rolesToSet);
         userRepository.save(user);
     }


    public UserResponse getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));

        // در اینجا User entity را به UserResponse DTO تبدیل می‌کنید
        UserResponse response = new UserResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setUsername(user.getUsername());


        return response;
    }

    public void updateUserProfile(String username, UpdateUserProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);
    }

}