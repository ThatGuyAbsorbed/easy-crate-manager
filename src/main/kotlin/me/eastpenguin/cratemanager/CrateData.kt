package me.eastpenguin.cratemanager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

val weightKey = NamespacedKey("cratemanager", "crate_weight")

val hologramkey = NamespacedKey("cratemanager", "crate_hologram")

val activeCrates = mutableMapOf<Location, TextDisplay>()

object CrateData {
    val openShulkers = mutableMapOf<UUID, ShulkerBox>()
}

data class CrateReward(
    val item: ItemStack,
    val chance: Double
)

fun getWeight(item: ItemStack): Int {
    val meta = item.itemMeta ?: return 1

    return meta.persistentDataContainer.get(
        weightKey,
        PersistentDataType.INTEGER
    ) ?: 1
}

fun addChanceLore(
    item: ItemStack,
    weight: Int,
    total: Int
): ItemStack {

    val clone = item.clone()
    val meta = clone.itemMeta

    val percent = (weight.toDouble() / total.toDouble()) * 100.0

    val lore = meta.lore()?.toMutableList() ?: mutableListOf()
    lore.add(Component.text(""))
    lore.add(Component.text("Chance: %.2f%%".format(percent)).color(NamedTextColor.GRAY))
    lore.add(Component.text(""))

    meta.lore(lore)
    clone.itemMeta = meta

    return clone
}

fun spawnHologram(location: Location, text: Component): TextDisplay {

    val world = location.world ?: throw IllegalStateException("World is null")

    val hologramLocation = location.clone().add(0.5, 1.8, 0.5)

    val display = world.spawnEntity(
        hologramLocation,
        EntityType.TEXT_DISPLAY
    ) as TextDisplay

    display.text(text)

    display.billboard = Display.Billboard.CENTER
    display.isShadowed = false
    display.isSeeThrough = true
    display.isPersistent = true

    display.persistentDataContainer.set(
        hologramkey,
        PersistentDataType.STRING,
        serializeLocation(location)
    )

    return display
}

fun getHologramText(shulker: ShulkerBox): Component {

    val item = shulker.inventory.getItem(18)
        ?: return Component.text("Crate")

    if (item.type != Material.PAPER) {
        return Component.text("Crate")
    }

    val meta = item.itemMeta
        ?: return Component.text("Crate")

    return meta.displayName() ?: Component.text("Crate")
}

fun handleCrate(shulker: ShulkerBox, location: Location) {

    val newText = getHologramText(shulker)

    val existing = activeCrates[location]

    // CASE 1: no hologram yet → create it
    if (existing == null) {
        activeCrates[location] = spawnHologram(location, newText)
        return
    }

    // CASE 2: hologram exists → check if update needed
    val currentText = existing.text()

    if (currentText != newText) {
        existing.text(newText)
    }
}

fun serializeLocation(location: Location): String {

    val block = location.toBlockLocation()

    return "${block.world.name}:${block.blockX}:${block.blockY}:${block.blockZ}"
}

fun deserializeLocation(data: String): Location? {

    val split = data.split(":")
    if (split.size != 4) return null

    val world = Bukkit.getWorld(split[0]) ?: return null

    return Location(
        world,
        split[1].toInt().toDouble(),
        split[2].toInt().toDouble(),
        split[3].toInt().toDouble()
    ).toBlockLocation()
}