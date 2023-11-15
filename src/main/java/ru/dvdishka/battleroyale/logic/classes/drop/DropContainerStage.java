package ru.dvdishka.battleroyale.logic.classes.drop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum DropContainerStage {

    PRE_CLICK_STAGE(Component.text("[CLOSED]")
            .color(NamedTextColor.GREEN)
            .decorate(TextDecoration.BOLD)),
    OPENING_STAGE(Component.text("[OPEN]")
            .color(NamedTextColor.RED)
            .decorate(TextDecoration.BOLD)),
    OPEN_STAGE(Component.text("[OPENING...]")
            .color(NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD));

    private Component stageComponent;

    DropContainerStage(Component stageComponent) {
        this.stageComponent = stageComponent;
    }

    public Component getStageComponent() {
        return stageComponent;
    }
}
