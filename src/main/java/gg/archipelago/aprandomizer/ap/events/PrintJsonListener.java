package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.Print.APPrintJsonType;
import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.PrintJSONEvent;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;

public class PrintJsonListener {

    @ArchipelagoEventListener
    public void onPrintJson(PrintJSONEvent event) {
        // Don't print chat messages originating from ourselves.
        if (event.type == APPrintJsonType.Chat)
            if (event.apPrint.slot == APRandomizer.getAP().getSlot())
                return;

        Utils.sendFancyMessageToAll(event.apPrint);
    }
}