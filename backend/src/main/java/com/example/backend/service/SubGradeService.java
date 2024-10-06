package com.example.backend.service;

import com.example.backend.dao.SubGradeDao;
import com.example.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des sous-notes.
 */
@Service
public class SubGradeService {

    private final TeamGradeService teamGradeService;
    private final SubGradeDao subGradeDao;
    private final TeamGradeFromStudentService teamGradeFromStudentService;
    private final BonusMalusService bonusMalusService;
    private final UserService userService;
    private final SprintService sprintService;
    private final TeamService teamService;
    private final EvaluationService evaluationService;

    /**
     * Constructeur de la classe SubGradeService.
     *
     * @param teamGradeService Le service de gestion des notes d'équipe.
     * @param subGradeDao Le DAO des sous-notes.
     * @param teamGradeFromStudentService Le service de gestion des notes d'équipe des étudiants.
     * @param bonusMalusService Le service de gestion des bonus/malus.
     * @param userService Le service de gestion des utilisateurs.
     * @param sprintService Le service de gestion des sprints.
     * @param teamService Le service de gestion des équipes.
     * @param evaluationService Le service de gestion des évaluations.
     */
    @Autowired
    public SubGradeService(TeamGradeService teamGradeService, SubGradeDao subGradeDao, TeamGradeFromStudentService teamGradeFromStudentService, BonusMalusService bonusMalusService, UserService userService, SprintService sprintService, TeamService teamService, EvaluationService evaluationService) {
        this.teamGradeService = teamGradeService;
        this.subGradeDao = subGradeDao;
        this.teamGradeFromStudentService = teamGradeFromStudentService;
        this.bonusMalusService = bonusMalusService;
        this.userService = userService;
        this.sprintService = sprintService;
        this.teamService = teamService;
        this.evaluationService = evaluationService;
    }

    /**
     * Met à jour une sous-note.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param gradeType Le type de note.
     * @return La sous-note mise à jour.
     */
    public SubGrade updateSubGrade(int userId, int sprintId, GradeTypes gradeType) {
        Team myTeam = teamService.getTeamByUserId(userId);
        SubGrade subGrade = getSubGradeByUserIdAndSprintIdAndGradeType(userId, sprintId, gradeType);
        List<User> evaluators = new ArrayList<>();
        Double value = null;
        if((gradeType.getId() >= 2 && gradeType.getId() <= 4)) {
            evaluators.add(myTeam.getSupervisor());
            evaluators.addAll(userService.getTechnicalCoaches());
            updateEvaluations(subGrade, evaluators);
        } else if(gradeType.getId() == 5){
            evaluators = userService.getAllTeachers();
            updateEvaluations(subGrade, evaluators);
        } else if(gradeType.getId() == 8){
            evaluators.addAll(userService.getTeachersWithTeams());
        }  else if(gradeType.getId() == 9){
            evaluators.addAll(userService.getTechnicalCoaches());
        }else if (gradeType.getId() == 6 || gradeType.getId() == 7){
            value = bonusMalusService.getBonusMalusValue(userId, sprintId, gradeType.getId());
            evaluators.add(myTeam.getSupervisor());
        } else if (gradeType.getId() == 1){
            value = teamGradeFromStudentService.getOTPR(myTeam.getId(), sprintId);
            updateStatusSubGrade(subGrade, myTeam);
        } else {
            throw new IllegalArgumentException("Invalid grade type");
        }
        if(value != null){
            subGrade.setValue(value);
        } else {
            subGrade.setValue(calculateValue(subGrade));
        }
        for(User evaluator: evaluators){
            evaluationService.addEvaluator(subGrade, evaluator);
        }
        subGrade.setGradeType(gradeType);
        return subGradeDao.save(subGrade);
    }

    /**
     * Met à jour le statut d'une sous-note.
     *
     * @param subGrade La sous-note.
     * @param myTeam L'équipe de l'utilisateur.
     */
    private void updateStatusSubGrade(SubGrade subGrade, Team myTeam) {
        List<Team> teams = teamService.getAllTeams();
        List<TeamGradeFromStudent> teamGradeFromStudents = teamGradeFromStudentService.getTeamGradesFromStudentForTeam(myTeam.getId(), subGrade.getSprint().getId());
        if(teams.size()-1 == teamGradeFromStudents.size()){
            subGrade.setStatus(EvaluationStatus.COMPLETED);
        } else {
            subGrade.setStatus(EvaluationStatus.PENDING);
        }
    }

    /**
     * Met à jour les évaluations d'une sous-note.
     *
     * @param subGrade La sous-note.
     * @param evaluators Les évaluateurs.
     */
    private void updateEvaluations(SubGrade subGrade, List<User> evaluators) {
        for(User evaluator: evaluators){
            Evaluation evaluation = evaluationService.addEvaluator(subGrade, evaluator);
            Team myTeam = teamService.getTeamByUserId(subGrade.getUser().getId());
            Double value = teamGradeService.getAverageTeamGradeByCategoryId(subGrade.getGradeType().getId(), subGrade.getSprint().getId(), myTeam.getId(), evaluator.getId());
            evaluationService.updateEvaluation(evaluation, value);
        }
    }

    /**
     * Calcule la valeur d'une sous-note.
     *
     * @param subGrade La sous-note.
     * @return La valeur de la sous-note.
     */
    public Double calculateValue(SubGrade subGrade){
        return subGrade.getEvaluations().stream()
                .filter(evaluation -> evaluation.getValue() != null)
                .mapToDouble(Evaluation::getValue)
                .average()
                .orElse(0);
    }

    /**
     * Crée une sous-note.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param gradeType Le type de note.
     * @return La sous-note créée.
     */
    public SubGrade createSubGrade(int userId, int sprintId, GradeTypes gradeType) {
        SubGrade subGrade = new SubGrade();
        subGrade.setUser(userService.getUserById(userId));
        subGrade.setSprint(sprintService.getSprintById(sprintId));
        subGrade.setGradeType(gradeType);
        return subGradeDao.save(subGrade);
    }

    /**
     * Récupère une sous-note par l'identifiant de l'utilisateur, l'identifiant du sprint et le type de note.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param gradeType Le type de note.
     * @return La sous-note.
     */
    public SubGrade getSubGradeByUserIdAndSprintIdAndGradeType(int userId, int sprintId, GradeTypes gradeType) {
        try{
            return subGradeDao.findByUserIdAndSprintIdAndGradeType(userId, sprintId, gradeType).orElseThrow();
        } catch (Exception e) {
            return createSubGrade(userId, sprintId, gradeType);

        }
    }

    /**
     * Récupère une evaluation par une sous-note et un évaluateur.
     *
     * @param subGradeId L'identifiant de la sous-note.
     * @param evaluatorId L'identifiant de l'évaluateur.
     * @return L'évaluation.
     */
    public Evaluation getEvaluationBySubgrade(int subGradeId, int evaluatorId) {
        try{
            return evaluationService.getEvaluationBySubGradeIdAndEvaluatorId(subGradeId, evaluatorId);
        } catch (Exception e) {
            return evaluationService.addEvaluator(subGradeDao.findById(subGradeId).orElseThrow(), userService.getUserById(evaluatorId));
        }
    }
}
