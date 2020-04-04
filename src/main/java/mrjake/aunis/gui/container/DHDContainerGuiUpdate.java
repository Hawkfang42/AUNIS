package mrjake.aunis.gui.container;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.state.State;
import mrjake.aunis.tileentity.util.ReactorStateEnum;

public class DHDContainerGuiUpdate extends State {
	public DHDContainerGuiUpdate() {}
	
	public int fluidAmount;
	public int tankCapacity;
	public ReactorStateEnum reactorState;
	
	public DHDContainerGuiUpdate(int fluidAmount, int tankCapacity, ReactorStateEnum reactorState) {
		this.fluidAmount = fluidAmount;
		this.tankCapacity = tankCapacity;
		this.reactorState = reactorState;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(fluidAmount);
		buf.writeInt(tankCapacity);
		buf.writeShort(reactorState.getKey());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		fluidAmount = buf.readInt();
		tankCapacity = buf.readInt();
		reactorState = ReactorStateEnum.valueOf(buf.readShort());
	}

}
