package fr.tofuxia.enutrosor.commands.arguments;

import java.util.Arrays;
import java.util.Collection;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.tofuxia.enutrosor.api.TransactionReason;
import net.minecraft.commands.CommandSourceStack;

public class TransactionReasonArgument implements ArgumentType<TransactionReason> {

    @Override
    public TransactionReason parse(StringReader reader) throws CommandSyntaxException {
        String reason = reader.readUnquotedString();
        return TransactionReason.valueOf(reason);
    }

    @Override
    public Collection<String> getExamples() {
        TransactionReason[] reasons = TransactionReason.values();
        String[] examples = new String[reasons.length];
        for (int i = 0; i < reasons.length; i++) {
            examples[i] = reasons[i].name();
        }
        return Arrays.asList(examples);
    }

    public static TransactionReasonArgument transactionReason() {
        return new TransactionReasonArgument();
    }

    public static TransactionReason getTransactionReason(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, TransactionReason.class);
    }

}
