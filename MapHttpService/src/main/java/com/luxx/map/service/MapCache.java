package com.luxx.map.service;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map服务，地图缓存
 *
 * @author luxiaoxun
 * @version 1.0
 * @since 2015.06.1
 */
public class MapCache {
    private static Logger log = LoggerFactory.getLogger(MapCache.class);

    private Cache<MapRequestParam, byte[]> mapCacheLoader = null;

    private static class MapCacheHolder {
        private static final MapCache instance = new MapCache();
    }

    public static MapCache getInstance() {
        return MapCacheHolder.instance;
    }

    private MapCache() {
        // mapCacheLoader =
        // CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(5,
        // TimeUnit.MINUTES).build();
        mapCacheLoader = CacheBuilder.newBuilder().maximumSize(5000).build();
    }

    public byte[] getMapCacheTile(final MapRequestParam mapRequestParam) {
        byte[] tileBytes = null;
        try {
            tileBytes = mapCacheLoader.get(mapRequestParam, new Callable<byte[]>() {
                public byte[] call() throws SQLException {
                    return getMapTile(mapRequestParam);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return tileBytes;
    }

    private byte[] getMapTile(final MapRequestParam mapRequestParam) throws SQLException {
        byte[] allBytesInBlob = null;
        if (mapRequestParam.isOk()) {
            String dbId = mapRequestParam.getDbType();
            int zoom = mapRequestParam.getZoom();
            int x = mapRequestParam.getX();
            int y = mapRequestParam.getY();
            allBytesInBlob = MapDbOperation.getTile(x, y, zoom, dbId);
        }
        return allBytesInBlob;
    }

}
