package mrjake.aunis.tileentity.stargate;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.stargate.EnumMemberVariant;
import mrjake.aunis.state.StargateCamoState;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.StateProviderInterface;
import mrjake.aunis.state.StargateLightState;
import mrjake.aunis.state.State;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

/**
 * TileEntity for ring blocks and chevron blocks
 * 
 * Holds the camouflage block in {@link ItemStackHandler}, providing it into {@link Block#getExtendedState()}
 * 
 * @author MrJake
 */
public class StargateMemberTile extends TileEntity implements ITickable, StateProviderInterface {
	
	boolean firstTick = true;
	private boolean waitForClear = false;
	private long clearWaitStarted;
	
	@Override
	public void update() {

		if (firstTick) {
			firstTick = false;
			
			if (world.isRemote) {				
				AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, Aunis.proxy.getPlayerClientSide(), StateTypeEnum.CAMO_STATE));
				AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, Aunis.proxy.getPlayerClientSide(), StateTypeEnum.LIGHT_STATE));
			}
		}
		
		
		if (!world.isRemote) {
			if (waitForClear) {
				if (world.getTotalWorldTime() - clearWaitStarted >= 40) {
					waitForClear = false;
					
					syncLightUp();
				}
			}
		}
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Is chevron block emitting light
	 */
	private boolean isLitUp;
	
	public void syncLightUp() {
		TargetPoint point = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
		AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.LIGHT_STATE, getState(StateTypeEnum.LIGHT_STATE)), point);
	}
	
	public void setLitUp(boolean isLitUp) {
		this.isLitUp = isLitUp;

		
		if (isLitUp)
			syncLightUp();
		
		else {
			clearWaitStarted = world.getTotalWorldTime();
			waitForClear = true;
		}
		
//		world.notifyLightSet(pos);
//		world.setBlockState(pos, world.getBlockState(pos).withProperty(AunisProps.LIT_UP, isLitUp), 3);
		
		markDirty();
	}
	
	public boolean isLitUp(IBlockState state) {
		return state.getValue(AunisProps.MEMBER_VARIANT) == EnumMemberVariant.CHEVRON && isLitUp;
	}
	
	
	// ---------------------------------------------------------------------------------	
	private IBlockState camoBlockState;
	
	public void setCamoState(IBlockState doubleSlabState) {
		this.camoBlockState = doubleSlabState;
		
		markDirty();
	}
	
	public IBlockState getCamoState() {
		return camoBlockState;
	}
	
	public ItemStack getCamoItemStack() {
		if (camoBlockState != null) {
			Block block = camoBlockState.getBlock();
			int quantity = 1;
			int meta;
			
			if (block instanceof BlockSlab && ((BlockSlab) block).isDouble()) {
				quantity = 2;
				meta = block.getMetaFromState(camoBlockState);
				
				if (block == Blocks.DOUBLE_STONE_SLAB)
					block = Blocks.STONE_SLAB;
				
				else if (block == Blocks.DOUBLE_STONE_SLAB2)
					block = Blocks.STONE_SLAB2;
				
				else if (block == Blocks.DOUBLE_WOODEN_SLAB)
					block = Blocks.WOODEN_SLAB;
				
				else if (block == Blocks.PURPUR_DOUBLE_SLAB)
					block = Blocks.PURPUR_SLAB;
			}
			
			else {
				meta = block.getMetaFromState(camoBlockState);
			}
			
			return new ItemStack(block, quantity, meta);
		}
		
		else {
			return null;
		}
	}
	
	// ---------------------------------------------------------------------------------
	private BlockPos basePos;
	
	public boolean isMerged() {
		return basePos != null;
	}
	
	public BlockPos getBasePos() {
		return basePos;
	}
	
	public StargateBaseTileSG1 getBaseTile(World world) {
		if (basePos != null)
			return (StargateBaseTileSG1) world.getTileEntity(basePos);
		
		return null;
	}
	
	public void setBasePos(BlockPos basePos) {
		this.basePos = basePos;
		
		markDirty();
	}
	// ---------------------------------------------------------------------------------
	// NBT
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("isLitUp", isLitUp);		
		
		if (basePos != null)
			compound.setLong("basePos", basePos.toLong());
		
		if (camoBlockState != null) {
			compound.setString("doubleSlabBlock", camoBlockState.getBlock().getRegistryName().toString());
			compound.setInteger("doubleSlabMeta", camoBlockState.getBlock().getMetaFromState(camoBlockState));
		}
		
		return super.writeToNBT(compound);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void readFromNBT(NBTTagCompound compound) {		
		isLitUp = compound.getBoolean("isLitUp");
		
		if (compound.hasKey("basePos"))
			basePos = BlockPos.fromLong(compound.getLong("basePos"));
		
		if (compound.hasKey("doubleSlabBlock")) {
			Block dblSlabBlock = Block.getBlockFromName(compound.getString("doubleSlabBlock"));
			camoBlockState = dblSlabBlock.getStateFromMeta(compound.getInteger("doubleSlabMeta"));
		}
		
		super.readFromNBT(compound);
	}
	
	
	// ---------------------------------------------------------------------------------
	// States
	
	@Override
	public State getState(StateTypeEnum stateType) {
		switch (stateType) {
			case CAMO_STATE:				
				return new StargateCamoState(camoBlockState);
				
			case LIGHT_STATE:
				return new StargateLightState(isLitUp);
				
			default:
				return null;
		}		
	}
	@Override
	public State createState(StateTypeEnum stateType) {
		switch (stateType) {
			case CAMO_STATE:
				return new StargateCamoState();
				
			case LIGHT_STATE:
				return new StargateLightState();
				
			default:
				return null;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setState(StateTypeEnum stateType, State state) {
		switch (stateType) {
			case CAMO_STATE:
				StargateCamoState memberState = (StargateCamoState) state;				
				camoBlockState = memberState.getState();
				
				world.markBlockRangeForRenderUpdate(pos, pos);
				break;
				
			case LIGHT_STATE:
				isLitUp = ((StargateLightState) state).isLitUp();
				world.notifyLightSet(pos);
				world.checkLightFor(EnumSkyBlock.BLOCK, pos);
				
				break;
				
			default:
				break;
		}
	}	
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
