package me.eastpenguin.cratemanager

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class MenuListener : Listener {

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory.holder !is PreviewGUI) return
        if (e.currentItem == null) return

        e.isCancelled = true

        val player = e.whoClicked as Player
        val shulker = CrateData.openShulkers[player.uniqueId] ?: return
        when (e.rawSlot) {
            39 -> {
                if (!player.isOp) return
                val key: ItemStack = shulker.inventory.getItem(9)?.clone() ?: return
                if (key.isEmpty) return
                key.amount = 64
                player.inventory.addItem(key)

            }
            40 -> { //exit
                player.inventory.close()
            }
            41 -> {
                if (!player.isOp) return
                player.inventory.close()
                player.openInventory(shulker.inventory)

            }
        }
    }
}