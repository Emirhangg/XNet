package mcjty.xnet.network;

import mcjty.lib.network.ICommandHandler;
import mcjty.lib.network.PacketRequestListFromServer;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.clientinfo.ConnectedBlockClientInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketGetConnectedBlocks extends PacketRequestListFromServer<ConnectedBlockClientInfo, PacketGetConnectedBlocks, PacketConnectedBlocksReady> {

    public PacketGetConnectedBlocks() {

    }

    public PacketGetConnectedBlocks(BlockPos pos) {
        super(XNet.MODID, pos, TileEntityController.CMD_GETCONNECTEDBLOCKS, TypedMap.EMPTY);
    }

    public static class Handler implements IMessageHandler<PacketGetConnectedBlocks, IMessage> {
        @Override
        public IMessage onMessage(PacketGetConnectedBlocks message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetConnectedBlocks message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
            ICommandHandler commandHandler = (ICommandHandler) te;
            List<ConnectedBlockClientInfo> list = commandHandler.executeWithResultList(message.command, message.params, Type.create(ConnectedBlockClientInfo.class));
            XNetMessages.INSTANCE.sendTo(new PacketConnectedBlocksReady(message.pos, TileEntityController.CLIENTCMD_CONNECTEDBLOCKSREADY, list), ctx.getServerHandler().player);
        }
    }

}
