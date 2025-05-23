package io.github.godoyjoao.server.customMob;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
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
import java.util.Map;

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
                        .then(Commands.argument("id", IntegerArgumentType.integer(1))
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
                                                                                                .executes(CustomMobCommand::createCustomMob)))))))))));



    }

    private static int createCustomMob(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }
        String id = ctx.getArgument("id", String.class);
        String path = id + ".";
        Double maxHealth = ctx.getArgument("max health", Double.class);
        String entityName = ctx.getArgument("name", String.class);
        EntitySelectorArgumentResolver entitySelectorArgumentResolver = ctx.getArgument("entity", EntitySelectorArgumentResolver.class);
        Entity entity = entitySelectorArgumentResolver.resolve(ctx.getSource()).getFirst();
        Location location = player.getLocation();
        config.set(path + "name", entityName);
        config.set(path + "maxHealth", maxHealth);
        config.set(path + "entityType", entity.getName());
        config.set(path + "world", player.getWorld().getName());
        config.set(path + "locationX", location.getX());
        config.set(path + "locationY", location.getY());
        config.set(path + "locationZ", location.getZ());
        

        return Command.SINGLE_SUCCESS;
    }

    private static int summonCustomMob(CommandContext<CommandSourceStack> ctx) {
        int id = IntegerArgumentType.getInteger(ctx, "id");
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Esse comando só pode ser executado por um jogador!");
            return Command.SINGLE_SUCCESS;
        }

        Location location = player.getLocation();
        World world = location.getWorld();
        Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);

        zombie.customName(Component.text("Zumbi", TextColor.color(200, 200, 50)));
        zombie.setCustomNameVisible(true);
        zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50.0);
        zombie.setHealth(50.0);
        EntityEquipment equipment = zombie.getEquipment();
        ItemStack diamondChestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta meta = diamondChestplate.getItemMeta();
        meta.addEnchant(Enchantment.UNBREAKING, 100, true);
        meta.addEnchant(Enchantment.PROTECTION, 100, true);
        meta.addEnchant(Enchantment.THORNS, 15, true);
        diamondChestplate.setItemMeta(meta);
        equipment.setHelmet(new ItemStack(Material.ACACIA_BOAT));
        equipment.setChestplate(diamondChestplate);
        equipment.setDropChance(EquipmentSlot.CHEST, 100);
        for (ItemStack item : equipment.getArmorContents()) {
            ctx.getSource().getSender().sendMessage(item.displayName());
        }

        return Command.SINGLE_SUCCESS;
    }
}
