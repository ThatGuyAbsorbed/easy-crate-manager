package me.eastpenguin.cratemanager

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.ShulkerBox
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class CrateInteractListener : Listener {

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.block.state !is ShulkerBox) return
        val holo = activeCrates.remove(e.block.location) ?: return
        holo.remove()
    }

    @EventHandler
    fun onPunch(e: PlayerInteractEvent) {


        val block = e.clickedBlock ?: return
        val player = e.player
        val mainhand = player.inventory.itemInMainHand

        if (block.state !is ShulkerBox) return
        val meta = block.state as ShulkerBox
        val s_inv = meta.inventory
        if (s_inv.getItem(0)?.type != Material.COMMAND_BLOCK_MINECART) {
            val holo = activeCrates.remove(block.location) ?: return
            holo.remove()
            return
        }

        if (s_inv.getItem(18)?.type == Material.PAPER) {
            handleCrate(meta,block.location)
        }
        else {
            val holo = activeCrates.remove(block.location)
            holo?.remove()
        }

        if (e.action == Action.LEFT_CLICK_BLOCK) {
            player.playSound(player.location, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1f, 1f)
            PreviewGUI(player, meta)

        }
        else if (e.action == Action.RIGHT_CLICK_BLOCK) {

            val key = s_inv.getItem(9)
            if (key != null && !key.isEmpty) {
                if (mainhand.isSimilar(key)) {
                    player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                    block.world.spawnParticle(
                        Particle.FIREWORK,
                        block.location.clone().add(0.5,0.5,0.5),
                        30,
                        0.5, 0.0, 0.5,
                        0.2
                    );
                    mainhand.amount -= 1
                    val rewards = getRewards(s_inv)
                    val reward = rollReward(rewards)
                    player.inventory.addItem(cleanRewardItem(reward.item))
                }
                else player.sendMessage(Component.text("You need a valid crate key to open this.").color(NamedTextColor.RED))
            }
            else player.sendMessage(Component.text("You need a valid crate key to open this.").color(NamedTextColor.RED))

        }
        e.isCancelled = true

    }

    fun getRewards(inventory: Inventory): MutableList<CrateReward> {
        val rewards = mutableListOf<CrateReward>()

        for (row in 0 until 3) {
            for (col in 1..7) {

                val slot = row * 9 + col
                val item = inventory.getItem(slot) ?: continue

                if (item.type.isAir) continue

                rewards += CrateReward(
                    item = item.clone(),
                    chance = getWeight(item).toDouble()
                )
            }
        }
        return rewards
    }

    fun rollReward(rewards: List<CrateReward>): CrateReward {
        val totalWeight = rewards.sumOf { it.chance }

        var random = Math.random() * totalWeight

        for (reward in rewards) {
            random -= reward.chance

            if (random <= 0) {
                return reward
            }
        }

        return rewards.last()
    }

    fun cleanRewardItem(item: ItemStack): ItemStack {

        val clone = item.clone()

        val meta = clone.itemMeta
        val container = meta.persistentDataContainer

        container.remove(weightKey)

        clone.itemMeta = meta

        return clone
    }

}