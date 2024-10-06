package com.example.backend.dao;

import com.example.backend.model.BonusMalus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonusMalusDao extends JpaRepository<BonusMalus, Integer> {

    List<BonusMalus> findAllBySprintId(int sprintId);
    List<BonusMalus> findAllByTeamIdAndSprintId(int teamId, int sprintId);
    BonusMalus findByAttributedToIdAndSprintIdAndIsUnlimited(int userId, int sprintId, boolean isUnlimited);
    List<BonusMalus> findAllByTeamIdAndSprintIdAndStatus(int teamId, int sprintId, BonusMalus.BonusMalusStatus status);
    BonusMalus findByAttributedToIdAndSprintIdAndIsUnlimitedAndStatus(int userId, int sprintId, boolean isUnlimited, BonusMalus.BonusMalusStatus status);

}
