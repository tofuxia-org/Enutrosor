package fr.tofuxia.enutrosor;

import java.util.Optional;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import fr.tofuxia.enutrosor.api.API;
import fr.tofuxia.enutrosor.commands.arguments.OwnerArgument;
import fr.tofuxia.enutrosor.commands.arguments.TransactionReasonArgument;
import fr.tofuxia.enutrosor.commands.arguments.WalletTypeArgument;
import fr.tofuxia.enutrosor.impl.APIImpl;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Enutrosor.MODID)
public class Enutrosor {
    
    public static final String MODID = "enutrosor";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static API api;

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister
            .create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, MODID);
    static {
        ARGUMENT_TYPES.register("wallet_type", () -> ArgumentTypeInfos.registerByClass(WalletTypeArgument.class, SingletonArgumentInfo.contextFree(WalletTypeArgument::new)));
        ARGUMENT_TYPES.register("owner", () -> ArgumentTypeInfos.registerByClass(OwnerArgument.class, SingletonArgumentInfo.contextFree(OwnerArgument::new)));
        ARGUMENT_TYPES.register("transaction_reason", () -> ArgumentTypeInfos.registerByClass(TransactionReasonArgument.class, SingletonArgumentInfo.contextFree(TransactionReasonArgument::new)));
    }

    public Enutrosor(IEventBus modEventBus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        Enutrosor.setAPI(new APIImpl());
        ARGUMENT_TYPES.register(modEventBus);
    }

    public static Optional<API> getAPI() {
        if(api == null) {
            LOGGER.error("❌ API is not initialized");
            return Optional.empty();
        }
        return Optional.of(api);
    }

    public static void setAPI(API api) {
        Enutrosor.api = api;
    }

}
