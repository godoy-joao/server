package io.github.godoyjoao.server.customMob;

import com.destroystokyo.paper.Namespaced;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.*;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomMobCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("customMob")
                .then(Commands.argument("id", IntegerArgumentType.integer(1))
                        .executes(CustomMobCommand::createCustomMob)
                );
    }

    private static int createCustomMob(CommandContext<CommandSourceStack> ctx) {
        int id = IntegerArgumentType.getInteger(ctx, "id");
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Esse comando só pode ser executado por um jogador!");
            return Command.SINGLE_SUCCESS;
        }

        Location location = player.getLocation();
        World world = location.getWorld();
        Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);

        zombie.customName(Component.text("Zumbi",TextColor.color(200,200,50)));
        zombie.setCustomNameVisible(true);
        zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50.0);
        zombie.setHealth(50.0);
        EntityEquipment equipment = zombie.getEquipment();
        ItemStack diamondChestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta meta = diamondChestplate.getItemMeta();
        meta.addEnchant(Enchantment.UNBREAKING, 100, true);
        meta.addEnchant(Enchantment.PROTECTION, 10, true);
        meta.addEnchant(Enchantment.THORNS, 15, true);
        diamondChestplate.setItemMeta(meta);
        equipment.setChestplate(diamondChestplate);
        for (ItemStack item : equipment.getArmorContents()) {
            ctx.getSource().getSender().sendMessage(item.displayName());
        }

        return Command.SINGLE_SUCCESS;
    }
}
