package org.micoli.minecraft.webInterface;

import java.awt.event.ActionListener;
import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.micoli.minecraft.bukkit.QDBukkitPlugin;
import org.micoli.minecraft.bukkit.QDCommand;
import org.micoli.minecraft.bukkit.QDCommandManager;
import org.micoli.minecraft.utils.ChatFormater;
import org.micoli.minecraft.utils.Task;
import org.micoli.minecraft.webInterface.entities.HeroesExporter;
import org.micoli.minecraft.webInterface.entities.ItemDefinition;

/**
 * The Class LocalPlan.
 */
public class WebInterface extends QDBukkitPlugin implements ActionListener {

	/** The instance. */
	private static WebInterface instance;

	/** The executor. */
	QDCommandManager executor;
	
	/**
	 * Gets the single instance of LocalPlan.
	 * 
	 * @return the instance
	 */
	public static WebInterface getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.micoli.minecraft.bukkit.QDBukkitPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		
		instance = this;
		commandString = "heroesint";
		withDatabase = false;
		super.onEnable();
		log(ChatFormater.format("%s version enabled", this.pdfFile.getName(), this.pdfFile.getVersion()));

		
		saveConfig();

		executor = new QDCommandManager(this, new Class[] { getClass() });
		Task runningTask = new Task(this, this) {
			public void run() {
				ItemDefinition.initialize(instance);
				ItemDefinition.exportDatas();
				
				HeroesExporter heroesExporter= new HeroesExporter(instance);
				heroesExporter.exportConfig();
				heroesExporter.exportPlayers();	
			}
		};
		runningTask.startDelayed(20L);
	}

	/**
	 * Cmd_comments on.
	 * 
	 * @param sender
	 *            the sender
	 * @param command
	 *            the command
	 * @param label
	 *            the label
	 * @param args
	 *            the args
	 */
	@QDCommand(aliases = "commentsOn", permissions = {}, usage = "", description = "enable plugin comments")
	public void cmd_commentsOn(CommandSender sender, Command command, String label, String[] args) {
		setComments((Player) sender, true);
	}

	/**
	 * Cmd_comments off.
	 * 
	 * @param sender
	 *            the sender
	 * @param command
	 *            the command
	 * @param label
	 *            the label
	 * @param args
	 *            the args
	 */
	@QDCommand(aliases = "commentsOff", permissions = {}, usage = "", description = "disabled plugin comments")
	public void cmd_commentsOff(CommandSender sender, Command command, String label, String[] args) {
		setComments((Player) sender, false);
	}
	
	
	/**
	 * Cmd_list.
	 * 
	 * @param sender
	 *            the sender
	 * @param command
	 *            the command
	 * @param label
	 *            the label
	 * @param args
	 *            the args
	 * @throws Exception
	 *             the exception
	 */
	@QDCommand(aliases = "list", permissions = { "localplan.list" }, usage = "[<player>]", description = "list all parcel belonging to a given player, if no player given then use the current player")
	public void cmd_list(CommandSender sender, Command command, String label, String[] args) throws Exception {
		HeroesExporter heroesExporter= new HeroesExporter(instance);
		heroesExporter.exportConfig();	
		heroesExporter.exportPlayers();	
	}
	/**
	 * Gets the export json path.
	 *
	 * @return the export json path
	 */
	public File getExportJsonPath() {
		File exportJsonPath = getDataFolder();
		if (!exportJsonPath.exists()) {
			exportJsonPath.mkdir();
		}
		exportJsonPath = new File(getDataFolder(), "exportJson");
		if (!exportJsonPath.exists()) {
			exportJsonPath.mkdir();
		}
		return exportJsonPath;
	}

}