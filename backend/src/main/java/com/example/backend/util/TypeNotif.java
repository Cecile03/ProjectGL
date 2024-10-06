package com.example.backend.util;

import lombok.Getter;

@Getter
public enum TypeNotif {
    BONUS_MALUS("Bonus/Malus"),
    NEW_TEAM("Nouvelle équipe"),;

    private final String roleString;

    TypeNotif(String roleString) {
        this.roleString = roleString;
    }
}