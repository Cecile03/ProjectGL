package com.example.backend.service;

import com.example.backend.dao.RoleDao;
import com.example.backend.dao.TeamDao;
import com.example.backend.dao.UserDao;
import com.example.backend.dto.UserInteract;
import com.example.backend.dto.UserSendDTO;
import com.example.backend.model.Role;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service de gestion des utilisateurs.
 */
@Service
public class UserService implements UserDetailsService {

    private final NotificationService notificationService;
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final TeamDao teamDao;
    private final CleanupService cleanupService;
    private static final String ROLE_NOT_FOUND_MSG = "Role not found";

    /**
     * Constructeur de la classe UserService.
     *
     * @param userDao Le DAO des utilisateurs.
     * @param roleDao Le DAO des rôles.
     * @param notificationService Le service de gestion des notifications.
     * @param teamDao Le DAO des équipes.
     * @param cleanupService Le service de nettoyage.
     */
    @Autowired
    public UserService(UserDao userDao, RoleDao roleDao, NotificationService notificationService, TeamDao teamDao, CleanupService cleanupService){
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.notificationService = notificationService;
        this.teamDao = teamDao;
        this.cleanupService = cleanupService;
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return La liste des utilisateurs.
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * Récupère tous les enseignants.
     *
     * @return La liste des enseignants.
     */
    public List<User> getTeachers() {
        Role ssRole = roleDao.findByName(Role.RoleName.SS)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));

        return userDao.findByRoles(ssRole).orElseThrow();
    }

    /**
     * Récupère tous les enseignants avec des équipes.
     *
     * @return La liste des enseignants avec des équipes.
     */
    public List<User> getTeachersWithTeams() {
        List<Team> teams = teamDao.findAll();
        Set<User> teachers = new HashSet<>();
        for (Team team : teams) {
            teachers.add(team.getSupervisor());
        }
        return new ArrayList<>(teachers);
    }

    /**
     * Récupère tous les étudiants.
     *
     * @return La liste des étudiants.
     */
    public List<User> getStudents() {
        Role osRole = roleDao.findByName(Role.RoleName.OS)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));

        return userDao.findByRoles(osRole).orElseThrow();
    }

    /**
     * Récupère tous les membres du staff.
     *
     * @return La liste des membres du staff.
     */
    public List<User> getAllStaff() {
        Role ssRole = roleDao.findByName(Role.RoleName.SS)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));
        Role plRole = roleDao.findByName(Role.RoleName.PL)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));

        List<Role> roles = Arrays.asList(ssRole, plRole);
        List<User> users = userDao.findByRolesIn(roles);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found with roles SS or PL");
        }
        return users;
    }

    /**
     * Récupère tous les membres du staff avec des équipes.
     *
     * @return La liste des membres du staff avec des équipes.
     */
    public List<User> getTechnicalCoaches(){
        Role tcRole = roleDao.findByName(Role.RoleName.TC)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));

        return userDao.findByRoles(tcRole).orElseThrow();
    }

    /**
     * Récupère tous les chefs de projet.
     *
     * @return La liste des chefs de projet.
     */
    public List<User> getProjectLeader(){
        Role smRole = roleDao.findByName(Role.RoleName.PL)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));

        return userDao.findByRoles(smRole).orElseThrow();
    }

    /**
     * Récupère tous les chefs d'option.
     *
     * @return La liste des chefs d'option.
     */
    public List<User> getOptionLeader(){
        Role smRole = roleDao.findByName(Role.RoleName.OL)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));

        return userDao.findByRoles(smRole).orElseThrow();
    }

    /**
     * Récupère tous les enseignants.
     *
     * @return La liste des enseignants.
     */
    public List<User> getAllTeachers(){
        Role ssRole = roleDao.findByName(Role.RoleName.SS)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));
        Role tcRole = roleDao.findByName(Role.RoleName.TC)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));
        Role plRole = roleDao.findByName(Role.RoleName.PL)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));
        Role olRole = roleDao.findByName(Role.RoleName.OL)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));

        Set<User> teachers = new HashSet<>();
        teachers.addAll(userDao.findByRoles(ssRole).orElseThrow());
        teachers.addAll(userDao.findByRoles(tcRole).orElseThrow());
        teachers.addAll(userDao.findByRoles(plRole).orElseThrow());
        teachers.addAll(userDao.findByRoles(olRole).orElseThrow());

        return new ArrayList<>(teachers);
    }

    /**
     * Supprime tous les étudiants.
     */
    public void deleteStudents() {
        cleanupService.deleteAllStudents();
    }

    /**
     * Charge un utilisateur par son nom d'utilisateur.
     *
     * @param username the username identifying the user whose data is required.
     * @return Les details de l'utilisateur.
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByEmail(username).orElseThrow();
    }

    /**
     * Charge un utilisateur par son email.
     *
     * @param email L'email de l'utilisateur.
     * @return L'utilisateur.
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé.
     */
    public User loadUserByEmail(String email) throws UsernameNotFoundException {
        return userDao.findByEmail(email).orElseThrow();
    }

    /**
     * Enregistre un utilisateur.
     *
     * @param user L'utilisateur à enregistrer.
     * @return L'utilisateur enregistré.
     * @throws IllegalArgumentException si l'email est déjà utilisé.
     */
    public User saveUser(User user) throws IllegalArgumentException {
        Optional<User> existingUser = userDao.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            // Lancer une exception ou gérer cette situation comme vous le souhaitez
            throw new IllegalArgumentException("Email is already in use");
        } else {
            return userDao.save(user);
        }
    }

    /**
     * Met à jour un utilisateur.
     *
     * @param user L'utilisateur à mettre à jour.
     * @return L'utilisateur mis à jour.
     */
    public User updateUser(User user) {
        return userDao.save(user);
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id L'identifiant de l'utilisateur.
     */
    public void deleteUser(int id) {
        // Supprimer toutes les notifications de l'utilisateur
        notificationService.deleteAllNotificationsOfUser(id);
        // Supprimer les références à l'utilisateur dans la table userteam
        userDao.findById(id).ifPresent(user -> user.getTeams().clear());
        // Supprimer l'utilisateur lui-même
        userDao.deleteById(id);
    }

    /**
     * Vérifie si un utilisateur existe par son email.
     *
     * @param email L'email de l'utilisateur.
     * @return true si l'utilisateur existe, false sinon.
     */
    public boolean existsByEmail(String email) {
        Optional<User> user = userDao.findByEmail(email);
        return user.isPresent();
    }

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return L'utilisateur.
     */
    public User getUserById(int id) {
        return userDao.findById(id).orElseThrow();
    }

    /**
     * Convertit un UserInteract en User.
     *
     * @param userDto Le UserInteract à convertir.
     * @return L'utilisateur converti.
     */
    public Optional<User> userInteractToUser(UserInteract userDto) {
        if(userDao.findByEmail(userDto.getEmail()).isEmpty()){
            User user = new User();
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setGender(userDto.getGender());
            user.setOption(userDto.getOption());
            user.setBachelor(userDto.isBachelor());
            user.setGradePast(userDto.getGradePast());
            return Optional.of(user);
        }
        return userDao.findByEmail(userDto.getEmail());
    }

    /**
     * Cherche les utilisateurs par son équipe.
     * @param teamId L'identifiant de l'équipe.
     * @return La liste des utilisateurs de l'équipe.
     */
    public List<User> findByTeam(int teamId) {
        return userDao.findByTeams_Id(teamId);
    }

    /**
     * Met le rôle par défaut à un utilisateur.
     * @param user L'utilisateur.
     */
    public void setDefaultRoles(User user) {
        Role role = roleDao.findByName(Role.RoleName.OS)
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MSG));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
    }

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return L'utilisateur.
     */
    public User getUserByIdNotOptional(int id) {
        return userDao.findById(id).orElse(null);
    }

    /**
     * Convertit un User en UserSendDTO.
     *
     * @param user L'utilisateur à convertir.
     * @return L'utilisateur converti.
     */
    public UserSendDTO toDTO(User user) {
        UserSendDTO userDTO = new UserSendDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());

        // Extract role names and add them to a list
        List<String> roleNames = new ArrayList<>();
        for (GrantedAuthority authority : user.getAuthorities()) {
            roleNames.add(authority.getAuthority());
        }
        userDTO.setRoles(roleNames);
        return userDTO;
    }

    /**
     * Récupère un rôle par son nom.
     *
     * @param role Le nom du rôle.
     * @return Le rôle.
     */
    public Role getRoleByName(String role) {
        return roleDao.findByName(Role.RoleName.valueOf(role))
                .orElseThrow(() -> new IllegalArgumentException(ROLE_NOT_FOUND_MSG));
    }
}
