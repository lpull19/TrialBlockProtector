package org.fstpdev.trialprotection;

import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class TrialBlockProtector extends JavaPlugin implements Listener {

    private LandsIntegration lands;

    @Override
    public void onEnable() {

        lands = LandsIntegration.of(this);


        if (lands == null) {
            getLogger().severe("Lands plugin not found or failed to integrate. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("TrialBlockProtector loaded successfully.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();

        if (type != Material.TRIAL_SPAWNER && type != Material.VAULT) return;

        LandWorld landWorld = lands.getWorld(event.getBlock().getWorld());
        if (landWorld == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot break Trial Vaults or Spawners.");
            return;
        }

        // Cancel if block is in wilderness (unclaimed area)
        if (landWorld.getArea(event.getBlock().getLocation()) == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot break Trial Vaults or Spawners.");
            return;
        }

        // Cancel if player lacks BLOCK_BREAK permission for this material at this location
        boolean canBreak = landWorld.hasFlag(
                event.getPlayer(),
                event.getBlock().getLocation(),
                type,
                Flags.BLOCK_BREAK,
                false
        );

        if (!canBreak) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou are not allowed to break blocks here.");
        }
    }



}