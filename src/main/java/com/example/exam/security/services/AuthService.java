package com.example.exam.security.services;

import com.example.exam.payload.request.LoginRequest;
import com.example.exam.payload.request.SignUpRequest;
import com.example.exam.payload.response.JwtResponse;
import com.example.exam.payload.response.MessageResponse;
import com.example.exam.repository.PermissionRepository;
import com.example.exam.repository.RoleRepository;
import com.example.exam.repository.UserRepository;
import com.example.exam.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.exam.exceptions.PermissionNotFoundException;
import com.example.exam.exceptions.RoleNotFoundException;
import com.example.exam.model.security.permission.EPermission;
import com.example.exam.model.security.permission.Permission;
import com.example.exam.model.security.role.ERole;
import com.example.exam.model.security.role.Role;
import com.example.exam.model.security.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }


    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        userDetails.getAuthorities().forEach(authority -> {
            if (authority.getAuthority().startsWith("ROLE_")) {
                roles.add(authority.getAuthority());
            } else {
                permissions.add(authority.getAuthority());
            }
        });

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles, permissions);
    }

    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }


        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(RoleNotFoundException::new);
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(RoleNotFoundException::new);
                        roles.add(adminRole);
                        break;
                    case "importer":
                        Role importerRole = roleRepository.findByName(ERole.ROLE_IMPORTER)
                                .orElseThrow(RoleNotFoundException::new);
                        roles.add(importerRole);
                        break;
                    case "student_admin":
                        Role studentAdminRole = roleRepository.findByName(ERole.ROLE_STUDENT_ADMIN)
                                .orElseThrow();
                        roles.add(studentAdminRole);
                        break;
                    case "pensioner_admin":
                        Role pensionerAdminRole = roleRepository.findByName(ERole.ROLE_PENSIONER_ADMIN)
                                .orElseThrow();
                        roles.add(pensionerAdminRole);
                        break;
                    case "employee_admin":
                        Role employeeAdminRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE_ADMIN)
                                .orElseThrow();
                        roles.add(employeeAdminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RoleNotFoundException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        Set<Permission> permissions = getPermissions(signUpRequest);

        user.setPermissions(permissions);
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private Set<Permission> getPermissions(SignUpRequest signUpRequest) {
        Set<String> strPermissions = signUpRequest.getPermissions();
        Set<Permission> permissions = new HashSet<>();

        if (strPermissions != null) {
            strPermissions.forEach(permissionName -> {
                Permission permission = permissionRepository.findByName(EPermission.valueOf(permissionName))
                        .orElseThrow(() -> new PermissionNotFoundException("Error: Permission is not found."));
                permissions.add(permission);
            });
        }
        return permissions;
    }

}
