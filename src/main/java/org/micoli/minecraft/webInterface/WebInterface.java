package org.micoli.minecraft.webInterface;

//  scp /Users/o.michaud/Documents/workspace/minecraft-LocalPlan/target/morg-LocalPlan-0.1.1-SNAPSHOT.jar  micoli@craft.micoli.org:/opt/minecraft/plugins/.

import java.awt.event.ActionListener;
import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.micoli.minecraft.bukkit.QDBukkitPlugin;
import org.micoli.minecraft.bukkit.QDCommand;
import org.micoli.minecraft.bukkit.QDCommand.SenderType;
import org.micoli.minecraft.bukkit.QDCommandManager;
import org.micoli.minecraft.utils.Task;
import org.micoli.minecraft.webInterface.entities.heroes.HeroesConfigExporter;
import org.micoli.minecraft.webInterface.entities.heroes.HeroesPlayerExporter;
import org.micoli.minecraft.webInterface.entities.items.ItemDefinitionExporter;
import org.micoli.minecraft.webInterface.entities.localPlan.ParcelExporter;
import org.micoli.minecraft.webInterface.listeners.WebInterfaceJSONAPIListener;

import com.alecgorge.minecraft.jsonapi.JSONAPI;

// TODO: Auto-generated Javadoc
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
	private String parcelExporterPath = "export/localPlan";
	
	/** The Parcel exporter cfg. */
	final private String ParcelExporterCfg = "ParcelExporter.path";

	/** The items exporter path. */
	private String heroesExporterPath = "export/heroes";
	
	/** The items exporter cfg. */
	final private String heroesExporterCfg = "heroesExporter.path";

	/** The items exporter path. */
	private String itemsExporterPath = "export/items";
	
	/** The items exporter cfg. */
	final private String itemsExporterCfg = "itemsExporter.path";
	
	/** The perespctive used for maps exporter . */
	private String perspectiveMapsExporter ="iso_SE_60_vlowres";
	
	/** The items exporter cfg. */
	final private String perspectiveMapsExporterCfg = "mapsExporter.perspective";
	
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
		
		configFile.set(ParcelExporterCfg, configFile.getString(ParcelExporterCfg, getParcelExporterPath()));
		setParcelExporterPath(configFile.getString(ParcelExporterCfg));
		
		configFile.set(heroesExporterCfg, configFile.getString(heroesExporterCfg, getHeroesExporterPath()));
		setHeroesExporterPath(configFile.getString(heroesExporterCfg));
		
		configFile.set(itemsExporterCfg, configFile.getString(itemsExporterCfg, getItemsExporterPath()));
		setItemsExporterPath(configFile.getString(itemsExporterCfg));
		
		configFile.set(perspectiveMapsExporterCfg, configFile.getString(perspectiveMapsExporterCfg, getPerspectiveMapsExporter()));
		setPerspectiveMapsExporter(configFile.getString(perspectiveMapsExporterCfg));
		
		saveConfig();
		
		Plugin checkplugin = this.getServer().getPluginManager().getPlugin("JSONAPI");
		if (checkplugin != null) {
			JSONAPI jsonapi = (JSONAPI) checkplugin;
			WebInterfaceJSONAPIListener webInterfaceJSONAPIListener = new WebInterfaceJSONAPIListener();
			jsonapi.registerMethods(webInterfaceJSONAPIListener);
		}
		
		Task runningTask = new Task(this, this) {
			public void run() {
				instance.exportAll();
			}
		};
		runningTask.startDelayed(20L);

	}
	
	/**
	 * Export all.
	 */
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
		ParcelExporter.exportParcelMaps();
		ParcelExporter.exportParcels();
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
	 * Gets the items exporter path.
	 *
	 * @return the itemsExporterPath
	 */
	public String getItemsExporterPath() {
		return itemsExporterPath;
	}

	/**
	 * Sets the items exporter path.
	 *
	 * @param itemsExporterPath the itemsExporterPath to set
	 */
	public void setItemsExporterPath(String itemsExporterPath) {
		this.itemsExporterPath = itemsExporterPath;
	}

	/**
	 * @return the heroesExporterPath
	 */
	public String getHeroesExporterPath() {
		return heroesExporterPath;
	}

	/**
	 * @param heroesExporterPath the heroesExporterPath to set
	 */
	public void setHeroesExporterPath(String heroesExporterPath) {
		this.heroesExporterPath = heroesExporterPath;
	}

	/**
	 * @return the heroesExporterCfg
	 */
	public String getHeroesExporterCfg() {
		return heroesExporterCfg;
	}

	/**
	 * @return the parcelExporter
	 */
	public ParcelExporter getParcelExporter() {
		return parcelExporter;
	}

	/**
	 * @param parcelExporter the parcelExporter to set
	 */
	public void setParcelExporter(ParcelExporter parcelExporter) {
		this.parcelExporter = parcelExporter;
	}

	/**
	 * @return the parcelExporterCfg
	 */
	public String getParcelExporterCfg() {
		return ParcelExporterCfg;
	}

	/**
	 * @return the itemsExporterCfg
	 */
	public String getItemsExporterCfg() {
		return itemsExporterCfg;
	}

	/**
	 * @return the perspectiveMapsExporter
	 */
	public String getPerspectiveMapsExporter() {
		return perspectiveMapsExporter;
	}

	/**
	 * @param perspectiveMapsExporter the perspectiveMapsExporter to set
	 */
	public void setPerspectiveMapsExporter(String perspectiveMapsExporter) {
		this.perspectiveMapsExporter = perspectiveMapsExporter;
	}

	/**
	 * @return the perspectiveMapsExporterCfg
	 */
	public String getPerspectiveMapsExporterCfg() {
		return perspectiveMapsExporterCfg;
	}

	/**
	 * Gets the export json path.
	 *
	 * @return the export json path
	 */
	public File getExportJsonPath(String cfg) {
		File path = getDataFolder();
		if (!path.exists()) {
			path.mkdir();
		}
		File supPath=getDataFolder();
		String[] paths = configFile.getString(cfg).split("/");
		for(int i=0;i<paths.length;i++){
			path = new File(supPath, paths[i]);
			if (!path.exists()) {
				path.mkdir();
			}
			supPath = path;
		}
		return path;
	}

	/**
	 * CmdScan.
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
	@QDCommand(aliases = "scan", permissions = {  }, usage = "[<player>]", description = "list all parcel belonging to a given player, if no player given then use the current player",senderType=SenderType.BOTH)
	public void cmdScan(CommandSender sender, Command command, String label, String[] args) throws Exception {
		instance.exportAll();	
	}
}