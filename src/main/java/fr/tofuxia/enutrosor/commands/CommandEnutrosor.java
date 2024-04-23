package fr.tofuxia.enutrosor.commands;

import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.tofuxia.enutrosor.Enutrosor;
import fr.tofuxia.enutrosor.api.API;
import fr.tofuxia.enutrosor.api.Owner;
import fr.tofuxia.enutrosor.api.OwnerTypePair;
import fr.tofuxia.enutrosor.api.TransactionReason;
import fr.tofuxia.enutrosor.api.Wallet;
import fr.tofuxia.enutrosor.api.WalletType;
import fr.tofuxia.enutrosor.commands.arguments.OwnerArgument;
import fr.tofuxia.enutrosor.commands.arguments.TransactionReasonArgument;
import fr.tofuxia.enutrosor.commands.arguments.WalletTypeArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandEnutrosor extends Command {
    
    public CommandEnutrosor(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        super(dispatcher, context);
    }

    @Override
    String getCommandName() {
        return "enutrosor";
    }

    @Override
    void buildCommand(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext context) {

        /**
         * enutrosor
         *  - wallet
         *   - create [owner] [type]
         *   - delete [owner] [type]
         *   - balance [owner] [type]
         *   - list [owner]
         *  - transaction
         *   - create [ownerFrom] [fromType] [ownerTo] [toType] [amount] [reason]
         *   - list [owner] [type]
         */

        literal
        .then(Commands.literal("wallet") // MARK: Wallet
            .then(Commands.literal("create") // MARK: Create wallet
                .then(Commands.argument("owner", OwnerArgument.owner())
                    .then(Commands.argument("type", WalletTypeArgument.walletType())
                        .executes(ctx -> {
                            Owner owner = OwnerArgument.getOwner(ctx, "owner");
                            WalletType walletType = WalletTypeArgument.getWalletType(ctx, "type");
                            OwnerTypePair otp = new OwnerTypePair(owner, walletType);
                            Enutrosor.LOGGER.info("Creating wallet for " + otp);
                            Optional<API> apiOpt = Enutrosor.getAPI();
                            if (apiOpt.isEmpty()) {
                                Enutrosor.LOGGER.error("❌ API is not initialized in CommandEnutrosor.buildCommand");
                                ctx.getSource().sendFailure(Component.literal("API is not initialized"));
                                return 0;
                            }
                            API api = apiOpt.get();
                            Wallet wallet = api.getOrCreateWallet(owner, walletType).get();
                            ctx.getSource().sendSuccess(() -> Component.literal("Wallet created: " + wallet), false);
                            return 1;
                        })
                    )
                )
            )
            .then(Commands.literal("delete") // MARK: Delete wallet
                .then(Commands.argument("owner", OwnerArgument.owner())
                    .then(Commands.argument("type", WalletTypeArgument.walletType())
                        .executes(ctx -> {
                            Owner owner = OwnerArgument.getOwner(ctx, "owner");
                            WalletType walletType = WalletTypeArgument.getWalletType(ctx, "type");
                            OwnerTypePair otp = new OwnerTypePair(owner, walletType);
                            Enutrosor.LOGGER.info("Deleting wallet for " + otp);
                            return 1;
                        })
                    )
                )
            )
            .then(Commands.literal("balance") // MARK: Get balance
                .then(Commands.argument("owner", OwnerArgument.owner())
                    .then(Commands.argument("type", WalletTypeArgument.walletType())
                        .executes(ctx -> {
                            Owner owner = OwnerArgument.getOwner(ctx, "owner");
                            WalletType walletType = WalletTypeArgument.getWalletType(ctx, "type");
                            OwnerTypePair otp = new OwnerTypePair(owner, walletType);
                            Enutrosor.LOGGER.info("Getting balance for " + otp);
                            return 1;
                        })
                    )
                )
            )
            .then(Commands.literal("list") // MARK: List wallets
                .then(Commands.argument("owner", OwnerArgument.owner())
                    .executes(ctx -> {
                        Optional<API> apiOpt = Enutrosor.getAPI();
                        if (apiOpt.isEmpty()) {
                            Enutrosor.LOGGER.error("❌ API is not initialized in CommandEnutrosor.buildCommand");
                            ctx.getSource().sendFailure(Component.literal("API is not initialized"));
                            return 0;
                        }
                        Owner owner = OwnerArgument.getOwner(ctx, "owner");
                        API api = apiOpt.get();
                        Enutrosor.LOGGER.info("Listing wallets for " + owner);
                        api.getWalletsWhereOwner(owner).forEach(wallet -> {
                            ctx.getSource().sendSuccess(() -> Component.literal(wallet.toString()), false);
                        });
                        return 1;
                    })
                )
            )
        )
        .then(Commands.literal("transaction") // MARK: Transaction
            .then(Commands.literal("create") // MARK: Create transaction
                .then(Commands.argument("ownerFrom", OwnerArgument.owner())
                    .then(Commands.argument("fromType", WalletTypeArgument.walletType())
                        .then(Commands.argument("ownerTo", OwnerArgument.owner())
                            .then(Commands.argument("toType", WalletTypeArgument.walletType())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                    .then(Commands.argument("reason", TransactionReasonArgument.transactionReason())
                                        .executes(ctx -> {
                                            Owner ownerFrom = OwnerArgument.getOwner(ctx, "ownerFrom");
                                            WalletType fromType = WalletTypeArgument.getWalletType(ctx, "fromType");
                                            Owner ownerTo = OwnerArgument.getOwner(ctx, "ownerTo");
                                            WalletType toType = WalletTypeArgument.getWalletType(ctx, "toType");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            TransactionReason reason = TransactionReasonArgument.getTransactionReason(ctx, "reason");
                                            OwnerTypePair otpFrom = new OwnerTypePair(ownerFrom, fromType);
                                            OwnerTypePair otpTo = new OwnerTypePair(ownerTo, toType);
                                            Enutrosor.LOGGER.info("Creating transaction from " + otpFrom + " to " + otpTo + " with amount " + amount + " and reason " + reason);
                                            return 1;
                                        })
                                    )
                                )
                            )
                        )
                    )
                )
            )
            .then(Commands.literal("list") // MARK: List transactions
                .then(Commands.argument("owner", OwnerArgument.owner())
                    .then(Commands.argument("type", WalletTypeArgument.walletType())
                        .executes(ctx -> {
                            Owner owner = OwnerArgument.getOwner(ctx, "owner");
                            WalletType walletType = WalletTypeArgument.getWalletType(ctx, "type");
                            OwnerTypePair otp = new OwnerTypePair(owner, walletType);
                            Enutrosor.LOGGER.info("Listing transactions for " + otp);
                            return 1;
                        })
                    )
                )
            )
        );
    }
}
