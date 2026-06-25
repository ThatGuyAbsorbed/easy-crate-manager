package me.eastpenguin.cratemanager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType


class weightcommand() : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return false

        if (!sender.isOp) {
            sender.sendMessage(Component.text("you do not have permision to use this command", NamedTextColor.RED))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage("/setweight <weight>")
            return true
        }
        val weight = args[0].toIntOrNull()

        if (weight == null || weight <= 0) {
            sender.sendMessage(Component.text("Invalid weight.", NamedTextColor.RED))
            return true
        }

        val item = sender.inventory.itemInMainHand

        if (item.type.isAir) {
            sender.sendMessage(Component.text("you need an item in your hand to run this command", NamedTextColor.RED))
            return true
        }

        val meta = item.itemMeta

        meta.persistentDataContainer.set(
            weightKey,
            PersistentDataType.INTEGER,
            weight
        )

        item.itemMeta = meta

        sender.sendMessage("Set weight to $weight")

        return true


    }
}