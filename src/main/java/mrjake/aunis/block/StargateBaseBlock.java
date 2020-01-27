package mrjake.aunis.block;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.stargate.BoundingHelper;
import mrjake.aunis.stargate.MergeHelper;
import mrjake.aunis.stargate.StargateNetwork;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.DHDTile;
import mrjake.aunis.tileentity.stargate.StargateBaseTileSG1;
import mrjake.aunis.upgrade.ITileEntityUpgradeable;
import mrjake.aunis.upgrade.UpgradeHelper;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class StargateBaseBlock extends Block {

	private static final String blockName = "stargatebase_block";
	
	public StargateBaseBlock() {		
		super(Material.IRON);
		
		setRegistryName(Aunis.ModID + ":" + blockName);
		setTranslationKey(Aunis.ModID + "." + blockName);
		
		setSoundType(SoundType.METAL); 
		setCreativeTab(Aunis.aunisCreativeTab);
		
		setDefaultState(blockState.getBaseState()
				.withProperty(AunisProps.FACING_HORIZONTAL, EnumFacing.NORTH)
				.withProperty(AunisProps.RENDER_BLOCK, true));
		
		setLightOpacity(0);
		
		setHardness(3.0f);
		setHarvestLevel("pickaxe", 3);
	}
	
	// ------------------------------------------------------------------------
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AunisProps.FACING_HORIZONTAL, AunisProps.RENDER_BLOCK);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {		
		return (state.getValue(AunisProps.RENDER_BLOCK) ? 0x04 : 0) |
				state.getValue(AunisProps.FACING_HORIZONTAL).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {		
		return getDefaultState()
				.withProperty(AunisProps.RENDER_BLOCK, (meta & 0x04) != 0)
				.withProperty(AunisProps.FACING_HORIZONTAL, EnumFacing.byHorizontalIndex(meta & 0x03));
	}
	
	// ------------------------------------------------------------------------	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		Aunis.info("onBlockPlacedBy");
		
		if (!world.isRemote) {
			state = state.withProperty(AunisProps.FACING_HORIZONTAL, placer.getHorizontalFacing().getOpposite())
					.withProperty(AunisProps.RENDER_BLOCK, true); // 2 - send update to clients
		
			world.setBlockState(pos, state);
					
			StargateBaseTileSG1 gateTile = (StargateBaseTileSG1) world.getTileEntity(pos);
//			MergeHelper.updateChevRingMergeState(world, pos, false);
			gateTile.updateMergeState(MergeHelper.checkBlocks(world, pos), state);
		
			// ------------------------------------------------------------------------
			state = world.getBlockState(pos);
			
			if (!state.getValue(AunisProps.RENDER_BLOCK)) {				
				BlockPos closestDhd = LinkingHelper.findClosestUnlinked(world, pos, LinkingHelper.getDhdRange(), AunisBlocks.dhdBlock);
				
				if (closestDhd != null) {
					DHDTile dhdTile = (DHDTile) world.getTileEntity(closestDhd);
					
					dhdTile.setLinkedGate(pos);
					gateTile.setLinkedDHD(closestDhd);
				}
			}
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {		
		StargateBaseTileSG1 gateTile = (StargateBaseTileSG1) world.getTileEntity(pos);
						
		if (!world.isRemote) {
			gateTile.updateMergeState(false, state);
			
//			DHDTile linkedDhdTile = gateTile.getLinkedDHD(world);
//			if (linkedDhdTile != null)
//				linkedDhdTile.setLinkedGate(null);
			
			StargateNetwork.get(world).removeStargate(gateTile.gateAddress);
			
			// Supports upgrades
			if (gateTile instanceof ITileEntityUpgradeable) {			
				if (gateTile.hasUpgrade() || gateTile.getUpgradeRendererState().doInsertAnimation) {
					InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(AunisItems.crystalGlyphStargate));
				}
			}
		}
		
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		StargateBaseTileSG1 gateTile = (StargateBaseTileSG1) world.getTileEntity(pos);
		ItemStack heldItem = player.getHeldItem(hand);
		
		// Server side
		if (!world.isRemote) {			
			if (heldItem.getItem() == AunisItems.analyzerAncient) {
				AunisPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(gateTile.getPos(), StateTypeEnum.GUI_STATE, gateTile.getState(StateTypeEnum.GUI_STATE)), (EntityPlayerMP) player);
								
				return true;
			}
			
			else if (heldItem.getItem() == AunisItems.dialerFast) {				
				NBTTagCompound compound = heldItem.getTagCompound();
				if (compound == null) 
					compound = new NBTTagCompound();
				
				byte[] symbols = new byte[gateTile.gateAddress.size()];
				
				for (int i=0; i<gateTile.gateAddress.size(); i++)
					symbols[i] = (byte) gateTile.gateAddress.get(i).id;
				
				compound.setByteArray("address", symbols);
				
				heldItem.setTagCompound(compound);
				
				return true;
			}
			
			else if (!state.getValue(AunisProps.RENDER_BLOCK) && hand == EnumHand.MAIN_HAND) {
				return UpgradeHelper.upgradeInteract((EntityPlayerMP) player, gateTile, heldItem);
			}
			
			return false;
		}
		
		// Client side
		else {
			// Aunis.info("horizontalRotation: " + gateTile.getRenderer().byHorizontalIndexRotation());
			
			return  heldItem.getItem() == AunisItems.analyzerAncient ||
					heldItem.getItem() == AunisItems.dialerFast || 
					heldItem.getItem() == AunisItems.crystalGlyphStargate || 
					heldItem.getItem() == AunisItems.pageNotebookItem || 
					heldItem.getItem() == Items.AIR;
		}
	}
	
	// ------------------------------------------------------------------------
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public StargateBaseTileSG1 createTileEntity(World world, IBlockState state) {
		return new StargateBaseTileSG1();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		// Client side
		
		if ( state.getValue(AunisProps.RENDER_BLOCK) )
			return EnumBlockRenderType.MODEL;
		else
			return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		return BoundingHelper.getStargateBlockBoundingBox(state, access, pos);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		return BoundingHelper.getStargateBlockBoundingBox(state, access, pos);
	}
}
