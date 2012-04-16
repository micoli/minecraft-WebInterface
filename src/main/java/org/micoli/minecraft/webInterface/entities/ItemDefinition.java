package org.micoli.minecraft.webInterface.entities;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.micoli.minecraft.utils.ChatFormater;
import org.micoli.minecraft.utils.Images;
import org.micoli.minecraft.utils.Json;
import org.micoli.minecraft.utils.ServerLogger;
import org.micoli.minecraft.webInterface.WebInterface;

/**
 * The Class QDItemDefinition.
 */
public class ItemDefinition {
	
	/** The plugin. */
	private static JavaPlugin plugin;

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

	/** The material. */
	private Material material;
	
	/** The scale. */
	private Boolean scale = false;
	
	/** The path. */
	private File path;

	/** The icons. */
	HashMap<Integer, IconDef> icons = new HashMap<Integer, IconDef>();

	/**
	 * Gets the material.
	 *
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Sets the material.
	 *
	 * @param material the new material
	 */
	public void setMaterial(Material material) {
		this.material = material ;
	}

	/**
	 * Instantiates a new qD item definition.
	 *
	 * @param mat the mat
	 */
	public ItemDefinition(Material mat) {
		this.material = mat;
	}

	/**
	 * Export.
	 */
	public void export() {
		path = ((WebInterface) plugin).getExportJsonPath();
		ServerLogger.log("==>export  " + material.name());

		exportData();
		Iterator<Integer> iterator = icons.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			exportIcon(key);
		}
	}

	/**
	 * Gets the icon.
	 *
	 * @param data the data
	 * @return the icon
	 */
	public BufferedImage getIcon(Integer data) {
		BufferedImage tiles = null;
		if (icons.get(data).filename == null) {
			ServerLogger.log("No filename found  " + icons.get(data).filename + "-->" + icons.get(data).subName);
			return null;
		}
		if (icons.get(data).filename.equalsIgnoreCase("terrain")) {
			tiles = imgTerrain;
		} else if (icons.get(data).filename.equalsIgnoreCase("items")) {
			tiles = imgItems;
		} else if (icons.get(data).filename.equalsIgnoreCase("special")) {
			tiles = imgSpecial;
		}
		if (tiles == null) {
			ServerLogger.log("Tiles not found " + icons.get(data).filename + "-->" + icons.get(data).subName);
			return null;
		}
		BufferedImage bi = tiles.getSubimage(icons.get(data).x * 16, icons.get(data).y * 16, 16, 16);
		if (scale) {
			AffineTransform tx = new AffineTransform();
			tx.scale(2, 2);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			BufferedImage biNew = new BufferedImage((int) (bi.getWidth() * 2), (int) (bi.getHeight() * 2), bi.getType());
			bi = op.filter(bi, biNew);
		}
		// ServerLogger.log("export X %d=>%d=>%d",mat.getId(),data,mat.getId()*16,Math.min(32,data)*16);

		Images.copySrcIntoDstAt(bi, allIcons, material.getId() * 16, Math.min(16, data) * 16);
		return bi;
	}

	/**
	 * Export icon.
	 *
	 * @param data the data
	 */
	private void exportIcon(Integer data) {
		File pngWriter = new File(String.format("%s/%s_%03d.png", path.getAbsoluteFile(), material.name(), data));
		if (this.icons.get(data).filename != null) {
			try {
				BufferedImage buf = getIcon(data);
				if (buf != null) {
					ImageIO.write(buf, "png", pngWriter);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Export data.
	 */
	public void exportData() {
		
		SubData sd = new SubData();

		sd.material = material;
		sd.maxStackSize = material.getMaxStackSize();
		sd.id = material.getId();
		sd.maxDurability = material.getMaxDurability();
		sd.isBlock = material.isBlock();
		sd.icons = icons;
		sd.listRecipes = plugin.getServer().getRecipesFor(new ItemStack(material, 1));
		//ServerLogger.log(sd.material.toString());
		Json.exportObjectToJson(path.getAbsoluteFile() + "/" + material.name() + ".json",sd);
		/*
		if (recipe == null) {

		} else if (recipe instanceof ShapelessRecipe) {
			ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
			//toJson();
			// ServerLogger.log("SHAPELESS-------------------");
			// ServerLogger.log(shapelessRecipe.getResult().toString());
			// ServerLogger.log(shapelessRecipe.getIngredientList().toString());
		} else if (recipe instanceof ShapedRecipe) {
			ShapedRecipe shappedRecipe = (ShapedRecipe) recipe;
			//toJson();
			// ServerLogger.log("SHAPED-------------------%s %d %d",
			// recipe.getResult().getType().name(),
			// recipe.getResult().getType().getId(),
			// recipe.getResult().getData().getData());
			// ServerLogger.log("{ChatColor.GREEN}result {ChatColor.DARK_RED} %s",
			// shappedRecipe.getResult().toString());
			// ServerLogger.log("{ChatColor.GREEN}map {ChatColor.RED} %s",
			// shappedRecipe.getIngredientMap().toString());
			// String[] map = shappedRecipe.getShape();
			// for (int i = 0; i < map.length; i++) {
			// ServerLogger.log("{ChatColor.GREEN}shape {ChatColor.BLUE} [%s]",
			// map[i]);
			// }
		}*/
	}

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
	 * Sets the item definitions.
	 *
	 * @param aitemDefinitions the aitem definitions
	 */
	public void setItemDefinitions(HashMap<Integer, ItemDefinition> aitemDefinitions) {
		itemDefinitions = aitemDefinitions;
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
			ServerLogger.log(ChatFormater.format("error reading images tiles :: %s", e.toString()));
			e.printStackTrace();
		}

		ServerLogger.log(ChatFormater.format("reading items.txt"));
		Integer maxId = parseItemsTxt(plugin.getClass().getClassLoader().getResourceAsStream("items.txt"));
		ItemDefinition.allIcons = new BufferedImage(16 * (maxId + 1), 16 * (16 + 1), ItemDefinition.imgTerrain.getType());
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
						if (ItemDefinition.getItemDefinitions().containsKey(id)) {
							def = ItemDefinition.getItemDefinitions().get(id);
						} else {
							def = new ItemDefinition(Material.getMaterial(id));
							ItemDefinition.getItemDefinitions().put(id, def);
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
						ServerLogger.log("%s %s %s %s", id, data, name, filename);
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return maxId;
	}

	/**
	 * Export datas.
	 */
	public static void exportDatas() {
		List<SubDataIt> listSd = new ArrayList<SubDataIt>();
		ServerLogger.log(ChatFormater.format("reading items.txt"));
		
		Iterator<Integer> iterator = ItemDefinition.getItemDefinitions().keySet().iterator();

		while (iterator.hasNext()) {
			Integer key = iterator.next();
			ItemDefinition rd = ItemDefinition.getItemDefinitions().get(key);
			ServerLogger.log("%s", rd.getMaterial().name());
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
			ImageIO.write(ItemDefinition.allIcons, "png", pngWriter);
			ImageIO.write(ItemDefinition.allIcons, "jpg", jpgWriter);
			ImageIO.write(ItemDefinition.allIcons, "gif", gifWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * List recipes.
	 */
	public void listRecipes() {
		Iterator<Recipe> iterator = plugin.getServer().recipeIterator();

		while (iterator.hasNext()) {
			Recipe recipeItem = iterator.next();
			ServerLogger.log("%s", recipeItem.getResult().getType().name());
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
