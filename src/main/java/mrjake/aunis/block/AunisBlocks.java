package mrjake.aunis.block;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.item.StargateMemberItemBlock;
import mrjake.aunis.stargate.EnumMemberVariant;
import mrjake.aunis.tileentity.CrystalInfuserTile;
import mrjake.aunis.tileentity.DHDTile;
import mrjake.aunis.tileentity.TRControllerTile;
import mrjake.aunis.tileentity.TransportRingsTile;
import mrjake.aunis.tileentity.stargate.StargateMemberTile;
import mrjake.aunis.tileentity.stargate.StargateBaseTileOrlin;
import mrjake.aunis.tileentity.stargate.StargateBaseTileSG1;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class AunisBlocks {
	public static NaquadahOreBlock naquadahOreBlock = new NaquadahOreBlock();
	public static Block naquadahBlock = new Block(Material.IRON).setRegistryName(Aunis.ModID, "naquadah_block").setTranslationKey(Aunis.ModID + ".naquadah_block");
	
	public static StargateBaseBlock stargateBaseBlock = new StargateBaseBlock();	
	public static StargateOrlinBlock stargateOrlinBlock = new StargateOrlinBlock();	
	public static StargateMemberBlockOrlin stargateMemberBlockOrlin = new StargateMemberBlockOrlin();	
	
	public static DHDBlock dhdBlock = new DHDBlock();
	public static CrystalInfuserBlock crystalInfuserBlock = new CrystalInfuserBlock();
	
	public static TransportRingsBlock transportRingsBlock = new TransportRingsBlock();
	public static TRControllerBlock trControllerBlock = new TRControllerBlock();
	public static InvisibleBlock invisibleBlock = new InvisibleBlock();	
	
	// -----------------------------------------------------------------------------
	public static StargateMemberBlock stargateMemberBlock = new StargateMemberBlock();
	
	
	private static Block[] blocks = {
		naquadahOreBlock,
		naquadahBlock,
		
		stargateBaseBlock,
		stargateOrlinBlock,
		stargateMemberBlockOrlin,
		
		dhdBlock,
		crystalInfuserBlock,
		
		transportRingsBlock,
		trControllerBlock,
		invisibleBlock
	};
	
	@SubscribeEvent
	public static void onRegisterBlocks(Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		
		registry.registerAll(blocks);
		registry.register(stargateMemberBlock);
		
		GameRegistry.registerTileEntity(StargateBaseTileSG1.class, AunisBlocks.stargateBaseBlock.getRegistryName());
		GameRegistry.registerTileEntity(StargateBaseTileOrlin.class, AunisBlocks.stargateOrlinBlock.getRegistryName());
		
		GameRegistry.registerTileEntity(StargateMemberTile.class, AunisBlocks.stargateMemberBlock.getRegistryName());
		GameRegistry.registerTileEntity(DHDTile.class, AunisBlocks.dhdBlock.getRegistryName());
		GameRegistry.registerTileEntity(CrystalInfuserTile.class, AunisBlocks.crystalInfuserBlock.getRegistryName());
		GameRegistry.registerTileEntity(TransportRingsTile.class, AunisBlocks.transportRingsBlock.getRegistryName());
		GameRegistry.registerTileEntity(TRControllerTile.class, AunisBlocks.trControllerBlock.getRegistryName());
	}
	
	@SubscribeEvent
	public static void onRegisterItems(Register<Item> event) {	
		IForgeRegistry<Item> registry = event.getRegistry();
		
		for (Block block : blocks)
			registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		
		registry.register(new StargateMemberItemBlock(stargateMemberBlock));		
	}
	
	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event) {
		for (Block block : blocks) {			
			ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
		}
		
		int ringMeta = stargateMemberBlock.getMetaFromState(stargateMemberBlock.getDefaultState().withProperty(AunisProps.MEMBER_VARIANT, EnumMemberVariant.RING));
		int chevronMeta = stargateMemberBlock.getMetaFromState(stargateMemberBlock.getDefaultState().withProperty(AunisProps.MEMBER_VARIANT, EnumMemberVariant.CHEVRON));
		
		ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(stargateMemberBlock), ringMeta, new ModelResourceLocation("aunis:stargate_member_block_ring"));
		ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(stargateMemberBlock), chevronMeta, new ModelResourceLocation("aunis:stargate_member_block_chevron"));
	}
}

