package ru.dvdishka.battleroyale.logic.classes.drop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum DropContainerStage {

    PRE_CLICK_STAGE(
            Component.text("[CLOSED]")
            .color(NamedTextColor.GREEN)
            .decorate(TextDecoration.BOLD),
            Component.text("[CLOSED]")
                    .color(NamedTextColor.GREEN)
                    .decorate(TextDecoration.BOLD)),

    OPENING_STAGE(
            Component.text("[OPENING...")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD),
            Component.text("[OPENING]")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD)),

    OPEN_STAGE(
            Component.text("[OPEN]")
                    .color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD),
            Component.text("[OPEN]")
                    .color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD));

    private final Component stageComponent;
    private final Component stageNameComponent;

    DropContainerStage(Component stageComponent, Component stageNameComponent) {
        this.stageComponent = stageComponent;
        this.stageNameComponent = stageNameComponent;
    }

    public Component getForPercentStageComponent() {
        return stageComponent;
    }

    public Component getStageNameComponent() {
        return stageNameComponent;
    }
}
