package com.bukkitbackup.full.threading;

import com.bukkitbackup.full.BackupFull;
import com.bukkitbackup.full.config.Settings;
import com.bukkitbackup.full.config.Strings;
import com.bukkitbackup.full.utils.LogUtils;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Backup - The simple server backup solution.
 *
 * @author Samuel98
 * @author info@samuel98.com
 */
public class PrepareBackup implements Runnable {

    private final Plugin plugin;
    private final Server pluginServer;
    private final Settings settings;
    private final Strings strings;
    public static boolean backupInProgress = false;
    public static boolean backupEnabled = true;
    public boolean isLastBackup = false;
    public boolean isManualBackup;

    public PrepareBackup(Plugin plugin, Settings settings, Strings strings) {
        this.plugin = plugin;
        this.pluginServer = plugin.getServer();
        this.settings = settings;
        this.strings = strings;
    }

    public synchronized void run() {
        if (backupInProgress) {
            LogUtils.sendLog(strings.getString("backupinprogress"));
        } else {
            checkShouldDoBackup();
        }
    }

    /**
     * This method decides whether the doBackup should be run.
     *
     * It checks: - Online players. - Bypass node. - Manual doBackup.
     *
     * It then runs the doBackup if needed.
     */
    private void checkShouldDoBackup() {
        // If it is a manual doBackup, start it, otherwise, perform checks.
        if (isManualBackup) {
            prepareBackup();
        } else if (backupEnabled) {
            // No player checking.
            if (settings.getBooleanProperty("backupemptyserver", false)) {
                prepareBackup();
            } else {
                // Checking online players.
                if (pluginServer.getOnlinePlayers().size() == 0) {

                    // Check if last backup
                    if (isLastBackup) {
                        LogUtils.sendLog(strings.getString("lastbackup"));
                        prepareBackup();
                        isLastBackup = false;
                    } else {
                        LogUtils.sendLog(strings.getString("abortedbackup"));
                    }
                } else {
                    // Default don't do backup.
                    boolean doBackup = false;

                    // Get all online players and loop through them.
                    for (Player currentplayer : pluginServer.getOnlinePlayers()) {
                        // If any players do not have the node, do the doBackup.
                        if (!currentplayer.hasPermission("backup.bypass")) {
                            doBackup = true;
                        }
                    }

                    // Final check if we should do the backup.
                    if (doBackup) {
                        prepareBackup();
                    } else {
                        LogUtils.sendLog(strings.getString("skipbackupbypass"));
                    }
                }
            }
        } else {
            LogUtils.sendLog(strings.getString("backupoff"));
        }
    }

    /**
     * Prepares for, and starts, a doBackup.
     */
    protected void prepareBackup() {
        // Tell the world!
        backupInProgress = true;

        // Notify doBackup has started.
        notifyStarted();

        // Save all players to worlds.
        pluginServer.savePlayers();

        // Turn off auto-saving of worlds.
        for (World world : pluginServer.getWorlds()) {
            world.setAutoSave(false);
        }

        // Perform final world save before backup.
        for (World world : pluginServer.getWorlds()) {
            world.save();
        }

        // Schedule the doBackup.
        pluginServer.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                pluginServer.getScheduler().runTaskAsynchronously(plugin, BackupFull.backupTask);
            }
        });
        isManualBackup = false;
    }

    /**
     * Notify that the backup has started.
     *
     */
    private void notifyStarted() {
        // Get message.
        String startBackupMessage = strings.getString("backupstarted");

        // Check the string is set.
        if (startBackupMessage != null) {

            // Check if we are using multiple lines.
            if (startBackupMessage.contains(";;")) {

                // Convert to array of lines.
                List<String> messageList = Arrays.asList(startBackupMessage.split(";;"));

                // Loop the lines of this message.
                for (String thisMessage : messageList) {
                    // Retrieve this line of the message.
                    // Notify all players, regardless of the permission node.
                    this.notifyPlayers(thisMessage);
                }
            } else {
                // Notify all players, regardless of the permission node.
                this.notifyPlayers(startBackupMessage);
            }
        }
    }

    /**
     * Private method to notify all players.
     * @param message The message of which to notify.
     */
    private void notifyPlayers(String message) {
        // Notify all players, regardless of the permission node.
        if (settings.getBooleanProperty("notifyallplayers", true)) {
            pluginServer.broadcastMessage(message);
        } else {
            // Get all online players and loop through them.
            for (Player currentplayer : pluginServer.getOnlinePlayers()) {
                // If the current player has the right permissions, notify them.
                if (currentplayer.hasPermission("backup.notify")) {
                    currentplayer.sendMessage(message);
                }
            }
        }
    }
}
