package org.micoli.minecraft.webInterface.entities.items;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.micoli.minecraft.utils.Json;
import org.micoli.minecraft.webInterface.WebInterface;

/**
 * The Class QDItemDefinition.
 */
public class ItemDefinitionExporter {
	
	/** The plugin. */
	public static WebInterface plugin;

	/** The img terrain. */
	public static BufferedImage imgTerrain;
	
	/** The img items. */
	public static BufferedImage imgItems;
	
	/** The img special. */
	public static BufferedImage imgSpecial;
	
	/** The all icons. */
	public static BufferedImage allIcons;
	
	/** The item definitions. */
	private static HashMap<Integer, ItemDefinition> itemDefinitions = new HashMap<Integer, ItemDefinition>();
	
	/** The icons. */
	HashMap<Integer, IconDef> icons = new HashMap<Integer, IconDef>();

	/**
	 * Adds the item.
	 *
	 * @param data the data
	 * @param filename the filename
	 * @param subName the sub name
	 * @param x the x
	 * @param y the y
	 */
	public void addItem(Integer data, String filename, String subName, Integer x, Integer y) {
		IconDef def = new IconDef();
		def.data = data;
		def.filename = filename;
		def.subName = subName;
		def.x = x;
		def.y = y;
		icons.put(data, def);
	}

	/**
	 * Gets the item definitions.
	 *
	 * @return the itemDefinitions
	 */
	public static HashMap<Integer, ItemDefinition> getItemDefinitions() {
		return itemDefinitions;
	}

	/**
	 * Initialize.
	 *
	 * @param instance the instance
	 */
	public static void initialize(WebInterface instance) {
		plugin = instance;
		try {
			imgTerrain = ImageIO.read(plugin.getClass().getClassLoader().getResourceAsStream("terrain.png"));
			imgItems = ImageIO.read(plugin.getClass().getClassLoader().getResourceAsStream("items.png"));
			imgSpecial = ImageIO.read(plugin.getClass().getClassLoader().getResourceAsStream("special.png"));
		} catch (Exception e) {
			plugin.logger.log("error reading images tiles :: %s", e.toString());
			plugin.logger.dumpStackTrace(e);
		}

		plugin.logger.log("reading items.txt");
		Integer maxId = parseItemsTxt(plugin.getClass().getClassLoader().getResourceAsStream("items.txt"));
		ItemDefinition.setAllIcons(new BufferedImage(16 * (maxId + 1), 16 * (16 + 1), imgTerrain.getType()));
	}
	/**
	 * Parses the items txt.
	 *
	 * @param is the is
	 * @return the integer
	 */
	public static Integer parseItemsTxt(InputStream is) {
		Pattern fullPattern = Pattern.compile("(\\s*)([0-9]{1,4})(\\s+)(.*)(\\s+)(.*)\\.png(\\s+)([0-9]{1,3}),([0-9]{1,3})(\\s*)(.*)");
		Pattern dataPattern = Pattern.compile("(\\s*)([0-9]{1,3})(\\s*)");
		Matcher matcher;
		Integer maxId = 0;
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader input = new BufferedReader(isr);
			try {
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					matcher = fullPattern.matcher(line);
					if (matcher.matches()) {
						ItemDefinition def;
						Integer id = Integer.parseInt(matcher.group(2));
						if (getItemDefinitions().containsKey(id)) {
							def = getItemDefinitions().get(id);
						} else {
							def = new ItemDefinition(Material.getMaterial(id));
							getItemDefinitions().put(id, def);
						}
						String name = matcher.group(4).toUpperCase().trim();
						String filename = matcher.group(6).trim();
						Integer x = Integer.parseInt(matcher.group(8));
						Integer y = Integer.parseInt(matcher.group(9));
						matcher = dataPattern.matcher(matcher.group(11));
						Integer data = 0;
						if (matcher.matches()) {
							data = Integer.parseInt(matcher.group(2));
						}
						maxId = Math.max(id, maxId);
						def.addItem(data, filename, name, x, y);
						//ServerLogger.log("%s %s %s %s", id, data, name, filename);
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			plugin.logger.dumpStackTrace(e);
		}
		return maxId;
	}

	/**
	 * Export datas.
	 */
	public static void exportDatas() {
		List<SubDataIt> listSd = new ArrayList<SubDataIt>();
		plugin.logger.log("Exporting datas");
		
		Iterator<Integer> iterator = getItemDefinitions().keySet().iterator();

		while (iterator.hasNext()) {
			Integer key = iterator.next();
			ItemDefinition rd = getItemDefinitions().get(key);
			//ServerLogger.log("%s", rd.getMaterial().name());
			rd.export();
			SubDataIt sd = new SubDataIt(rd.getMaterial().getId(), rd.getMaterial().name());
			listSd.add(sd);
		}

		File path = ((WebInterface) plugin).getExportJsonPath();
		Json.exportObjectToJson(String.format("%s/__allitems.json", path.getAbsoluteFile()), listSd);
		File pngWriter = new File(String.format("%s/__allicons.png", path.getAbsoluteFile()));
		File jpgWriter = new File(String.format("%s/__allicons.jpg", path.getAbsoluteFile()));
		File gifWriter = new File(String.format("%s/__allicons.gif", path.getAbsoluteFile()));
		try {
			ImageIO.write(ItemDefinition.getAllIcons(), "png", pngWriter);
			ImageIO.write(ItemDefinition.getAllIcons(), "jpg", jpgWriter);
			ImageIO.write(ItemDefinition.getAllIcons(), "gif", gifWriter);
		} catch (Exception e) {
			plugin.logger.dumpStackTrace(e);
		}
	}

	/**
	 * List recipes.
	 */
	public void listRecipes() {
		Iterator<Recipe> iterator = plugin.getServer().recipeIterator();

		while (iterator.hasNext()) {
			//Recipe recipeItem = iterator.next();
			//ServerLogger.log("%s", recipeItem.getResult().getType().name());
			/*
			 * if
			 * (!itemDefinitions.containsKey(recipeItem.getResult().getTypeId(
			 * ))) { rd = new QDItemDefinition(this,
			 * Material.getMaterial(recipeItem.getResult().getType().getId()));
			 * rd.setMaterial(recipeItem.getResult().getType());
			 * itemDefinitions.put(recipeItem.getResult().getType().getId(),
			 * rd); } else { rd =
			 * itemDefinitions.get(recipeItem.getResult().getTypeId()); }
			 * rd.export();
			 */
		}
	}
}
