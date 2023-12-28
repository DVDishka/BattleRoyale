package ru.dvdishka.battleroyale.logic.classes.superpower;

public enum CustomEffectType {

    NO_FALL_DAMAGE("No Fall Damage");

    CustomEffectType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return this.name;
    }
}
