package example.NettyNIO;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class NettyEchoClient {
    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 8080;

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class) // NIO client socket
             .handler(new ChannelInitializer<Channel>() {
                 @Override
                 protected void initChannel(Channel ch) {
                     ch.pipeline().addLast(new EchoClientHandler());
                 }
             });

            Channel ch = b.connect(host, port).sync().channel();
            ch.writeAndFlush(Unpooled.copiedBuffer("Hello from client", CharsetUtil.UTF_8));
            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}

class EchoClientHandler extends SimpleChannelInboundHandler<io.netty.buffer.ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, io.netty.buffer.ByteBuf msg) {
        System.out.println("Client received: " + msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
