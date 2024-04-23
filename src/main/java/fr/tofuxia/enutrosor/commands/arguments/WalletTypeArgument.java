package fr.tofuxia.enutrosor.commands.arguments;

import java.util.Arrays;
import java.util.Collection;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.tofuxia.enutrosor.api.WalletType;
import net.minecraft.commands.CommandSourceStack;

public class WalletTypeArgument implements ArgumentType<WalletType> {

    @Override
    public WalletType parse(StringReader reader) throws CommandSyntaxException {
        String type = reader.readUnquotedString();
        return WalletType.valueOf(type);
    }

    @Override
    public Collection<String> getExamples() {
        WalletType[] types = WalletType.values();
        String[] examples = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            examples[i] = types[i].name();
        }
        return Arrays.asList(examples);
    }

    public static WalletTypeArgument walletType() {
        return new WalletTypeArgument();
    }

    public static WalletType getWalletType(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, WalletType.class);
    }

}
