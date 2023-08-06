package pro.mikey.ccc.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import pro.mikey.ccc.data.PlayerData;
import pro.mikey.ccc.struct.Location;

import java.util.HashMap;
import java.util.LinkedList;

public class HomeCommand implements Command {
    private static final DynamicCommandExceptionType DIMENSION_MISMATCH = new DynamicCommandExceptionType(obj -> Component.translatable("ccc.commands.response.home.level_missing", obj));
    private static final DynamicCommandExceptionType HOME_MISSING = new DynamicCommandExceptionType(obj -> Component.translatable("ccc.commands.response.home.missing", obj));

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("home")
                .then(goHomeCommand("go"))
                .then(Commands.literal("add")
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(ctx -> this.addHome(ctx, StringArgumentType.getString(ctx, "name")))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .suggests(homeSuggestionProvider())
                                .executes(ctx -> this.removeHome(ctx, StringArgumentType.getString(ctx, "name")))))
                // TODO: Update
                .then(Commands.literal("list").executes(this::listHomes))
                .then(Commands.literal("clear-all").executes(this::clearAllHomes));
    }

    private int clearAllHomes(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrException();
        PlayerData.get(player).homes().clear();
        PlayerData.get(player).save(player);

        ctx.getSource().sendSuccess(() -> Component.translatable("ccc.commands.feedback.home.clear_all"), false);

        return 0;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> goHomeCommand(String name) {
        return Commands.literal(name)
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests(homeSuggestionProvider())
                        .executes(ctx -> goHome(ctx, StringArgumentType.getString(ctx, "name"))));
    }

    private static int goHome(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrException();
        var home = findHome(player, name);

        var result = home.teleportTo(player);
        if (!result) {
            throw DIMENSION_MISMATCH.create(home.pos().dimension().location());
        }

        ctx.getSource().sendSuccess(() -> Component.translatable("ccc.commands.feedback.home.teleport", home.name()), false);
        return 0;
    }

    private int addHome(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrException();
        var newHome = Location.create(name, player);

        PlayerData playerData = PlayerData.get(player);
        playerData.addHome(newHome);
        playerData.save(player);

        ctx.getSource().sendSuccess(() -> Component.translatable("ccc.commands.feedback.home.added", newHome.name()), false);

        return 0;
    }

    private int removeHome(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrException();

        PlayerData playerData = PlayerData.get(player);
        LinkedList<Location> homes = playerData.homes();
        var home = homes.stream().filter(h -> h.name().equalsIgnoreCase(name.toLowerCase())).findFirst();
        if (home.isEmpty()) {
            ctx.getSource().sendFailure(Component.translatable("ccc.commands.response.home.missing", name));
            return -1;
        }

        Location foundHome = home.get();
        homes.remove(foundHome);
        playerData.save(player);

        ctx.getSource().sendSuccess(() -> Component.translatable("ccc.commands.feedback.home.remove", foundHome.name()), false);

        return 0;
    }

    private int listHomes(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrException();
        var homes = PlayerData.get(player).homes();

        if (homes.isEmpty()) {
            ctx.getSource().sendFailure(Component.translatable("ccc.commands.feedback.home.no_homes"));
            return -1;
        }

        ctx.getSource().sendSuccess(() -> Component.translatable("ccc.generic.homes", homes.size()), false);

        // Create a map of dimensions to homes
        var dimensionMap = new HashMap<ResourceLocation, LinkedList<Location>>();
        homes.forEach(home -> {
            var dimensionHomes = dimensionMap.getOrDefault(home.pos().dimension().location(), new LinkedList<>());
            dimensionHomes.add(home);
            dimensionMap.put(home.pos().dimension().location(), dimensionHomes);
        });

        dimensionMap.forEach((dim, innerHomes) -> {
            ctx.getSource().sendSuccess(() -> Component.literal("%s/%s".formatted(dim.getNamespace(), dim.getPath())).withStyle(ChatFormatting.GOLD), false);
            innerHomes.forEach(home -> {
                BlockPos location = home.pos().pos();

                var component = Component.literal("");
                component.append(Component.literal(home.name()).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ccc home go %s".formatted(home.name())))));
                component.append(Component.literal(" ("));
                component.append(Component.literal(location.toShortString()).withStyle(Style.EMPTY.withUnderlined(true).withColor(ChatFormatting.GRAY).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp %s %s %s %s".formatted(player.getName().getString(), location.getX(), location.getY(), location.getZ())))));
                component.append(Component.literal(")"));

                ctx.getSource().sendSuccess(() -> component, false);
            });
        });

        return 0;
    }

    private static Location findHome(ServerPlayer player, String homeName) throws CommandSyntaxException {
        var home = PlayerData.get(player).getHome(homeName);
        if (home.isEmpty()) {
            throw HOME_MISSING.create(homeName);
        }

        return home.get();
    }

    public static SuggestionProvider<CommandSourceStack> homeSuggestionProvider() {
        return (ctx, builder) -> {
            SharedSuggestionProvider.suggest(PlayerData
                    .get(ctx.getSource().getPlayerOrException())
                    .homes()
                    .stream()
                    .map(Location::name), builder
            );

            return builder.buildFuture();
        };
    }

    public static class GoHomeShortcutCommand implements Command {
        @Override
        public LiteralArgumentBuilder<CommandSourceStack> register() {
            return goHomeCommand("gohome");
        }
    }
}
