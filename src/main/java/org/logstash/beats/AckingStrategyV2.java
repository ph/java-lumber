package org.logstash.beats;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

public class AckingStrategyV2 extends AckingStrategy {
    private static Logger logger = Logger.getLogger(Server.class.getName());

    private static Byte[] keepAliveResponse = new Byte[] { '2', 'A', '0'};

    public void ack(Message message, ChannelHandlerContext ctx) {
        if(ackNeeded(message)) {
            ByteBuf buffer = ctx.alloc().buffer(6);
            buffer.writeByte('2');
            buffer.writeByte('A');
            buffer.writeInt(message.getSequence());
            ctx.writeAndFlush(buffer);
            logger.debug("Ack on sequence: " + message.getSequence());
        }
    }

    private boolean ackNeeded(Message message) {
        if(message.getPayload().getWindowSize() == message.getSequence()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void keepAlive(ChannelHandlerContext ctx) {
        ctx.write(keepAliveResponse);
    }
}