package mrjake.aunis.item;

import net.minecraft.item.Item;

public class AunisItems {
	/**
	 * Provides basic analyzing info
	 */
	public static ItemBase analyzerAncient = new ItemBase("analyzer_ancient");
	
	/**
	 * Crafting of the Stargate
	 */
	public static ItemBase crystalControlDhd = new ItemBase("crystal_control_dhd");
	public static ItemBase crystalControlEmpty = new ItemBase("crystal_control_empty");
	public static ItemBase crystalControlRing = new ItemBase("crystal_control_ring");
	public static ItemBase crystalControlStargate = new ItemBase("crystal_control_stargate");
	
	/**
	 * These allow for dialing 8th glyph(cross dimension travel)
	 */
	public static ItemBase crystalGlyphDhd = new ItemBase("crystal_glyph_dhd");
	public static ItemBase crystalGlyphEmpty = new ItemBase("crystal_glyph_empty");
	public static ItemBase crystalGlyphStargate = new ItemBase("crystal_glyph_stargate");
	
	/**
	 * Used for fast dialing the gate
	 * To be removed in final edition
	 */
	public static ItemBase dialerFast = new ItemBase("dialer_fast");
	
	/**
	 * Diffrent Naquadah(main Stargate building material) stages of purity
	 */
	public static ItemBase naquadahOrePure = new ItemBase("naquadah_ore_pure");
	public static ItemBase naquadahOreShard = new ItemBase("naquadah_ore_shard");
	public static ItemBase naquadahRefined = new ItemBase("naquadah_refined");
	
	public static ItemBase stargateRingItem = new ItemBase("stargate_ring_item");
	
	public static Item[] items = {
		analyzerAncient,
		
		crystalControlDhd,
		crystalControlEmpty,
		crystalControlRing,
		crystalControlStargate,
		
		crystalGlyphDhd,
		crystalGlyphEmpty,
		crystalGlyphStargate,
		
		dialerFast,
		
		naquadahOrePure,
		naquadahOreShard,
		naquadahRefined,
		
		stargateRingItem
	};
}
