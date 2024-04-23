package fr.tofuxia.enutrosor.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import fr.tofuxia.enutrosor.Enutrosor;
import fr.tofuxia.enutrosor.api.API;
import fr.tofuxia.enutrosor.api.Owner;
import net.minecraft.world.entity.player.Player;

public class OwnerArgument implements ArgumentType<Owner> {

    @Override
    public Owner parse(StringReader reader) throws CommandSyntaxException {
        try {
            UUID uuid = UUID.fromString(reader.readUnquotedString());
            Optional<API> apiOpt = Enutrosor.getAPI();
            if (apiOpt.isEmpty()) {
                Enutrosor.LOGGER.error("❌ API is not initialized in UUIDArgumentType.parse");
                return null;
            }
            API api = apiOpt.get();
            Owner owner = api.getOrCreateOwner(uuid);
            return owner;
        } catch (IllegalArgumentException e) {
            throw new CommandSyntaxException(uuidException, new Message() {
                @Override
                public String getString() {
                    return "Invalid UUID";
                }
            });
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if(context.getSource() instanceof Player) {
            Player player = (Player) context.getSource();
            player.getServer().getPlayerList().getPlayers().forEach((p) -> {
                builder.suggest(p.getUUID().toString());
            });
            return builder.buildFuture();
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList(UUID.randomUUID().toString());
    }

    public static OwnerArgument owner() {
        return new OwnerArgument();
    }

    public static Owner getOwner(CommandContext<?> context, String name) {
        Enutrosor.LOGGER.info("Getting owner from context");
        Enutrosor.LOGGER.info("Name: " + name);
        Enutrosor.LOGGER.info("Context: " + context);
        Enutrosor.LOGGER.info("Owner: " + context.getArgument(name, Owner.class));
        return context.getArgument(name, Owner.class);
    }

    public static UUIDCommandExceptionType uuidException = new UUIDCommandExceptionType();
    public static class UUIDCommandExceptionType implements CommandExceptionType {}
    
}
