package ru.dvdishka.battleroyale.common;

import java.util.Arrays;
import java.util.List;

public class ConfigVariables {

    public static List<Integer> zones = Arrays.asList(2000, 1500, 1000, 700, 500, 200);
    public static List<Integer> times = Arrays.asList(1500, 1200, 900, 600, 300, 200);
    public static int defaultWorldBorderDiameter = 10000;
    public static int timeOut = 600;
    public static int finalZoneDiameter = 100;
    public static int finalZoneTimeOut = 200;
    public static int finalZoneDuration = 120;
    public static int finalZoneMoveDuration = 120;
    public static int minFinalZoneMove = 50;
    public static int maxFinalZoneMove = 100;
    public static int zoneMoveTimeOut = 10;
    public static int startBoxX = 0;
    public static int startBoxY = 200;
    public static int startBoxZ = 0;

    public static boolean betterLogging = false;
}
