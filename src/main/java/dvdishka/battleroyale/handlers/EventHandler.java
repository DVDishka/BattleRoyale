package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.common.Border;
import dvdishka.battleroyale.common.CommonVariables;
import io.papermc.paper.event.world.border.WorldBorderBoundsChangeFinishEvent;
import org.bukkit.event.Listener;

public class EventHandler implements Listener {

    /*@org.bukkit.event.EventHandler
    public void worldBorderBoundsChangeFinishEvent(WorldBorderBoundsChangeFinishEvent event) {

        int zoneStage = CommonVariables.getZoneStage();
        zoneStage++;
        CommonVariables.setZoneStage(zoneStage);

        if (zoneStage <= CommonVariables.zones.size()) {
           Border.setSize(CommonVariables.zones.get(zoneStage - 1),
                    CommonVariables.times.get(zoneStage - 1));
        }

        else if (zoneStage == CommonVariables.zones.size() + 1) {
            Border.setSize(1, CommonVariables.finalZoneTime);
        }
        return;
    }*/
}
