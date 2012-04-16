package org.micoli.minecraft.heroesInterface;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.micoli.minecraft.bukkit.QDBukkitPlugin;
import org.micoli.minecraft.bukkit.QDCommand;
import org.micoli.minecraft.bukkit.QDCommandManager;
import org.micoli.minecraft.utils.ChatFormater;
import org.micoli.minecraft.utils.ServerLogger;
import org.micoli.minecraft.utils.Task;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.Skill;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalPlan.
 */
public class HeroesInterface extends QDBukkitPlugin implements ActionListener {

	/** The instance. */
	private static HeroesInterface instance;

	/** The executor. */
	QDCommandManager executor;

	/** The dynmap plugin. */
	Heroes heroesPlugin;

	/**
	 * Gets the single instance of LocalPlan.
	 * 
	 * @return the instance
	 */
	public static HeroesInterface getInstance() {
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

		heroesPlugin = (Heroes) getServer().getPluginManager().getPlugin("Heroes");

		configFile.set("marker.defaultPrice", configFile.getDouble("marker.defaultPrice", 50));
		saveConfig();

		executor = new QDCommandManager(this, new Class[] { getClass() });
		Task runningTask = new Task(this, this) {
			public void run() {
				listClasses();
			}
		};
		runningTask.startDelayed(50L);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.micoli.minecraft.bukkit.QDBukkitPlugin#getDatabaseORMClasses()
	 */
	protected java.util.List<Class<?>> getDatabaseORMClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		// list.add(Parcel.class);
		return list;
	};

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
	 * List classes.
	 */
	public void listClasses(){
		if (heroesPlugin == null) {
			ServerLogger.log("Heroes plugin unavailable");
		} else {
			ServerLogger.log("List of Heroes classes");
			ServerLogger.log("exp on death %s",heroesPlugin.getConfig().get("leveling.exp-loss"));
			Iterator<HeroClass> heroClassIterator = heroesPlugin.getClassManager().getClasses().iterator();
			while (heroClassIterator.hasNext()) {
				HeroClass heroClass = heroClassIterator.next();
				ServerLogger.log("class %s", heroClass.getName());
				ServerLogger.log("==> desc %s", heroClass.getDescription());
				ServerLogger.log("==> maxLevel %s", heroClass.getMaxLevel());
				ServerLogger.log("==> baseHealthLevel %s", heroClass.getBaseMaxHealth());
				ServerLogger.log("==> maxHealthLevel %s", heroClass.getMaxHealthPerLevel());
				ServerLogger.log("==> baseManaLevel %s", heroClass.getBaseMaxMana());
				ServerLogger.log("==> maxManaLevel %s", heroClass.getMaxManaPerLevel());
				
				for(Material material:heroClass.getAllowedArmor()){
					ServerLogger.log("==> armor allowed %s", material.toString());
				}
				for(Material material:heroClass.getAllowedWeapons()){
					ServerLogger.log("==> weapons allowed %s", material.toString());
				}
				for(String heroSkillName:heroClass.getSkillNames()){
					ServerLogger.log("==> skill %s", heroSkillName);
					Skill skill = heroesPlugin.getSkillManager().getSkill(heroSkillName);
					ServerLogger.log("   ==> usage %s", skill.getUsage());
					ServerLogger.log("   ==> permission %s", skill.getPermission());
				}
				
			}
		}
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
		listClasses();
	}

}