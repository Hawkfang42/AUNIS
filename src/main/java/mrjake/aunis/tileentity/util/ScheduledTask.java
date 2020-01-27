package mrjake.aunis.tileentity.util;

import mrjake.aunis.stargate.EnumScheduledTask;
import mrjake.aunis.tileentity.stargate.StargateBaseTileSG1;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Holds scheduled task to be performed some time in future on {@link ITickable#update()}.
 * 
 * Previously done by a shitload of variables in {@link StargateBaseTileSG1} and other like "waitForShit", "shitStateChange".
 * 
 * @author MrJake222
 */
public class ScheduledTask implements INBTSerializable<NBTTagCompound> {
	
	/**
	 * {@link TileEntity} to perform {@link IScheduledTaskExecutor#executeTask(EnumScheduledTask)} on.
	 */
	private IScheduledTaskExecutor executor;
	
	/**
	 * When the {@link ScheduledTask} was created.
	 */
	private long taskCreated;
	
	/**
	 * Task to be called when the time will come. Also holds the wait time.
	 */
	private EnumScheduledTask scheduledTask;
	
	/**
	 * Is this {@link ScheduledTask} actively called from the {@link ITickable#update()}?
	 */
	private boolean active;
	
	/**
	 * User can use {@link ScheduledTask#ScheduledTask(IScheduledTaskExecutor, long, EnumScheduledTask, int)} to set custom waiting time.
	 */
	private boolean customWaitTime;
	private int waitTime;
	
	/**
	 * Main constructor
	 * 
	 * @param taskCreated When the {@link ScheduledTask} was created.
	 * @param scheduledTask Task to perform.
	 */
	public ScheduledTask(IScheduledTaskExecutor executor, long taskCreated, EnumScheduledTask scheduledTask) {
		this.executor = executor;
		this.taskCreated = taskCreated;
		this.scheduledTask = scheduledTask;
		this.active = true;
		
		this.customWaitTime = false;
	}
	
	public ScheduledTask(IScheduledTaskExecutor executor, long taskCreated, EnumScheduledTask scheduledTask, int waitTime) {
		this(executor, taskCreated, scheduledTask);
		
		this.customWaitTime = true;
		this.waitTime = waitTime;
	}
	
	public ScheduledTask(IScheduledTaskExecutor executor, NBTTagCompound compound) {
		this.executor = executor;
		
		deserializeNBT(compound);
	}
	
	/**
	 * Mark this {@link ScheduledTask} inactive.
	 * Prevents {@link ScheduledTask#activate(long, double)} from being called from the {@link ITickable#update()}.
	 * 
	 * @return This instance.
	 */
	public ScheduledTask inactive() {
		this.active = false;
		
		return this;
	}
	
	/**
	 * Mark this {@link ScheduledTask} active.
	 * @see Activation#inactive().
	 * 
	 * @return This instance.
	 */
	public ScheduledTask active() {
		this.active = true;
		
		return this;
	}
	
	/**
	 * Getter for active
	 * 
	 * @return active state.
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Main waiting function. Call this in {@link ITickable#update()}.
	 * 
	 * @param worldTicks Usually {@link World#getTotalWorldTime()}.
	 * 
	 * If this {@link ScheduledTask} should be removed.
	 */
	public boolean update(long worldTicks) {
		int waitTime = customWaitTime ? this.waitTime : scheduledTask.waitTicks;
		
		if (worldTicks-taskCreated >= waitTime) {
			try {
				executor.executeTask(scheduledTask);
			}
			
			catch (UnsupportedOperationException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setLong("taskCreated", taskCreated);
		compound.setInteger("scheduledTask", scheduledTask.id);
		compound.setBoolean("active", active);
		
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		taskCreated = compound.getLong("taskCreated");
		scheduledTask = EnumScheduledTask.valueOf(compound.getInteger("scheduledTask"));
		active = compound.getBoolean("active");
	}

	// Eclipse generated methods
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scheduledTask == null) ? 0 : scheduledTask.hashCode());
		result = prime * result + (int) (taskCreated ^ (taskCreated >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduledTask other = (ScheduledTask) obj;
		if (scheduledTask != other.scheduledTask)
			return false;
		if (taskCreated != other.taskCreated)
			return false;
		return true;
	}	
}
