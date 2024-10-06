package com.example.backend.service;

import com.example.backend.dao.TeamGradeFromStudentDao;
import com.example.backend.model.TeamGradeFromStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des notes d'équipe des étudiants.
 */
@Service
public class TeamGradeFromStudentService {

    private final TeamGradeFromStudentDao teamGradeFromStudentDao;
    private final TeamService teamService;
    private final SprintService sprintService;

    /**
     * Constructeur de la classe TeamGradeFromStudentService.
     *
     * @param teamGradeFromStudentDao Le DAO des notes d'équipe des étudiants.
     * @param teamService Le service de gestion des équipes.
     * @param sprintService Le service de gestion des sprints.
     */
    @Autowired
    public TeamGradeFromStudentService(TeamGradeFromStudentDao teamGradeFromStudentDao, TeamService teamService, SprintService sprintService){
        this.teamGradeFromStudentDao = teamGradeFromStudentDao;
        this.teamService = teamService;
        this.sprintService = sprintService;
    }

    /**
     * Enregistre une note d'équipe des étudiants.
     *
     * @param teamNotingId L'identifiant de l'équipe notant.
     * @param teamToNoteId L'identifiant de l'équipe notée.
     * @param sprintId L'identifiant du sprint.
     * @param grade La note.
     */
    public void saveTeamGradeFromStudent(int teamNotingId, int teamToNoteId, int sprintId, int grade) {
        TeamGradeFromStudent teamGradeFromStudent = new TeamGradeFromStudent();
        teamGradeFromStudent.setGrade(grade);
        teamGradeFromStudent.setTeamNoting(teamService.getTeamById(teamNotingId));
        teamGradeFromStudent.setTeamToNote(teamService.getTeamById(teamToNoteId));
        teamGradeFromStudent.setSprint(sprintService.getSprintById(sprintId));
        teamGradeFromStudentDao.save(teamGradeFromStudent);
    }

    /**
     * Met à jour une note d'équipe des étudiants.
     *
     * @param teamNotingId L'identifiant de l'équipe notant.
     * @param teamToNoteId L'identifiant de l'équipe notée.
     * @param sprintId L'identifiant du sprint.
     * @param grade La note.
     */
    public void updateTeamGradeFromStudent(int teamNotingId, int teamToNoteId, int sprintId, int grade) {
        try{
            TeamGradeFromStudent teamGradeFromStudent = teamGradeFromStudentDao.findByTeamNotingIdAndTeamToNoteIdAndSprintId(teamNotingId, teamToNoteId, sprintId).orElseThrow();
            teamGradeFromStudent.setGrade(grade);
            teamGradeFromStudentDao.save(teamGradeFromStudent);
        } catch (Exception e) {
            saveTeamGradeFromStudent(teamNotingId, teamToNoteId, sprintId, grade);
        }
    }

    /**
     * Récupère une note d'équipe des étudiants.
     *
     * @param teamNotingId L'identifiant de l'équipe notant.
     * @param teamToNoteId L'identifiant de l'équipe notée.
     * @param sprintId L'identifiant du sprint.
     * @return La note d'équipe des étudiants.
     */
    public TeamGradeFromStudent getTeamGradeFromStudent(int teamNotingId, int teamToNoteId, int sprintId) {
        return teamGradeFromStudentDao.findByTeamNotingIdAndTeamToNoteIdAndSprintId(teamNotingId, teamToNoteId, sprintId).orElse(null);
    }

    /**
     * Récupère toutes les notes d'équipe des étudiants.
     *
     * @return La liste des notes d'équipe des étudiants.
     */
    public double getOTPR(int teamToNoteId, int sprintId) {
        try {
            List<TeamGradeFromStudent> teamGrades = getTeamGradesFromStudentForTeam(teamToNoteId, sprintId);
            if (teamGrades.isEmpty()){
                return 0;
            }
            return teamGrades.stream().mapToDouble(TeamGradeFromStudent::getGrade).sum()/teamGrades.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Récupère toutes les notes d'équipe des étudiants.
     *
     * @return La liste des notes d'équipe des étudiants.
     */
    public List<TeamGradeFromStudent> getTeamGradesFromStudentForTeam(int teamToNoteId, int sprintId) {
        return teamGradeFromStudentDao.findByTeamToNoteIdAndSprintId(teamToNoteId, sprintId).orElse(null);
    }
}
