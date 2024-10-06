package com.example.backend.service;

import com.example.backend.dao.BMValidationDao;
import com.example.backend.dao.BonusMalusDao;
import com.example.backend.dto.BonusMalusDTO;
import com.example.backend.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service de gestion des bonus et malus.
 */
@Service
public class BonusMalusService {

    private final BonusMalusDao bonusMalusDao;
    private final BMValidationDao bmValidationDao;
    private final TeamService teamService;
    private final SprintService sprintService;
    private final NotificationService notificationService;

    /**
     * Constructeur de la classe BonusMalusService.
     *
     * @param bonusMalusDao Le DAO des bonus et malus.
     * @param bmValidationDao Le DAO des validations de bonus et malus.
     * @param teamService Le service d'équipe.
     * @param sprintService Le service de sprint.
     * @param notificationService Le service de notification.
     */
    public BonusMalusService(BonusMalusDao bonusMalusDao, BMValidationDao bmValidationDao, TeamService teamService, SprintService sprintService, NotificationService notificationService) {
        this.bonusMalusDao = bonusMalusDao;
        this.bmValidationDao = bmValidationDao;
        this.teamService = teamService;
        this.sprintService = sprintService;
        this.notificationService = notificationService;
    }

    /**
     * Vérifie si un utilisateur a validé les bonus et malus.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return true si l'utilisateur a validé les bonus et malus, false sinon.
     */
    private boolean isUserValidatedBonusMalus(int userId, int teamId, int sprintId) {
        BMValidation bmV = bmValidationDao.findByUserIdAndTeamIdAndSprintId(userId, teamId, sprintId);
        return bmV != null && bmV.getUser().getId() == userId && bmV.getTeam().getId() == teamId;
    }

    /**
     * Vérifie si tous les utilisateurs ont validé les bonus et malus.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return true si tous les utilisateurs ont validé les bonus et malus, false sinon.
     */
    private boolean isAllUsersValidateBm(int teamId, int sprintId) {
        boolean allUsersValidated = true;
        List<User> usersInTeam = teamService.getTeamMembers(teamId);
        for (User u : usersInTeam) {
            if (!isUserValidatedBonusMalus(u.getId(), teamId, sprintId)) {
                allUsersValidated = false;
                break;
            }
        }
        return allUsersValidated;
    }

    /**
     * Convertit un BonusMalus en BonusMalusDTO.
     *
     * @param bm Le BonusMalus à convertir.
     * @return Le BonusMalusDTO converti.
     */
    private BonusMalusDTO convertToDTO(BonusMalus bm) {
        BonusMalusDTO bmDTO = new BonusMalusDTO();
        bmDTO.setId(bm.getId());
        bmDTO.setValue(bm.getValue());
        bmDTO.setComment(bm.getComment());
        bmDTO.setStatus(bm.getStatus().toString());
        bmDTO.setUnlimited(bm.isUnlimited());
        bmDTO.setAttributedBy(bm.getAttributedBy().getId());
        bmDTO.setAttributedTo(bm.getAttributedTo().getId());
        bmDTO.setTeamId(bm.getTeam().getId());
        bmDTO.setSprintId(bm.getSprint().getId());

        return bmDTO;
    }

    /**
     * Notifie les membres de l'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param emitter L'émetteur de la notification.
     * @param isForNewBm true si la notification est pour de nouveaux bonus et malus, false sinon.
     */
    private void notifyTeam(int teamId, User emitter, boolean isForNewBm) {
        List<User> usersInTeam = teamService.getTeamMembers(teamId);

        for(User user : usersInTeam) {
            Notification notif = new Notification();
            notif.setEmitter(emitter);
            notif.setReceiver(user);
            notif.setType("Bonus/Malus");
            notif.setStatus(Notification.Status.UNREAD);
            notif.setGroupId(0);
            if(isForNewBm) {
                notif.setDescription("Une nouvelle répartition de bonus et malus équitable a été effectuée.");
            } else {
                notif.setDescription("Les bonus et malus distribués ont été validés par tous les membres de votre équipe.");
            }
            notificationService.createNotification(notif);
        }
    }

    /**
     * Notifie le superviseur de l'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param emitter L'émetteur de la notification.
     */
    private void notifySupervisorNewBm(int teamId, User emitter) {
        Team team = teamService.getTeamById(teamId);

        Notification notif = new Notification();
        notif.setEmitter(emitter);
        notif.setReceiver(team.getSupervisor());
        notif.setType("Bonus/Malus");
        notif.setStatus(Notification.Status.UNREAD);
        notif.setGroupId(0);
        notif.setDescription("Des nouveaux bonus et malus ont été distribués dans votre équipe.");

        notificationService.createNotification(notif);
    }

    /**
     * Récupère tous les bonus et malus.
     *
     * @param sprintId L'identifiant du sprint.
     * @return La liste des bonus et malus.
     */
    public List<List<BonusMalusDTO>> getAllBonusMalus(int sprintId) {
        List<BonusMalus> bonusMalusList = bonusMalusDao.findAllBySprintId(sprintId);

        Map<Integer, List<BonusMalusDTO>> bonusMalusByTeamId = new HashMap<>();
        for (BonusMalus bm : bonusMalusList) {
            int teamId = bm.getTeam().getId();
            // Convertir BonusMalus en BonusMalusDTO
            BonusMalusDTO bmDTO = convertToDTO(bm);
            // Ajouter le BonusMalusDTO à la liste correspondante
            bonusMalusByTeamId.computeIfAbsent(teamId, k -> new ArrayList<>()).add(bmDTO);
        }

        // Retourner les valeurs du Map (listes de BonusMalusDTO classées par teamId)
        return new ArrayList<>(bonusMalusByTeamId.values());
    }

    /**
     * Récupère les bonus et malus illimités par équipe et sprint.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return La liste des bonus et malus illimités.
     */
    public List<BonusMalusDTO> getUBmByTeamAndSprint(int teamId, int sprintId) {
        List<BonusMalus> bonusMalusList = bonusMalusDao.findAllByTeamIdAndSprintId(teamId, sprintId);
        List<BonusMalusDTO> bonusMalusDTOList = new ArrayList<>();

        for (BonusMalus bm : bonusMalusList) {
            if(bm.isUnlimited()) {
                BonusMalusDTO bmDTO = convertToDTO(bm);
                bonusMalusDTOList.add(bmDTO);
            }
        }
        return bonusMalusDTOList;
    }

    /**
     * Récupère les bonus et malus limités par équipe et sprint.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return La liste des bonus et malus limités.
     */
    public List<BonusMalusDTO> getLBmByTeamAndSprint(int teamId, int sprintId) {
        List<BonusMalus> bonusMalusList = bonusMalusDao.findAllByTeamIdAndSprintId(teamId, sprintId);
        List<BonusMalusDTO> bonusMalusDTOList = new ArrayList<>();

        for (BonusMalus bm : bonusMalusList) {
            if(!bm.isUnlimited()) {
                BonusMalusDTO bmDTO = convertToDTO(bm);
                bonusMalusDTOList.add(bmDTO);
            }
        }
        return bonusMalusDTOList;
    }

    /**
     * Ajoute des bonus et malus par équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param bonusMalusList La liste des bonus et malus à ajouter.
     * @return La liste des bonus et malus ajoutés.
     */
    private List<BonusMalusDTO> addBmByTeamAsSS(List<BonusMalusDTO> bonusMalusList, int teamId, int sprintId, User currentUser) {
        Team team = teamService.getTeamById(teamId);
        if (team == null) {
            throw new EntityNotFoundException("Team with id " + teamId + " doesn't exist.");
        }
        BonusMalus.BonusMalusStatus status = BonusMalus.BonusMalusStatus.VALIDATED;

        Sprint sprint = sprintService.getSprintById(sprintId);

        List<BonusMalus> list = new ArrayList<>();
        for (BonusMalusDTO bmDTO : bonusMalusList) {
            // To be sure userId "attributedTo" passed in body is in the team
            User attributedTo = teamService.getOneUserInTeam(bmDTO.getAttributedTo(), team.getId());

            // Check if BonusMalus already exists for this user and sprint
            BonusMalus existingBm = bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimited(attributedTo.getId(), sprintId, true);
            BonusMalus bm;
            bm = Objects.requireNonNullElseGet(existingBm, BonusMalus::new);

            bm.setStatus(status);
            bm.setTeam(team);
            bm.setSprint(sprint);
            bm.setAttributedBy(currentUser);
            bm.setAttributedTo(attributedTo);
            bm.setValue(bmDTO.getValue());
            bm.setComment(bmDTO.getComment());
            bm.setUnlimited(true);

            BonusMalus savedBm = bonusMalusDao.save(bm);
            list.add(savedBm);
        }

        List<BonusMalusDTO> dtoList = new ArrayList<>();
        for (BonusMalus bm : list) {
            BonusMalusDTO bmDTO = convertToDTO(bm);
            dtoList.add(bmDTO);
        }

        return dtoList;
    }

    /**
     * Ajoute des bonus et malus par utilisateur.
     *
     * @param bonusMalusList La liste des bonus et malus à ajouter.
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param currentUser L'utilisateur courant.
     * @return La liste des bonus et malus ajoutés.
     */
    private List<BonusMalusDTO> addBmByTeamAsOS(List<BonusMalusDTO> bonusMalusList, int userId, int sprintId, User currentUser) {
        Team team = teamService.getTeamByUserId(userId);
        if (team == null) {
            throw new EntityNotFoundException("Team for userId " + userId + " doesn't exist.");
        }
        BonusMalus.BonusMalusStatus status = BonusMalus.BonusMalusStatus.PENDING;

        Sprint sprint = sprintService.getSprintById(sprintId);
        deleteAllPendingByTeamId(team.getId(), sprintId);

        List<BonusMalus> list = new ArrayList<>();
        float totalValue = 0;
        for (BonusMalusDTO bmDTO : bonusMalusList) {
            // To be sure userId "attributedTo" passed in body is in the team
            User attributedTo = teamService.getOneUserInTeam(bmDTO.getAttributedTo(), team.getId());
            BonusMalus bm = new BonusMalus();

            bm.setStatus(status);
            bm.setTeam(team);
            bm.setSprint(sprint);
            bm.setAttributedBy(currentUser);
            bm.setAttributedTo(attributedTo);
            bm.setValue(bmDTO.getValue());
            totalValue += bmDTO.getValue();
            bm.setComment(bmDTO.getComment());
            bm.setUnlimited(false);

            list.add(bm);
        }

        if (totalValue != 0) {
            throw new IllegalArgumentException("The total value of bonus/malus must be zero for non-SS users.");
        }

        List<BonusMalus> savedBonusMalus = bonusMalusDao.saveAll(list);
        notifyTeam(team.getId(), currentUser, true);
        notifySupervisorNewBm(team.getId(), currentUser);

        List<BonusMalusDTO> dtoList = new ArrayList<>();
        for (BonusMalus bm : savedBonusMalus) {
            BonusMalusDTO bmDTO = convertToDTO(bm);
            dtoList.add(bmDTO);
        }

        return dtoList;
    }

    /**
     * Ajoute des bonus et malus par équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param bonusMalusList La liste des bonus et malus à ajouter.
     * @return La liste des bonus et malus ajoutés.
     */
    public List<BonusMalusDTO> addBonusMalusByTeam(int teamId, int sprintId, List<BonusMalusDTO> bonusMalusList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        int userId = currentUser.getId();

        boolean isSS = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SS"));

        if(isSS) {
            return addBmByTeamAsSS(bonusMalusList, teamId, sprintId, currentUser);
        } else {
            return addBmByTeamAsOS(bonusMalusList, userId, sprintId, currentUser);
        }
    }

    /**
     * Valide les bonus et malus par équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     */
    public void validateTeamBM(int teamId, int sprintId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        int userId = currentUser.getId();

        boolean isSS = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SS"));

        Team team;
        if(isSS) {
            team = teamService.getTeamById(teamId);
        } else {
            team = teamService.getTeamByUserId(userId);
        }
        Sprint sprint = sprintService.getSprintById(sprintId);

        if (team == null) {
            throw new EntityNotFoundException("Team doesn't exist.");
        }
        // if user who did the request is an OS, add BM validation
        if(!isSS) {
            BMValidation bmValidation = new BMValidation();
            bmValidation.setTeam(team);
            bmValidation.setSprint(sprint);
            bmValidation.setUser(currentUser);

            bmValidationDao.save(bmValidation);
        }

        if (isAllUsersValidateBm(team.getId(), sprint.getId())) {
            notifyTeam(team.getId(), currentUser, false);

            // If the user who did the request is an SS, check all students validate B/M, and mark them validated
            if(isSS){
                List<BonusMalus> list = bonusMalusDao.findAllByTeamIdAndSprintId(team.getId(), sprint.getId());
                for (BonusMalus bm : list) {
                    bm.setStatus(BonusMalus.BonusMalusStatus.VALIDATED);
                }
                bonusMalusDao.saveAll(list);
            }
        }

    }

    /**
     * Récupère les membres qui ont validé les bonus et malus.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return La liste des membres qui ont validé les bonus et malus.
     */
    public List<Integer> getMembersWhoValidateBM(int teamId, int sprintId) {
        List<User> usersInTeam = teamService.getTeamMembers(teamId);
        List<Integer> validatedBy = new ArrayList<>();
        for(User user : usersInTeam) {
            if(isUserValidatedBonusMalus(user.getId(), teamId, sprintId)) {
                validatedBy.add(user.getId());
            }
        }
         return validatedBy;
    }

    /**
     * Supprime tous les bonus et malus en attente par équipe et sprint.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     */
    public void deleteAllPendingByTeamId(int teamId, int sprintId) {
        List<BonusMalus> bonusMalusList = bonusMalusDao.findAllByTeamIdAndSprintIdAndStatus(teamId, sprintId, BonusMalus.BonusMalusStatus.PENDING);
        bonusMalusDao.deleteAll(bonusMalusList);

        List<BMValidation> bmValidationList = bmValidationDao.findAllByTeamIdAndSprintId(teamId, sprintId);
        bmValidationDao.deleteAll(bmValidationList);
    }

    /**
     * Récupère les bonus et malus par équipe et sprint.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return La liste des bonus et malus.
     */
    public BonusMalus getBonusMalusStudentByTeamIdAndSprintIdValidated(int userId, int sprintId) {
        return bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(userId, sprintId, false, BonusMalus.BonusMalusStatus.VALIDATED);
    }

    /**
     * Récupère les bonus et malus SS par équipe et sprint.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return La liste des bonus et malus.
     */
    public BonusMalus getBonusMalusSSByTeamIdAndSprintIdValidated(int userId, int sprintId) {
        return bonusMalusDao.findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(userId, sprintId, true, BonusMalus.BonusMalusStatus.VALIDATED);
    }

    /**
     * Récupère la valeur du bonus et malus.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param gradeTypeId Le type de note.
     * @return La valeur du bonus et malus.
     */
    public double getBonusMalusValue(int userId, int sprintId, int gradeTypeId){
        BonusMalus bm;
        if(gradeTypeId == GradeTypes.GradeTypesEnum.TEBM.getId()) {
            bm = getBonusMalusStudentByTeamIdAndSprintIdValidated(userId, sprintId);
        } else {
            bm = getBonusMalusSSByTeamIdAndSprintIdValidated(userId, sprintId);
        }
        if(bm == null) {
            return 0;
        }
        return bm.getValue();
    }
}
