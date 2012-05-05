package org.micoli.minecraft.webInterface.entities.heroes;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Material;
import org.micoli.minecraft.bukkit.QDBukkitPlugin;
import org.micoli.minecraft.utils.Json;
import org.micoli.minecraft.webInterface.WebInterface;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillType;

/**
 * The Class HeroesExporter.
 */
public class HeroesConfigExporter {

	/** The heroes plugin. */
	private Heroes heroesPlugin;

	/** The plugin. */
	private WebInterface plugin;

	/**
	 * Instantiates a new heroes exporter.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public HeroesConfigExporter(WebInterface plugin) {
		this.plugin = plugin;
		heroesPlugin = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
	}

	/**
	 * The Class LevelingConfigPrm.
	 */
	class LevelingConfigPrm {

		/** The exp_curve. */
		public double exp_curve;

		/** The max_exp. */
		public double max_exp;

		/** The max_level. */
		public double max_level;

		/** The exp_loss. */
		public double exp_loss;

		/** The pvp_exp_loss. */
		public double pvp_exp_loss;

		/** The max_party_size. */
		public double max_party_size;

		/** The party_exp_bonus. */
		public double party_exp_bonus;

		/** The level_loss. */
		public boolean level_loss;

		/** The mastery_loss. */
		public boolean mastery_loss;

		/** The spawner_checks. */
		public boolean spawner_checks;

		/** The spawner_exp_mult. */
		public double spawner_exp_mult;

		/** The reset_on_death. */
		public boolean reset_on_death;

		/** The pvp_range. */
		public double pvp_range;

		/** The min_pvp_level. */
		public double min_pvp_level;

		/** The dump_exp_file. */
		public boolean dump_exp_file;
	}

	/**
	 * The Class ClassesConfigPrm.
	 */
	class ClassesConfigPrm {

		/** The swap_cost. */
		public double swap_cost;

		/** The old_swap_cost. */
		public double old_swap_cost;

		/** The prof_swap_cost. */
		public double prof_swap_cost;

		/** The old_prof_swap_cost. */
		public double old_prof_swap_cost;

		/** The master_swap_free. */
		public double master_swap_free;

		/** The first_swap_free. */
		public double first_swap_free;

		/** The use_prefix. */
		public double use_prefix;

		/** The reset_exp_on_change. */
		public double reset_exp_on_change;

		/** The reset_master_on_change. */
		public double reset_master_on_change;

		/** The reset_prof_master_on_change. */
		public double reset_prof_master_on_change;

		/** The reset_prof_on_pri_change. */
		public double reset_prof_on_pri_change;

		/** The lock_till_master. */
		public double lock_till_master;

		/** The lock_at_max_level. */
		public double lock_at_max_level;
	}

	/**
	 * The Class PropertiesConfigPrm.
	 */
	class PropertiesConfigPrm {

		/** The storage_type. */
		public double storage_type;

		/** The economy. */
		public double economy;

		/** The debug. */
		public double debug;

		/** The enchant_exp_mult. */
		public double enchant_exp_mult;

		/** The global_cooldown. */
		public double global_cooldown;

		/** The block_tracking_duration. */
		public double block_tracking_duration;

		/** The max_tracked_blocks. */
		public double max_tracked_blocks;

		/** The food_heal_percent. */
		public double food_heal_percent;

		/** The slow_while_casting. */
		public double slow_while_casting;

		/** The combat_time. */
		public double combat_time;
	}

	/**
	 * The Class BedConfigPrm.
	 */
	class BedConfigPrm {

		/** The enabled. */
		public double enabled;

		/** The interval. */
		public double interval;

		/** The percent. */
		public double percent;
	}

	/**
	 * The Class ManaConfigPrm.
	 */
	class ManaConfigPrm {

		/** The interval. */
		public double interval;
	}

	/**
	 * The Class BonusConfigPrm.
	 */
	class BonusConfigPrm {

		/** The expiration. */
		public double expiration;

		/** The exp. */
		public double exp;

		/** The message. */
		public double message;
	}

	/**
	 * The Class HatsConfigPrm.
	 */
	class HatsConfigPrm {

		/** The level. */
		public double level;

		/** The enabled. */
		public double enabled;
	}

	/**
	 * The Class SkillConfig.
	 */
	class SkillConfig {

		/** The name. */
		public String name;

		/** The usage. */
		public String usage;

		/** The permission. */
		public String permission;

		/** The notes. */
		public String[] notes;

		/** The identifiers. */
		public String[] identifiers;

		/** The types. */
		public Set<SkillType> types;
	}

	/**
	 * The Class ClassConfig.
	 */
	class ClassConfig {

		/** The name. */
		public String name;

		/** The description. */
		public String description;

		/** The max level. */
		public int maxLevel;

		/** The base max health. */
		public int baseMaxHealth;

		/** The max health per level. */
		public double maxHealthPerLevel;

		/** The base max mana. */
		public int baseMaxMana;

		/** The max mana per level. */
		public double maxManaPerLevel;

		/** The allowed armor. */
		Set<String> allowedArmor = new HashSet<String>();

		/** The allowed weapons. */
		Set<String> allowedWeapons = new HashSet<String>();

		/** The skills. */
		HashMap<String, SkillConfig> skills = new HashMap<String, SkillConfig>();
	}

	/**
	 * The Class ExportFormat.
	 */
	class ExportFormat {

		/** The leveling config prm. */
		public LevelingConfigPrm levelingConfigPrm = new LevelingConfigPrm();

		/** The classes config prm. */
		public ClassesConfigPrm classesConfigPrm = new ClassesConfigPrm();

		/** The properties config prm. */
		public PropertiesConfigPrm propertiesConfigPrm = new PropertiesConfigPrm();

		/** The bed config prm. */
		public BedConfigPrm bedConfigPrm = new BedConfigPrm();

		/** The mana config prm. */
		public ManaConfigPrm manaConfigPrm = new ManaConfigPrm();

		/** The bonus config prm. */
		public BonusConfigPrm bonusConfigPrm = new BonusConfigPrm();

		/** The hats config prm. */
		public HatsConfigPrm hatsConfigPrm = new HatsConfigPrm();

		/** The classes. */
		HashMap<String, ClassConfig> classes = new HashMap<String, ClassConfig>();
	}



	/**
	 * Gets the config prm.
	 * 
	 * @param sectionName
	 *            the section name
	 * @param prm
	 *            the prm
	 * @return the config prm
	 */
	public void getConfigPrm(String sectionName, Object prm) {
		for (Field subField : prm.getClass().getDeclaredFields()) {
			String propertyName = subField.getName().replace("_", "-");
			Object val = heroesPlugin.getConfig().get(sectionName + "." + propertyName);
			try {
				subField.set(prm, val);
				// ServerLogger.log("%s::%s => %s", sectionName, propertyName,
				// val);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
	}

	/**
	 * Export config.
	 */
	public void exportConfig() {
		plugin.logger.log("Heroes ExportConfig");
		if (heroesPlugin == null) {
			plugin.logger.log("Heroes plugin unavailable");
		} else {
			ExportFormat exportValues = new ExportFormat();
			getConfigPrm("leveling", exportValues.levelingConfigPrm);
			getConfigPrm("leveling", exportValues.classesConfigPrm);
			getConfigPrm("properties", exportValues.propertiesConfigPrm);
			getConfigPrm("bed", exportValues.bedConfigPrm);
			getConfigPrm("mana", exportValues.manaConfigPrm);
			getConfigPrm("bonus", exportValues.bonusConfigPrm);
			getConfigPrm("hats", exportValues.hatsConfigPrm);

			Iterator<HeroClass> heroClassIterator = heroesPlugin.getClassManager().getClasses().iterator();
			while (heroClassIterator.hasNext()) {
				HeroClass heroClass = heroClassIterator.next();
				ClassConfig classConfig = new ClassConfig();
				classConfig.name = heroClass.getName();
				classConfig.description = heroClass.getDescription();
				classConfig.maxLevel = heroClass.getMaxLevel();
				classConfig.baseMaxHealth = heroClass.getBaseMaxHealth();
				classConfig.maxHealthPerLevel = heroClass.getMaxHealthPerLevel();
				classConfig.baseMaxMana = heroClass.getBaseMaxMana();
				classConfig.maxManaPerLevel = heroClass.getMaxManaPerLevel();

				for (Material material : heroClass.getAllowedArmor()) {
					classConfig.allowedArmor.add(material.toString());
				}

				for (Material material : heroClass.getAllowedWeapons()) {
					classConfig.allowedWeapons.add(material.toString());
				}

				for (String heroSkillName : heroClass.getSkillNames()) {
					SkillConfig skillConfig = new SkillConfig();
					skillConfig.name = heroSkillName;
					Skill skill = heroesPlugin.getSkillManager().getSkill(heroSkillName);
					skillConfig.usage = skill.getUsage();
					skillConfig.permission = skill.getPermission();
					skillConfig.notes = skill.getNotes();
					skillConfig.identifiers = skill.getIdentifiers();
					skillConfig.types = skill.getTypes();
					classConfig.skills.put(skillConfig.name, skillConfig);
				}

				exportValues.classes.put(classConfig.name, classConfig);
				File path = plugin.getExportJsonPath(plugin.getHeroesExporterCfg());
				Json.exportObjectToJson(String.format("%s/__allclasses.json", path), exportValues);
			}
			// plugin.logger.log(Json.exportObjectToJson(exportValues));
			plugin.logger.log("Heroes ExportConfig done");
		}
	}
}
