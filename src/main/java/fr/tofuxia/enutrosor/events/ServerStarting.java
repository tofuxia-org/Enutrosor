package fr.tofuxia.enutrosor.events;

import fr.tofuxia.enutrosor.Database;
import fr.tofuxia.enutrosor.Enutrosor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod.EventBusSubscriber(modid = Enutrosor.MODID)
public class ServerStarting {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        if(event.getServer().isSingleplayer()) return;
        Database.setLogger(Enutrosor.LOGGER);
        Database.connect();
        Database.prepare();
        if (!Database.isReady()) {
            Enutrosor.LOGGER.error(
                    "Database is not ready, stopping the server to prevent data loss/desync, please check the logs for more information.");
                    Enutrosor.LOGGER.warn("If this is the first time you are running the server, please check the config file.");
            event.getServer().stopServer();
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        /* NOTE: Currently disabled as all features are not yet implemented */

        // Enutrosor.LOGGER.info("Registering commands ...");
        // CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        // new CommandEnutrosor(dispatcher, event.getBuildContext());
        // Enutrosor.LOGGER.info("Commands registered ✅");
    }

}