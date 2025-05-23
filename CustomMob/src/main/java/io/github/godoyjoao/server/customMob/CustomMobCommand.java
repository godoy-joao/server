package io.github.godoyjoao.server.customMob;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("@ApiStatus.Experimental")
public class CustomMobCommand {

    private static JavaPlugin plugin;
    private static File file;
    private static FileConfiguration config;

    public CustomMobCommand(JavaPlugin plugin) {
        CustomMobCommand.plugin = plugin;
        initialize();
    }

    public static void initialize() {
        file = new File(plugin.getDataFolder(), "custom-mob.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("customMob")
                .then(Commands.literal("summon")
                        .then(Commands.argument("id", StringArgumentType.string())
                                .executes(CustomMobCommand::summonCustomMob)))
                .then(Commands.literal("add")
                        .then(Commands.argument("id", StringArgumentType.string())
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .then(Commands.argument("entity", ArgumentTypes.entity())
                                                .then(Commands.argument("max health", DoubleArgumentType.doubleArg())
                                                        .then(Commands.argument("hand", ArgumentTypes.itemStack())
                                                                .then(Commands.argument("helmet", ArgumentTypes.itemStack())
                                                                        .then(Commands.argument("chest", ArgumentTypes.itemStack())
                                                                                .then(Commands.argument("legs", ArgumentTypes.itemStack())
                                                                                        .then(Commands.argument("boots", ArgumentTypes.itemStack())
                                                                                                .then(Commands.argument("location", ArgumentTypes.blockPosition())
                                                                                                        .then(Commands.argument("world", ArgumentTypes.world())
                                                                                                                .then(Commands.argument("level", IntegerArgumentType.integer())
                                                                                                                        .executes(CustomMobCommand::createCustomMob))))))))))))));


    }

    private static int createCustomMob(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }
        String id = ctx.getArgument("id", String.class);
        String path = id + ".";

        Double maxHealth = ctx.getArgument("max health", Double.class);

        Integer level = ctx.getArgument("level", Integer.class);

        String entityName = ctx.getArgument("name", String.class);

        EntitySelectorArgumentResolver entitySelectorArgumentResolver = ctx.getArgument("entity", EntitySelectorArgumentResolver.class);
        Entity entity = entitySelectorArgumentResolver.resolve(ctx.getSource()).getFirst();

        ItemStack hand = ctx.getArgument("hand", ItemStack.class);
        ItemStack helmet = ctx.getArgument("helmet", ItemStack.class);
        ItemStack chest = ctx.getArgument("chest", ItemStack.class);
        ItemStack legs = ctx.getArgument("legs", ItemStack.class);
        ItemStack boots = ctx.getArgument("boots", ItemStack.class);

        World world = ctx.getArgument("world", World.class);

        BlockPositionResolver blockPositionResolver = ctx.getArgument("location", BlockPositionResolver.class);
        BlockPosition blockPosition = blockPositionResolver.resolve(ctx.getSource());


        config.set(path + "name", entityName);
        config.set(path + "maxHealth", maxHealth);
        config.set(path + "entityType", entity.getName());
        config.set(path + "world", world.getName());
        config.set(path + "location.X", blockPosition.x());
        config.set(path + "location.Y", blockPosition.y());
        config.set(path + "location.Z", blockPosition.z());



        return Command.SINGLE_SUCCESS;
    }


    private static int summonCustomMob(CommandContext<CommandSourceStack> ctx) {
        String id = StringArgumentType.getString(ctx, "id");
        CommandSender sender = ctx.getSource().getSender();
        Set<String> idsValidos = config.getKeys(false);
        if (idsValidos.isEmpty()) {
            sender.sendMessage("Não há mobs para listar.");
        }


        return Command.SINGLE_SUCCESS;
    }
}
