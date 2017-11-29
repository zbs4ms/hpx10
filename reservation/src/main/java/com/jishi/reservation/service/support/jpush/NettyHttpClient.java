package com.jishi.reservation.service.support.jpush;

/**
 * Created by liangxiong on 2017/11/24.
 */
import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.connection.NettyClientInitializer;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import javax.net.ssl.SSLException;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpClient{
  private static Logger LOG = LoggerFactory.getLogger(NettyHttpClient.class);
  private String _authCode;
  private int _maxRetryTimes;
  private int _readTimeout;
  private Channel _channel;
  private Bootstrap b;
  private EventLoopGroup _workerGroup;
  private SslContext _sslCtx;
  private URI _uri;

  public NettyHttpClient(String authCode, HttpProxy proxy, ClientConfig config, URI uri, NettyHttpClient.BaseCallback callback) {
    this._maxRetryTimes = config.getMaxRetryTimes().intValue();
    this._readTimeout = config.getReadTimeout().intValue();
    String message = MessageFormat.format("Created instance with connectionTimeout {0}, readTimeout {1}, maxRetryTimes {2}, SSL Version {3}", config.getConnectionTimeout(), this._readTimeout, this._maxRetryTimes, config.getSSLVersion());
    LOG.info(message);
    this._authCode = authCode;

    try {
      this._sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
      this._workerGroup = new NioEventLoopGroup();
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(this._workerGroup)
              .channel(NioSocketChannel.class)
              .option(ChannelOption.SO_KEEPALIVE, true)
              .handler(new NettyClientInitializer(this._sslCtx, callback, (CountDownLatch)null));
      this.b = bootstrap;

      this._uri = uri;
      String scheme = _uri.getScheme() == null ? "http" : _uri.getScheme();
      int port = _uri.getPort();
      if (port == -1) {
        if ("http".equalsIgnoreCase(scheme)) {
          port = 80;
        } else if ("https".equalsIgnoreCase(scheme)) {
          port = 443;
        }
      }

      this._channel = this.b.connect(_uri.getHost(), port).channel();
    } catch (SSLException var6) {
      var6.printStackTrace();
    }

  }

  public void sendRequest(HttpMethod method, String content) {

    DefaultFullHttpRequest request;
    if (null != content) {
      ByteBuf byteBuf = Unpooled.copiedBuffer(content.getBytes(CharsetUtil.UTF_8));
      request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, _uri.getRawPath(), byteBuf);
      request.headers().set(HttpHeaderNames.CONTENT_LENGTH, (long)byteBuf.readableBytes());
    } else {
      request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, _uri.getRawPath());
    }

    request.headers().set(HttpHeaderNames.HOST, _uri.getHost());
    request.headers().set(HttpHeaderNames.AUTHORIZATION, this._authCode);
    request.headers().set("Content-Type", "application/json;charset=utf-8");
    LOG.info("Sending request. " + request);
    LOG.info("Send body: " + content);
    this._channel.writeAndFlush(request);

  }

  public void addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
    this._channel.closeFuture().addListener(var1);
  }

  public void close() {
    if (null != this._channel) {
      this._channel.closeFuture().syncUninterruptibly();
      this._workerGroup.shutdownGracefully();
      this._channel = null;
      this._workerGroup = null;
    }

    System.out.println("Finished request(s) " + new Date());
  }

  public interface BaseCallback extends cn.jiguang.common.connection.NettyHttpClient.BaseCallback {
    void onSucceed(ResponseWrapper var1);
  }
}
