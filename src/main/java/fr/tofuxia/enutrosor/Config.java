package fr.tofuxia.enutrosor;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = Enutrosor.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        // Database Config
        private static final ModConfigSpec.ConfigValue<String> JBDC_URL = BUILDER
                        .comment("The url of the database in the format jdbc:mysql://localhost:3306/")
                        .define("jdbcUrl", "jdbc:mysql://localhost:3306/");

        private static final ModConfigSpec.ConfigValue<String> USERNAME = BUILDER
                        .comment("The username of the database")
                        .define("username", "root");

        private static final ModConfigSpec.ConfigValue<String> PASSWORD = BUILDER
                        .comment("The password of the database")
                        .define("password", "p4$$w0rd");

        static final ModConfigSpec SPEC = BUILDER.build();

        // Database Config
        public static String jdbcUrl;
        public static String username;
        public static String password;

        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {
                // Database Config
                jdbcUrl = JBDC_URL.get();
                username = USERNAME.get();
                password = PASSWORD.get();
        }

}
