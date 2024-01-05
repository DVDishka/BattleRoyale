package ru.dvdishka.battleroyale.ui;

import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import me.catcoder.sidebar.pager.SidebarPager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Zone;
import ru.dvdishka.battleroyale.logic.common.ConfigVariables;
import ru.dvdishka.battleroyale.logic.common.GameVariables;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;

import java.util.List;

public class Radar {

    private final SidebarPager<Component> radarPager;
    private final Sidebar<Component> radarFirstPage;

    private static Radar instance = null;
    private static boolean isInitialized = false;

    private final TextColor safeZoneColor = NamedTextColor.AQUA;
    private final TextColor stableZoneColor = NamedTextColor.YELLOW;
    private final TextColor movingZoneFirstColor = NamedTextColor.RED;
    private final TextColor movingZoneSecondColor = NamedTextColor.DARK_RED;
    private volatile TextColor movingZoneColor = movingZoneFirstColor;

    private final TextColor firstCordColor = NamedTextColor.YELLOW;
    private final TextColor secondCordColor = NamedTextColor.RED;

    public String movingZoneChar = "=";

    private Radar() {

        radarFirstPage = ProtocolSidebar.newAdventureSidebar(
                Component.text("RADAR")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD),
                PluginVariables.plugin);

        radarFirstPage.addUpdatableLine(player -> {
            updateMovingZoneColor();
            return updateRadar(player, 0);
        });

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 1));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 2));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 3));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 4));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 5));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 6));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 7));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 8));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 9));

        radarFirstPage.addUpdatableLine(player -> updateRadar(player, 10));

        radarFirstPage.updateLinesPeriodically(10, 10);

        radarFirstPage.addUpdatableLine(player -> {

            Component component = Component.empty()
                    .append(Component.text("X:")
                            .decorate(TextDecoration.BOLD)
                            .color(firstCordColor))
                    .append(Component.space())
                    .append(Component.text(Zone.getInstance().getCurrentLeftBorder())
                            .color(firstCordColor))
                    .append(Component.space())
                    .append(Component.text("->"))
                    .append(Component.space())
                    .append(Component.text(Zone.getInstance().getNewLeftBorder())
                            .color(firstCordColor));

            return component;
        });

        radarFirstPage.addUpdatableLine(player -> {

            Component component = Component.empty()
                    .append(Component.text("X:")
                            .decorate(TextDecoration.BOLD)
                            .color(secondCordColor))
                    .append(Component.space())
                    .append(Component.text(Zone.getInstance().getCurrentRightBorder())
                            .color(secondCordColor))
                    .append(Component.space())
                    .append(Component.text("->"))
                    .append(Component.space())
                    .append(Component.text(Zone.getInstance().getNewRightBorder())
                            .color(secondCordColor));

            return component;
        });

        radarFirstPage.addUpdatableLine(player -> {

            Component component = Component.empty()
                    .append(Component.text("Z:")
                            .decorate(TextDecoration.BOLD)
                            .color(firstCordColor))
                    .append(Component.space())
                    .append(Component.text(Zone.getInstance().getCurrentLowerBorder())
                            .color(firstCordColor))
                    .append(Component.space())
                    .append(Component.text("->"))
                    .append(Component.space())
                    .append(Component.text(Zone.getInstance().getNewLowerBorder())
                            .color(firstCordColor));

            return component;
        });

        radarFirstPage.addUpdatableLine(player -> {

            Component component = Component.empty()
                    .append(Component.text("Z:")
                            .decorate(TextDecoration.BOLD)
                            .color(secondCordColor))
                    .append(Component.space())
                    .append(Component.text(Zone.getInstance().getCurrentUpperBorder())
                            .color(secondCordColor))
                    .append(Component.space())
                    .append(Component.text("->"))
                    .append(Component.space())
                    .append(Component.text(Zone.getInstance().getNewUpperBorder())
                            .color(secondCordColor));

            return component;
        });

        radarPager = new SidebarPager<>(List.of(radarFirstPage), 0, PluginVariables.plugin);
    }

    public static Radar getInstance() {
        if (instance == null) {
            instance = new Radar();
            isInitialized = true;
        }
        return instance;
    }

    public void addViewer(Player player) {
        radarPager.show(player);
    }

    public void updateMovingZoneColor() {

        if (!Zone.getInstance().isZoneMoving()) {
            movingZoneColor = stableZoneColor;
        }
        else {
            if (movingZoneColor == movingZoneFirstColor) {
                movingZoneColor = movingZoneSecondColor;
            } else {
                movingZoneColor = movingZoneFirstColor;
            }
        }
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    public void register() {}

    public void unregister() {

        try {
            isInitialized = false;
            radarFirstPage.destroy();
            radarPager.destroy();
            instance = null;
        } catch (Exception ignored) {}
    }

    private Component updateRadar(Player player, int lineNumber) {

        int playerRadarPositionX = getPlayerRadarPositionX(player);
        int playerRadarPositionZ = getPlayerRadarPositionZ(player);

        Component playerSymbolComponent = Component.text(getPlayerDirectionChar(player))
                .color(NamedTextColor.GREEN);

        Component component = Component.empty();

        if (lineNumber == 0 || lineNumber == 10) {

            if (lineNumber == playerRadarPositionZ) {

                component = Component
                        .text("=".repeat(playerRadarPositionX))
                        .append(playerSymbolComponent)
                        .append(Component.text("=".repeat(10 - playerRadarPositionX)));

            } else {

                component = Component.text("=".repeat(11));
            }
        }

        else if (lineNumber == 1 || lineNumber == 9) {

            if (lineNumber == playerRadarPositionZ) {

                if (playerRadarPositionX == 0) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX > 0 && playerRadarPositionX < 10) {

                    component = component
                            .append(Component.text(movingZoneChar.repeat(playerRadarPositionX - 1))
                                    .color(movingZoneColor))
                            .append(playerSymbolComponent)
                            .append(Component.text(movingZoneChar.repeat(10 - playerRadarPositionX))
                                    .color(movingZoneColor));
                } else {

                    if (!movingZoneChar.equals("<") && !movingZoneChar.equals(">")) {
                        component = component.append(Component.text(movingZoneChar.repeat(9))
                                .color(movingZoneColor));
                    } else {
                        component = component.append(Component.text(movingZoneChar.repeat(10))
                                .color(movingZoneColor));
                    }
                }

                if (playerRadarPositionX == 10) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }
            } else {
                component = Component
                        .text("=");

                if (!movingZoneChar.equals("<") && !movingZoneChar.equals(">")) {
                        component = component
                                .append(Component.text(movingZoneChar.repeat(9))
                                        .color(movingZoneColor));
                } else {
                    component = component
                            .append(Component.text(movingZoneChar.repeat(10))
                                    .color(movingZoneColor));
                }

                component = component
                        .append(Component.text("="));
            }
        }

        else if (lineNumber == 2 || lineNumber == 8 || GameVariables.zoneStage >= ConfigVariables.zones.size() && (lineNumber >= 2 && lineNumber <= 8)) {

            if (lineNumber == playerRadarPositionZ) {

                if (playerRadarPositionX == 0) {

                    component = component
                            .append(playerSymbolComponent);

                } else {

                    component = component
                            .append(Component.text("="));
                }

                if (playerRadarPositionX == 1) {

                    component = component
                            .append(playerSymbolComponent);
                } else {

                    component = component
                            .append(Component.text(movingZoneChar)
                                    .color(movingZoneColor));
                }

                if (playerRadarPositionX > 1 && playerRadarPositionX < 9) {

                    component = component
                            .append(Component.text("=".repeat(playerRadarPositionX - 2)))
                            .append(playerSymbolComponent)
                            .append(Component.text("=".repeat(8 - playerRadarPositionX)));
                } else {

                    component = component
                            .append(Component.text("=".repeat(7)));
                }

                if (playerRadarPositionX == 9) {

                    component = component
                            .append(playerSymbolComponent);

                } else {

                    component = component
                            .append(Component.text(movingZoneChar)
                                    .color(movingZoneColor));
                }

                if (playerRadarPositionX == 10) {

                    component = component
                            .append(playerSymbolComponent);
                } else {

                    component = component
                            .append(Component.text("="));
                }

            } else {

                component = component
                        .append(Component
                                .text("="))
                        .append(Component.text(movingZoneChar)
                                .color(movingZoneColor))
                        .append(Component.text("=".repeat(7)))
                        .append(Component.text(movingZoneChar)
                                .color(movingZoneColor))
                        .append(Component.text("="));
            }
        }

        else if (lineNumber == 3 || lineNumber == 7) {

            if (playerRadarPositionZ == lineNumber) {

                if (playerRadarPositionX == 0) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX == 1) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text(movingZoneChar)
                            .color(movingZoneColor));
                }

                if (playerRadarPositionX == 2) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX > 2 && playerRadarPositionX < 6) {

                    component = component
                            .append(Component.text("=".repeat(playerRadarPositionX - 3))
                                    .color(safeZoneColor))
                            .append(playerSymbolComponent)
                            .append(Component.text("=".repeat(5 - (playerRadarPositionX - 3)))
                                    .color(safeZoneColor));

                } else {

                    component = component.append(Component.text("=".repeat(5))
                            .color(safeZoneColor));
                }

                if (playerRadarPositionX == 8) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX == 9) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text(movingZoneChar)
                            .color(movingZoneColor));
                }

                if (playerRadarPositionX == 10) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

            } else {

                component = component
                        .append(Component.text("="))
                        .append(Component.text(movingZoneChar)
                                .color(movingZoneColor))
                        .append(Component.text("="))
                        .append(Component.text("=".repeat(5))
                                .color(safeZoneColor))
                        .append(Component.text("="))
                        .append(Component.text(movingZoneChar)
                                .color(movingZoneColor))
                        .append(Component.text("="));
            }
        }

        else if (lineNumber == 4 || lineNumber == 5 || lineNumber == 6) {

            if (playerRadarPositionZ == lineNumber) {

                if (playerRadarPositionX == 0) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX == 1) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text(movingZoneChar)
                            .color(movingZoneColor));
                }

                if (playerRadarPositionX == 2) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX == 3) {

                    component = component
                            .append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("=")
                            .color(safeZoneColor));
                }

                if (playerRadarPositionX > 3 && playerRadarPositionX < 7) {

                    component = component
                            .append(Component.text("=".repeat(playerRadarPositionX - 4)))
                            .append(playerSymbolComponent)
                            .append(Component.text("=".repeat(6 - playerRadarPositionX)));

                } else {

                    component = component.append(Component.text("=".repeat(3)));
                }

                if (playerRadarPositionX == 7) {

                    component = component
                            .append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("=")
                            .color(safeZoneColor));
                }

                if (playerRadarPositionX == 8) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX == 9) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text(movingZoneChar)
                            .color(movingZoneColor));
                }

                if (playerRadarPositionX == 10) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

            } else {

                component = component
                        .append(Component.text("="))
                        .append(Component.text(movingZoneChar)
                                .color(movingZoneColor))
                        .append(Component.text("="))
                        .append(Component.text("=")
                                .color(safeZoneColor))
                        .append(Component.text("=".repeat(3)))
                        .append(Component.text("=")
                                .color(safeZoneColor))
                        .append(Component.text("="))
                        .append(Component.text(movingZoneChar)
                                .color(movingZoneColor))
                        .append(Component.text("="));
            }
        }

        return component.decorate(TextDecoration.BOLD);
    }

    private int getPlayerRadarPositionX(Player player) {

        int playerX = player.getLocation().getBlockX();
        double playerFloatX = player.getLocation().getX();
        int playerZ = player.getLocation().getBlockZ();
        double playerFloatZ = player.getLocation().getZ();
        Zone zone = Zone.getInstance();

        if (playerFloatX - zone.getCurrentLeftFloatBorder() < 0) {
            return 0;
        }

        if (playerFloatX - zone.getCurrentRightFloatBorder() >= 0) {
            return 10;
        }

        if (playerFloatZ < zone.getCurrentLowerFloatBorder() || playerFloatZ >= zone.getCurrentUpperFloatBorder()) {

            int segment = (int) ((zone.getCurrentRightFloatBorder() - zone.getCurrentLeftFloatBorder()) / 9 + 1);

            return (int) ((playerFloatX - zone.getCurrentLeftFloatBorder()) / segment + 1);
        }

        else if (GameVariables.zoneStage < ConfigVariables.zones.size()) {

            if (playerX - zone.getNewLeftBorder() < 0) {
                return 2;
            }

            if (playerX - zone.getNewRightBorder() >= 0) {
                return 8;
            }

            if (playerZ < zone.getNewLowerBorder() || playerZ >= zone.getNewUpperBorder()) {

                int segment = (zone.getNewRightBorder() - zone.getNewLeftBorder()) / 5 + 1;

                return (playerX - zone.getNewLeftBorder()) / segment + 3;
            }

            else {

                int segment = (zone.getNewRightBorder() - zone.getNewLeftBorder()) / 3 + 1;

                return (playerX - zone.getNewLeftBorder()) / segment + 4;
            }

        } else {

            int segment = (int) ((zone.getCurrentRightFloatBorder() - zone.getCurrentLeftFloatBorder()) / 7 + 1);

            return (int) ((playerFloatX - zone.getCurrentLeftFloatBorder()) / segment + 2);
        }
    }

    private int getPlayerRadarPositionZ(Player player) {

        int playerX = player.getLocation().getBlockX();
        double playerFloatX = player.getLocation().getX();
        int playerZ = player.getLocation().getBlockZ();
        double playerFloatZ = player.getLocation().getZ();

        Zone zone = Zone.getInstance();

        if (playerFloatZ - zone.getCurrentLowerBorder() < 0) {
            return 0;
        }

        if (playerFloatZ - zone.getCurrentUpperBorder() >= 0) {
            return 10;
        }

        if (playerFloatX < zone.getCurrentLeftFloatBorder() || playerFloatX >= zone.getCurrentRightFloatBorder()) {

            int segment = (int) ((zone.getCurrentUpperFloatBorder() - zone.getCurrentLowerFloatBorder()) / 9 + 1);

            return (int) ((playerFloatZ - zone.getCurrentLowerFloatBorder()) / segment + 1);
        }

        else if (GameVariables.zoneStage < ConfigVariables.zones.size()) {

            if (playerZ - zone.getNewLowerBorder() < 0) {
                return 2;
            }

            if (playerZ - zone.getNewUpperBorder() >= 0) {
                return 8;
            }

            if (playerX < zone.getNewLeftBorder() || playerX >= zone.getNewRightBorder()) {

                int segment = (zone.getNewUpperBorder() - zone.getNewLowerBorder()) / 5 + 1;

                return (playerZ - zone.getNewLowerBorder()) / segment + 3;
            }

            else {

                int segment = (zone.getNewUpperBorder() - zone.getNewLowerBorder()) / 3 + 1;

                return (playerZ - zone.getNewLowerBorder()) / segment + 4;
            }

        } else {

            int segment = (int) ((zone.getCurrentUpperFloatBorder() - zone.getCurrentLowerFloatBorder()) / 7 + 1);

            return (int) ((playerFloatZ - zone.getCurrentLowerFloatBorder()) / segment + 2);
        }
    }

    private char getPlayerDirectionChar(Player player) {

        int side = ((int) player.getLocation().getYaw());

        if (side >= -135 && side < -45) {
            return '>';
        }
        else if (side >= -45 && side < 45) {
            return 'V';
        }
        else if (side >= 45 && side < 135) {
            return '<';
        }
        else if (side >= 135 || side < -135) {
            return 'A';
        }

        return 'O';
    }
}
