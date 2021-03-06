/*
 * Copyright 2014 University of Lancaster
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leafgraph.flowdam.netty;

import com.leafgraph.flowdam.proxy.Proxy;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * OpenFlowChannelInitializer provides the facility to setup up a Netty connection capable of processing OpenFlow
 * messages.
 */
public class OpenFlowChannelInitializer extends ChannelInitializer<SocketChannel> {
    /** Maximum possible length of a single OpenFlow message, as dictated by using uint16_t in the header. */
    public static int OPENFLOW_MAXIMUM_FRAME = (int) Math.pow(2, 16);

    /** Proxy to handle new connections initialized by this. */
    private Proxy proxy;
    /** If this Initializer is responsible for creating downstream channels. */
    private boolean downstream;

    /**
     * Constructs a new ChannelInitializer ready for initializing channels.
     *
     * @param proxy the proxy object that new channels belong to
     * @param downstream if channels created by this initializer are downsteam, if not then using a different last
     *                   entry in the pipeline
     */
    public OpenFlowChannelInitializer(Proxy proxy, boolean downstream) {
        this.proxy = proxy;
        this.downstream = downstream;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        /* Use Netty's prebuilt tools to handle frame separation on incoming data. */
        pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(OPENFLOW_MAXIMUM_FRAME, 2, 2, -4, 0));

        /* Process OpenFlow packets. */
        pipeline.addLast("openflowDecoder", new OpenFlowDecoder());
        pipeline.addLast("openflowEncoder", new OpenFlowEncoder());

        /* Idle Handler, prevent a hung switch or controller from disrupting traffic.  */
        pipeline.addLast("idleStateHandler", new IdleStateHandler(proxy.getIdleReadTimeout(), proxy.getIdleWriteTimeout(), 0, TimeUnit.MILLISECONDS));

        /* OpenFlow Processor. */
        if (downstream) {
            pipeline.addLast("messageHandler", new OpenFlowChannelInboundDownstreamHandler(proxy));
        } else {
            pipeline.addLast("messageHandler", new OpenFlowChannelInboundUpstreamHandler(proxy));
        }
    }
}
