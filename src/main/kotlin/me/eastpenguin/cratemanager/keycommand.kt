package me.eastpenguin.cratemanager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType


class keycommand() : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if (sender !is Player) return false
        if (!sender.isOp) {
            sender.sendMessage(Component.text("you do not have permision to use this command", NamedTextColor.RED))
            return false
        }
        val item = sender.inventory.itemInMainHand
        if (item.type.isAir) {
            sender.sendMessage(Component.text("you need an item in your hand to run this command", NamedTextColor.RED))
            return false
        }

        val key = NamespacedKey("cratemanager", "cratekey")

        item.editMeta { meta ->
            val container = meta.persistentDataContainer

            container.set(key, PersistentDataType.BYTE, 1.toByte())
            meta.lore(listOf(
                Component.text(""),
                Component.text("use this on a crate to open it", NamedTextColor.GRAY),
                Component.text("")
            ))
            meta.setEnchantmentGlintOverride(true)
        }

        sender.sendMessage(Component.text("Succsessfully turned item into a crate key.").color(NamedTextColor.GREEN))

        return false
    }
}