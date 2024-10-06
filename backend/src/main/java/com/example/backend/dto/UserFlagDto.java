package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

public class UserFlagDto {

    @Getter
    @Setter
    private int id;
    @Getter
    private FlagDto flagId;
    @Getter
    @Setter
    private UserInteract userId;
    private boolean isTeamSwitched;
    private Boolean isValidated;
    @Setter
    @Getter
    private Integer canceledString;

    // Getters and setters

    public boolean isTeamSwitched() {
        return isTeamSwitched;
    }

    public void setTeamSwitched(boolean teamSwitched) {
        isTeamSwitched = teamSwitched;
    }

    public Boolean getValidated() {
        return isValidated;
    }

    public void setValidated(Boolean validated) {
        isValidated = validated;
    }

    public void setFlagDto(FlagDto flagDto) {
        this.flagId = flagDto;
    }

    public void setUserDto(UserInteract userInteract) {
        this.userId = userInteract;
    }

    public FlagDto getFlagDto() {
        return this.flagId;
    }

    public UserInteract getUserDto() {
        return this.userId;
    }
}
