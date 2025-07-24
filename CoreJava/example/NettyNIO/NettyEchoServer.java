package example.NettyNIO;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

class NettyEchoServer {
    public static void main(String[] args) throws InterruptedException {
        int port = 8080;

        EventLoopGroup bossGroup = new NioEventLoopGroup(); // Accepts connections
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // Handles data

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class) // NIO server socket
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) {
                             ch.pipeline().addLast(new EchoServerHandler());
                         }
                     });

            ChannelFuture f = bootstrap.bind(port).sync();
            System.out.println("Server started on port " + port);
            f.channel().closeFuture().sync(); // Wait until server socket is closed
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Server received: " + msg);
        ctx.write(msg); // Echo back
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush(); // Send response
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // Close connection on error
    }
}

//  <dependency>
//     <groupId>io.netty</groupId>
//     <artifactId>netty-all</artifactId>
//     <version>4.1.68.Final</version>
// </dependency>