/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

package skinsrestorer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import skinsrestorer.storage.SkinProfile;
import skinsrestorer.utils.SkinGetUtils;
import skinsrestorer.utils.SkinGetUtils.SkinFetchFailedException;

public class Commands implements CommandExecutor {

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (!sender.hasPermission("skinsrestorer.cmds")) {
			sender.sendMessage("You don't have permission to do this");
			return true;
		}
		if ((args.length == 2) && args[0].equalsIgnoreCase("drop")) {
			SkinsRestorer.getInstance().getSkinStorage().removeSkinData(args[1]);
			sender.sendMessage(ChatColor.BLUE+"Skin data for player "+args[1]+" dropped");
		} else
		if ((args.length == 2) && args[0].equalsIgnoreCase("update")) {
			executor.execute(
				new Runnable() {
					@Override
					public void run() {
						String name = args[1];
						try {
							SkinProfile profile = SkinGetUtils.getSkinProfile(name);
							SkinsRestorer.getInstance().getSkinStorage().addSkinData(name, profile);
							sender.sendMessage(ChatColor.BLUE+"Skin data updated");
						} catch (SkinFetchFailedException e) {
							sender.sendMessage(ChatColor.RED+"Skin fetch failed: "+e.getMessage());
						}
					}
				}
			);
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			SkinsRestorer.getInstance().getConfiguration().loadConfig();
			SkinsRestorer.getInstance().startListeners();
			sender.sendMessage(ChatColor.BLUE+"Configuration reloaded");
		}
		return false;
	}

}
