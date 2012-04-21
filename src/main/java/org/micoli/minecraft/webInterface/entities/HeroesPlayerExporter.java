package org.micoli.minecraft.webInterface.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.micoli.minecraft.bukkit.QDBukkitPlugin;
import org.micoli.minecraft.utils.Json;
import org.micoli.minecraft.webInterface.WebInterface;
import org.yaml.snakeyaml.Yaml;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;

public class HeroesPlayerExporter {
	/** The heroes plugin. */
	Heroes heroesPlugin;

	/** The plugin. */
	QDBukkitPlugin plugin;

	/**
	 * Instantiates a new heroes exporter.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public HeroesPlayerExporter(QDBukkitPlugin plugin) {
		this.plugin = plugin;
		heroesPlugin = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
	}

	/**
	 * The Class HeroFormat.
	 */
	class HeroFormat {

		/** The player. */
		public String player = "";

		/** The binds. */
		public Map<String, String[]> binds = new HashMap<String, String[]>();

		/** The enchanting class. */
		public String enchantingClass = "";

		/** The experience map. */
		public Map<String, Double> experienceMap = new HashMap<String, Double>();

		/** The hero class. */
		public String heroClass = "";

		/** The second class. */
		public String secondClass = "";

		/** The level. */
		public int level = 0;

		/** The mana. */
		public int mana = 0;

		/** The max mana. */
		public int maxMana = 0;

		/** The mana regen. */
		public int manaRegen = 0;

		/** The health. */
		public int health = 0;

		/** The max health. */
		public int maxHealth = 0;

		/** The skills. */
		public Map<String, ConfigurationSection> skills = new HashMap<String, ConfigurationSection>();

		/** The skill settings. */
		public Map<String, ConfigurationSection> skillSettings = new HashMap<String, ConfigurationSection>();

		/** The summons. */
		public Set<String> summons = new HashSet<String>();

		/** The suppressed skills. */
		public Set<String> suppressedSkills = new HashSet<String>();
	}

	/**
	 * Export players.
	 */
	public void exportPlayers() {
		plugin.logger.log("Heroes ExportPlayers");
		if (heroesPlugin == null) {
			plugin.logger.log("Heroes plugin unavailable");
		} else {
			Map<String, Object> heroes = new HashMap<String, Object>();
			String root = heroesPlugin.getDataFolder() + "/players";
			String[] letterPath = new File(root).list();
			for (int i = 0; i < letterPath.length; i++) {
				File playersSubPathF = new File(root + "/" + letterPath[i]);
				if (playersSubPathF.isDirectory()) {
					String[] playersSubPath = playersSubPathF.list();
					for (int j = 0; j < playersSubPath.length; j++) {
						String playerFilename = root + "/" + letterPath[i] + "/" + playersSubPath[j];
						Yaml yaml = new Yaml();
						try {
							Object heroData = yaml.load(new FileInputStream(new File(playerFilename)));
							plugin.logger.log(heroData.toString());
							heroes.put(playersSubPath[j].replace(".yml", ""), heroData);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void exportPlayersStrongFormat() {
		plugin.logger.log("Heroes ExportPlayers");
		if (heroesPlugin == null) {
			plugin.logger.log("Heroes plugin unavailable");
		} else {
			Map<String, HeroFormat> heroes = new HashMap<String, HeroFormat>();
			Iterator<Hero> heroesIterator = heroesPlugin.getCharacterManager().getHeroes().iterator();
			while (heroesIterator.hasNext()) {
				Hero hero = heroesIterator.next();
				HeroFormat heroFormat = new HeroFormat();

				heroFormat.player = hero.getName();
				heroFormat.health = hero.getHealth();
				heroFormat.level = hero.getLevel();

				heroes.put(heroFormat.player, heroFormat);

				// Map<Material, String[]> binds = hero.getBinds();
				// HeroClass enchantingClass = hero.getEnchantingClass();
				// Map<String, Double> experienceMap =
				// hero.getExperienceMap();
				// HeroClass heroClass = hero.getHeroClass();
				// HeroClass secondClass = hero.getSecondClass();
				// int level = hero.getLevel();
				// int mana = hero.getMana();
				// int maxMana = hero.getMaxMana();
				// int manaRegen = hero.getManaRegen();
				// int health = hero.getHealth();
				// int maxHealth = hero.getMaxHealth();
				// Map<String, ConfigurationSection> skills =
				// hero.getSkills();
				// Map<String, ConfigurationSection> skillSettings =
				// hero.getSkillSettings();
				// Set<Monster> summons = hero.getSummons();
				// Set<String> suppressedSkills =
				// hero.getSuppressedSkills();
			}
			File path = ((WebInterface) plugin).getExportJsonPath();
			Json.exportObjectToJson(String.format("%s/__allheroes.json", path), heroes);
			// ServerLogger.log(Json.exportObjectToJson(heroes));
		}
	}
}
