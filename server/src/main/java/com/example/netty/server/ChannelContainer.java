package com.example.netty.server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelContainer {
    private static final ConcurrentHashMap<String, Channel> map = new ConcurrentHashMap();

    private ChannelContainer() {

    }

    public static void put(String id, Channel channel) {
        map.put(id, channel);
    }

    public static Channel get(String id) {
        return map.get(id);
    }

    public static void remove(String id, Channel channel) {
        map.remove(id);
    }

    public static String list() {
        return JSON.toJSONString(map.keys());
    }
}
