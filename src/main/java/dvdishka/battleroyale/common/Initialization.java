package dvdishka.battleroyale.common;

public class Initialization {

    public static void checkDependencies() {

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            CommonVariables.isFolia = true;
            Logger.getLogger().devLog("Folia has been detected!");
        } catch (Exception e) {
            CommonVariables.isFolia = false;
            Logger.getLogger().devLog("Folia has not been detected!");
        }
    }
}
