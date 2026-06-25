package me.eastpenguin.cratemanager

import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.persistence.PersistentDataType

class ChunkLoadListener : Listener {

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {

        for (entity in event.chunk.entities) {

            if (entity !is TextDisplay) continue

            val data = entity.persistentDataContainer.get(
                hologramkey,
                PersistentDataType.STRING
            ) ?: continue

            val loc = deserializeLocation(data) ?: continue

            activeCrates[loc] = entity
        }

    }
}