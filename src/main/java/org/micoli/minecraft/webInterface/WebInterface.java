package org.micoli.minecraft.webInterface;

//  scp /Users/o.michaud/Documents/workspace/minecraft-LocalPlan/target/morg-LocalPlan-0.1.1-SNAPSHOT.jar  micoli@craft.micoli.org:/opt/minecraft/plugins/.

import java.awt.event.ActionListener;
import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.micoli.minecraft.bukkit.QDBukkitPlugin;
import org.micoli.minecraft.bukkit.QDCommand;
import org.micoli.minecraft.bukkit.QDCommandManager;
import org.micoli.minecraft.utils.Task;
import org.micoli.minecraft.webInterface.entities.heroes.HeroesConfigExporter;
import org.micoli.minecraft.webInterface.entities.heroes.HeroesPlayerExporter;
import org.micoli.minecraft.webInterface.entities.items.ItemDefinitionExporter;
import org.micoli.minecraft.webInterface.entities.localPlan.ParcelExporter;

/**
 * The Class LocalPlan.
 */
public class WebInterface extends QDBukkitPlugin implements ActionListener {

	/** The instance. */
	private static WebInterface instance;

	/** The executor. */
	protected QDCommandManager executor;

	/**
	 * Gets the single instance of LocalPlan.
	 * 
	 * @return the instance
	 */
	public static WebInterface getInstance() {
		return instance;
	}
	/** The parcel exporter. */
	private ParcelExporter parcelExporter;

	/** The parcel exporter path. */
	private String parcelExporterPath = "parcelImages";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.micoli.minecraft.bukkit.QDBukkitPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		instance = this;
		commandString = "webinterface";
		super.onEnable();
		logger.log("%s version enabled", this.pdfFile.getName(), this.pdfFile.getVersion());

		executor = new QDCommandManager(this, new Class[] { getClass() });
		
		configFile.set("ParcelExporter.imagePath", configFile.getString("ParcelExporter.imagePath", getParcelExporterPath()));
		setParcelExporterPath(configFile.getString("ParcelExporter.imagePath"));
		
		saveConfig();
		
		Task runningTask = new Task(this, this) {
			public void run() {
				instance.exportAll();
			}
		};
		runningTask.startDelayed(20L);

	}
	
	public void exportAll(){
		logger.log("Export Items");
		ItemDefinitionExporter.initialize(instance);
		ItemDefinitionExporter.exportDatas();
		
		logger.log("Export heroesConfig");
		HeroesConfigExporter heroesConfigExporter= new HeroesConfigExporter(instance);
		heroesConfigExporter.exportConfig();
		
		logger.log("Export heroesPlayers");
		HeroesPlayerExporter heroesPlayerExporter= new HeroesPlayerExporter(instance);
		heroesPlayerExporter.exportPlayers();
		
		logger.log("Export Parcels");
		parcelExporter = new ParcelExporter(instance);
		parcelExporter.getMaps();
		logger.log("Export finished");
	}

	/**
	 * Gets the parcel exporter path.
	 *
	 * @return the parcelExporterPath
	 */
	public String getParcelExporterPath() {
		return parcelExporterPath;
	}

	/**
	 * Sets the parcel exporter path.
	 *
	 * @param parcelExporterPath the parcelExporterPath to set
	 */
	public void setParcelExporterPath(String parcelExporterPath) {
		this.parcelExporterPath = parcelExporterPath;
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
	@QDCommand(aliases = "scan", permissions = {  }, usage = "[<player>]", description = "list all parcel belonging to a given player, if no player given then use the current player")
	public void cmd_scan(CommandSender sender, Command command, String label, String[] args) throws Exception {
		instance.exportAll();	
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