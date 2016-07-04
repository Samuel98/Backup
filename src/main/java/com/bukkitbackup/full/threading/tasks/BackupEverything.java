package com.bukkitbackup.full.threading.tasks;

import com.bukkitbackup.full.config.Settings;
import com.bukkitbackup.full.utils.FileUtils;
import static com.bukkitbackup.full.utils.FileUtils.FILE_SEPARATOR;
import java.io.File;
import java.io.FileFilter;

/**
 * Backup - The simple server backup solution.
 *
<<<<<<< HEAD
 * @author Domenic Horner (gamerx)
=======
 * @author gamerx
 * @author me@gamerx.me
>>>>>>> dev
 */
public class BackupEverything {

    private final String backupPath;
    private final boolean shouldZIP;
    private final boolean useTemp;
    private final String tempDestination;
    private final FileFilter fileFilter;

    public BackupEverything(final Settings settings) {

        // Get the backup destination.
        backupPath = settings.getStringProperty("backuppath", "backups");

        // Get backup properties.
        shouldZIP = settings.getBooleanProperty("zipbackup", true);
        useTemp = settings.getBooleanProperty("usetemp", true);

        // Filefiler for excludes.
        fileFilter = new FileFilter() {

<<<<<<< HEAD
=======
            @Override
>>>>>>> dev
            public boolean accept(File f) {

                // Disallow server.log and the backuppath.
                if (f.getName().equals(settings.getStringProperty("backuppath", "backups"))) {
                    return false;
                }

                if (f.getName().equals("server.log")) {
                    return false;
                }

                return true;
            }
        };

        // Generate the worldStore.
        if (useTemp) {
            String tempFolder = settings.getStringProperty("tempfoldername", "");
            if (!tempFolder.equals("")) { // Absolute.
                tempDestination = tempFolder.concat(FILE_SEPARATOR);
            } else { // Relative.
                tempDestination = backupPath.concat(FILE_SEPARATOR).concat("temp").concat(FILE_SEPARATOR);
            }
        } else { // No temp folder.
            tempDestination = backupPath.concat(FILE_SEPARATOR);
        }
    }

    // The actual backup should be done here, as it is run in another thread.
    public void doEverything(String backupName) throws Exception {
<<<<<<< HEAD
            // Copy the directory.
            FileUtils.copyDirectory(new File(".".concat(FILE_SEPARATOR)), new File(tempDestination.concat(backupName)), fileFilter, true);

            // Perform the zipping action.
            FileUtils.doCopyAndZIP(tempDestination.concat(backupName), backupPath.concat(FILE_SEPARATOR).concat(backupName), shouldZIP, useTemp);
=======
        // Copy the directory.
        FileUtils.copyDirectory(new File(".".concat(FILE_SEPARATOR)), new File(tempDestination.concat(backupName)), fileFilter, true);

        // Perform the zipping action.
        FileUtils.doCopyAndZIP(tempDestination.concat(backupName), backupPath.concat(FILE_SEPARATOR).concat(backupName), shouldZIP, useTemp);
>>>>>>> dev

    }
}
