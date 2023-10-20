package ru.dvdishka.battleroyale.handlers;

import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.Zone;
import ru.dvdishka.battleroyale.common.Common;

public class Radar {

    private final Sidebar<Component> radar;

    private static Radar instance = null;

    private final TextColor safeZoneColor = NamedTextColor.DARK_GREEN;
    private final TextColor oldZoneColor = NamedTextColor.BLUE;
    private final TextColor movingZoneColor = NamedTextColor.RED;

    private Radar() {

        radar = ProtocolSidebar.newAdventureSidebar(
                Component.text("Radar")
                        .color(NamedTextColor.YELLOW)
                        .decorate(TextDecoration.BOLD),
                Common.plugin);

        radar.addUpdatableLine(player -> updateRadar(player, 0));

        radar.addUpdatableLine(player -> updateRadar(player, 1));

        radar.addUpdatableLine(player -> updateRadar(player, 2));

        radar.addUpdatableLine(player -> updateRadar(player, 3));

        radar.addUpdatableLine(player -> updateRadar(player, 4));

        radar.addUpdatableLine(player -> updateRadar(player, 5));

        radar.addUpdatableLine(player -> updateRadar(player, 6));

        radar.addUpdatableLine(player -> updateRadar(player, 7));

        radar.addUpdatableLine(player -> updateRadar(player, 8));

        radar.addUpdatableLine(player -> updateRadar(player, 9));

        radar.addUpdatableLine(player -> updateRadar(player, 10));

        radar.updateLinesPeriodically(10, 10);
    }

    public static Radar getInstance() {
        if (instance == null) {
            instance = new Radar();
        }
        return instance;
    }

    public void addViewer(Player player) {
        radar.addViewer(player);
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

        if (lineNumber == 1 || lineNumber == 9) {

            if (lineNumber == playerRadarPositionZ) {

                if (playerRadarPositionX == 0) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX > 0 && playerRadarPositionX < 10) {

                    component = component
                            .append(Component.text("=".repeat(playerRadarPositionX - 1))
                                    .color(movingZoneColor))
                            .append(playerSymbolComponent)
                            .append(Component.text("=".repeat(10 - playerRadarPositionX))
                                    .color(movingZoneColor));
                } else {

                    component = component.append(Component.text("=".repeat(9))
                            .color(movingZoneColor));
                }

                if (playerRadarPositionX == 10) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }
            } else {
                component = Component
                        .text("=")
                        .append(Component
                                .text("=".repeat(9))
                                .color(movingZoneColor))
                        .append(Component
                                .text("="));
            }
        }

        if (lineNumber == 2 || lineNumber == 8) {

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
                            .append(Component.text("=")
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
                            .append(Component.text("=")
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
                        .append(Component.text("=")
                                .color(movingZoneColor))
                        .append(Component.text("=".repeat(7)))
                        .append(Component.text("=")
                                .color(movingZoneColor))
                        .append(Component.text("="));
            }
        }

        if (lineNumber == 3 || lineNumber == 7) {

            if (playerRadarPositionZ == lineNumber) {

                if (playerRadarPositionX == 0) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX == 1) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("=")
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

                    component = component.append(Component.text("=")
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
                        .append(Component.text("=")
                                .color(movingZoneColor))
                        .append(Component.text("="))
                        .append(Component.text("=".repeat(5))
                                .color(safeZoneColor))
                        .append(Component.text("="))
                        .append(Component.text("=")
                                .color(movingZoneColor))
                        .append(Component.text("="));
            }
        }

        if (lineNumber == 4 || lineNumber == 5 || lineNumber == 6) {

            if (playerRadarPositionZ == lineNumber) {

                if (playerRadarPositionX == 0) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("="));
                }

                if (playerRadarPositionX == 1) {

                    component = component.append(playerSymbolComponent);

                } else {

                    component = component.append(Component.text("=")
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

                    component = component.append(Component.text("=")
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
                        .append(Component.text("=")
                                .color(movingZoneColor))
                        .append(Component.text("="))
                        .append(Component.text("=")
                                .color(safeZoneColor))
                        .append(Component.text("=".repeat(3)))
                        .append(Component.text("=")
                                .color(safeZoneColor))
                        .append(Component.text("="))
                        .append(Component.text("=")
                                .color(movingZoneColor))
                        .append(Component.text("="));
            }
        }

        return component;
    }

    private int getPlayerRadarPositionX(Player player) {

        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();
        Zone zone = Zone.getInstance();

        if (playerX - zone.getOldLeftBorder() < 0) {
            return 0;
        }

        if (playerX - zone.getOldRightBorder() >= 0) {
            return 10;
        }

        if (playerZ < zone.getOldLowerBorder() || playerZ >= zone.getOldUpperBorder()) {

            int segment = (zone.getOldRightBorder() - zone.getOldLeftBorder()) / 9 + 1;

            return (playerX - zone.getOldLeftBorder()) / segment + 1;
        }

        else {

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
        }
    }

    private int getPlayerRadarPositionZ(Player player) {

        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();
        Zone zone = Zone.getInstance();

        if (playerZ - zone.getOldLowerBorder() < 0) {
            return 10;
        }

        if (playerZ - zone.getOldUpperBorder() >= 0) {
            return 0;
        }

        if (playerX < zone.getOldLeftBorder() || playerX >= zone.getOldRightBorder()) {

            int segment = (zone.getOldUpperBorder() - zone.getOldLowerBorder()) / 9 + 1;

            return 10 - ((playerZ - zone.getOldLowerBorder()) / segment + 1);
        }

        else {

            if (playerZ - zone.getNewLowerBorder() < 0) {
                return 8;
            }

            if (playerZ - zone.getNewUpperBorder() >= 0) {
                return 2;
            }

            if (playerX < zone.getNewLeftBorder() || playerX >= zone.getNewRightBorder()) {

                int segment = (zone.getNewUpperBorder() - zone.getNewLowerBorder()) / 5 + 1;

                return 8 - ((playerZ - zone.getNewLowerBorder()) / segment + 1);
            }

            else {

                int segment = (zone.getNewUpperBorder() - zone.getNewLowerBorder()) / 3 + 1;

                return 7 - ((playerZ - zone.getNewLowerBorder()) / segment + 1);
            }
        }
    }

    private char getPlayerDirectionChar(Player player) {

        int side = ((int) player.getLocation().getYaw());

        if (side >= -135 && side < -45) {
            return '>';
        }
        else if (side >= -45 && side < 45) {
            return 'A';
        }
        else if (side >= 45 && side < 135) {
            return '<';
        }
        else if (side >= 135 || side < -135) {
            return 'V';
        }

        return 'O';
    }
}
