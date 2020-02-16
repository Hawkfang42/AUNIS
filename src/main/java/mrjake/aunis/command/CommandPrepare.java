package mrjake.aunis.command;

import mrjake.aunis.tileentity.util.PreparableInterface;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;

public class CommandPrepare extends CommandBase {

	@Override
	public String getName() {
		return "sgprepare";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/sgprepare";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {	
		EntityPlayerMP player = (EntityPlayerMP) sender;
		RayTraceResult rayTraceResult = player.rayTrace(8, 0);
		
		if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
			TileEntity tileEntity = sender.getEntityWorld().getTileEntity(rayTraceResult.getBlockPos());
			
			if (tileEntity instanceof PreparableInterface) {
				((PreparableInterface) tileEntity).prepare();
				
				notifyCommandListener(sender, this, "Preparing "+tileEntity.getClass().getSimpleName());
			}
	
			else
				notifyCommandListener(sender, this, "Not a instance of PreparableInterface");
		}
	}
}
