package com.example.backend.service;

import com.example.backend.dao.*;
import com.example.backend.dto.TeamDTO;
import com.example.backend.dto.TeamSendDTO;
import com.example.backend.dto.UserInteract;
import com.example.backend.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service de gestion des équipes.
 */
@Service
public class TeamService {

    private final UserService userService;
    private final CriteriaService criteriaService;
    private final UserTeamDao userTeamDao;
    private final TeamDao teamDao;
    private final CleanupService cleanupService;

    /**
     * Constructeur de la classe TeamService.
     *
     * @param userService Le service de gestion des utilisateurs.
     * @param criteriaService Le service de gestion des critères.
     * @param teamDao Le DAO des équipes.
     * @param userTeamDao Le DAO des utilisateurs dans les équipes.
     * @param cleanupService Le service de nettoyage.
     */
    @Autowired
    public TeamService(UserService userService,CriteriaService criteriaService, TeamDao teamDao, UserTeamDao userTeamDao, CleanupService cleanupService) {
        this.userService = userService;
        this.criteriaService = criteriaService;
        this.teamDao = teamDao;
        this.userTeamDao = userTeamDao;
        this.cleanupService = cleanupService;
    }

    /**
     * Crée les équipes avec le bon nombre de filles et de bachelors.
     *
     * @param users La liste des utilisateurs.
     * @param teachers La liste des enseignants.
     * @param nameOfTeams Les noms des équipes.
     * @param numberOfTeams Le nombre d'équipes.
     * @param numberOfGirlsPerTeam Le nombre de filles par équipe.
     * @return La liste des équipes créées.
     */
    public List<Team> createTeams(List<User> users, List<User> teachers, String[] nameOfTeams, int numberOfTeams, int numberOfGirlsPerTeam) {
        if(users.isEmpty() || teachers.isEmpty()) {
            throw new IllegalArgumentException("Not enough users or teachers to create the teams");
        }
        if(users.size() < numberOfTeams) {
            throw new IllegalArgumentException("Not enough users to create the teams");
        }
        if(teachers.size() < numberOfTeams) {
            throw new IllegalArgumentException("Not enough teachers to create the teams");
        }

        Criteria criteria = new Criteria();
        criteria.setNumberOfGirls(numberOfGirlsPerTeam);
        criteria.setNumberOfTeams(numberOfTeams);
        //Créer les équipes avec le bon nombre de filles et de bachelors
        List<List<User>> teamsOrganized = TeamBuilder.buildTeams(users, criteria);

        // Balance the teams in terms of grades
        List<List<User>> balancedTeams = TeamBuilder.balanceTeams(teamsOrganized, criteria);

        // Transform the lists of users into teams
        List<Team> teamsWithoutTeacher = formatTeams(balancedTeams, nameOfTeams, criteria);

        // Add the teachers to the teams
        return addTeachers(teamsWithoutTeacher, teachers);
    }

    /**
     * Ajoute les enseignants aux équipes.
     *
     * @param teamsWithoutTeacher Les équipes sans enseignant.
     * @param teachers Les enseignants.
     * @return La liste des équipes avec les enseignants.
     */
    private List<Team> addTeachers(List<Team> teamsWithoutTeacher, List<User> teachers) {
        for(int i = 0; i < teamsWithoutTeacher.size(); i++) {
            User teacher = teachers.get(i);
            addTeamSupervisor(teamsWithoutTeacher.get(i), teacher);
        }
        return teamsWithoutTeacher;
    }

    /**
     * Formate les équipes.
     *
     * @param teamsNotFormatted Les équipes non formatées.
     * @param names Les noms des équipes.
     * @param criteria Les critères.
     * @return La liste des équipes formatées.
     */
    private List<Team> formatTeams(List<List<User>> teamsNotFormatted, String[] names, Criteria criteria) {
        List<Team> teams = new ArrayList<>();
        int cmpt = 0;
        criteriaService.save(criteria);
        for(List<User> team : teamsNotFormatted) {
            Team newTeam = new Team();
            newTeam.setCriteria(criteria);
            cmpt++;
            try{
                newTeam.setName(names[cmpt-1]);
            } catch (Exception e) {
                newTeam.setName("Team "+cmpt);
            }
            newTeam.setStatus("none");
            teamDao.save(newTeam);
            for(User user : team) {
                //We're supposed to have the user in the database
                if (userService.existsByEmail(user.getEmail())) {
                    User updateUser = userService.loadUserByEmail(user.getEmail());
                    addTeamMember(newTeam, updateUser);
                }
            }
            teamDao.save(newTeam);
            teams.add(newTeam);
        }
        return teams;
    }

    /**
     * Ajoute un membre à une équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param userId L'identifiant de l'utilisateur.
     * @return L'utilisateur.
     */
    public User addTeamMember(int teamId, int userId) {
        Team team = teamDao.findById(teamId).orElseThrow();
        User user = userService.getUserById(userId);
        UserTeam ut = new UserTeam(user,team);
        user.addTeam(team);
        team.getUsers().add(user);
        teamDao.save(team);
        userService.updateUser(user);
        userTeamDao.save(ut);
        return user;
    }

    /**
     * Ajoute un membre à une équipe.
     *
     * @param team L'équipe.
     * @param user L'utilisateur.
     */
    public void addTeamMember(Team team, User user) {
        UserTeam ut = new UserTeam(user,team);
        user.addTeam(team);
        team.getUsers().add(user);
        teamDao.save(team);
        userService.updateUser(user);
        userTeamDao.save(ut);
    }

    /**
     * Ajoute un superviseur à une équipe.
     *
     * @param team L'équipe.
     * @param teacher L'enseignant.
     */
    private void addTeamSupervisor(Team team, User teacher) {
        team.setSupervisor(teacher);
        teamDao.save(team);
        userService.updateUser(teacher);
    }

    /**
     * Récupère les membres d'une équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @return La liste des membres de l'équipe.
     */
    public List<User> getTeamMembers(int teamId) {
        return userService.findByTeam(teamId);
    }

    /**
     * Récupère toutes les équipes.
     *
     * @return La liste des équipes.
     */
    public List<Team> getAllTeams() {
        return teamDao.findAll();
    }

    /**
     * Récupère une équipe par son identifiant.
     *
     * @param id L'identifiant de l'équipe.
     * @return L'équipe.
     */
    public Team getTeamById(int id) {
        return teamDao.findById(id).orElse(null);
    }

    /**
     * Récupère une équipe par l'identifiant d'un utilisateur.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return L'équipe.
     */
    public Team getTeamByUserId(int id) {
        User user = userService.getUserById(id);
        try {
            Optional<UserTeam> userTeamOptional = userTeamDao.findByUser(user);
            UserTeam userTeam = userTeamOptional.orElseThrow(() -> new EntityNotFoundException("UserTeam for user with id " + id + " doesn't exist."));
            return userTeam.getTeam();

        } catch (Exception e) {
            throw new EntityNotFoundException("Failed to get team by user id: " + id, e);
        }
    }

    /**
     * Récupère un utilisateur dans une équipe.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param teamId L'identifiant de l'équipe.
     * @return L'utilisateur.
     */
    public User getOneUserInTeam(int userId, int teamId) {
        User user = userService.getUserById(userId);
        Team team = getTeamById(teamId);
        if (team == null) {
            throw new EntityNotFoundException("Team with id " + teamId + " doesn't exist.");
        }

        Optional<UserTeam> userTeamOptional = userTeamDao.findByUserAndTeam(user, team);
        if (userTeamOptional.isPresent()) {
            return user;
        } else {
            throw new EntityNotFoundException("User with id " + userId + " is not in team with id " + teamId);
        }
    }

    /**
     * Met à jour une équipe.
     *
     * @param team L'équipe.
     */
    public void updateTeam(TeamDTO team) {
        Team teamOrigin = teamDao.findById(team.getId()).orElseThrow();
        cleanTeam(teamOrigin);
        teamOrigin.setName(team.getName());
        teamOrigin.setStatus(team.getStatus());
        teamOrigin.setSupervisor(userService.getUserById(team.getSupervisor().getId()));
        addTeamSupervisor(teamOrigin, userService.getUserById(team.getSupervisor().getId()));
        Set<User> set = new HashSet<>();
        for(UserInteract userDTO : team.getUsers()) {
            User user = userService.getUserById(userDTO.getId());
            set.add(user);
            addTeamMember(teamOrigin, userService.getUserById(userDTO.getId()));
        }
        teamOrigin.setUsers(set);
        teamDao.save(teamOrigin);
    }

    /**
     * Nettoie une équipe.
     *
     * @param team L'équipe.
     */
    private void cleanTeam(Team team) {
        List<UserTeam> uts = userTeamDao.findByTeam(team);
        userTeamDao.deleteAll(uts);
    }

    /**
     * Supprime une équipe.
     *
     * @param id L'identifiant de l'équipe.
     */
    public void deleteTeam(int id) {
        Team team = teamDao.findById(id).orElseThrow();
        this.cleanTeam(team);
        teamDao.delete(team);
    }

    /**
     * Supprime toutes les équipes.
     */
    public void deleteAllTeam() {
        cleanupService.deleteAllTeam();
    }

    /**
     * Transforme une équipe en DTO.
     *
     * @param team L'équipe.
     * @return L'équipe en DTO.
     */
    public TeamSendDTO toDTO(Team team) {
        TeamSendDTO teamDTO = new TeamSendDTO();
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        return teamDTO;
    }

    /**
     * Valide une équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     */
    public void validateTeam(int teamId) {
        Team team = teamDao.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        team.setValidated(true);
        teamDao.save(team);
    }

    /**
     * Change le statut d'une équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param status Le statut.
     */
    public void toggleStatus(int teamId, String status) {
        Team team = teamDao.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        team.setStatus(status);
        teamDao.save(team);
    }
}
