package me.eastpenguin.cratemanager

import org.bukkit.Bukkit
import org.bukkit.entity.TextDisplay
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin


class Cratemanager : JavaPlugin() {

    override fun onEnable() {
        getCommand("setcratekey")!!.setExecutor(keycommand())
        getCommand("setweight")!!.setExecutor(weightcommand())
        Bukkit.getPluginManager().registerEvents(MenuListener(),this)
        Bukkit.getPluginManager().registerEvents(CrateInteractListener(),this)
        Bukkit.getPluginManager().registerEvents(ChunkLoadListener(), this)

    }


}
