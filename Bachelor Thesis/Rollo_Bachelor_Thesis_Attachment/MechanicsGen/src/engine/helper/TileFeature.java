package engine.helper;

import java.util.Collections;
import java.util.List;

public enum TileFeature {
    BLOCK_UPPER,
    BLOCK_ALL,
    BLOCK_LOWER,
    SPECIAL,
    LIFE,
    BUMPABLE,
    BREAKABLE,
    PICKABLE,
    ANIMATED,
    SPAWNER;

    private static final List<TileFeature>[] TILE_FEATURES_CACHE;

    static {
        List<TileFeature>[] tempCache = new List[54];
        TILE_FEATURES_CACHE = tempCache;

        for (int i = 0; i < TILE_FEATURES_CACHE.length; i++) {
            TILE_FEATURES_CACHE[i] = Collections.emptyList();
        }

        List<TileFeature> blockAll = List.copyOf(List.of(TileFeature.BLOCK_ALL));
        TILE_FEATURES_CACHE[1] = blockAll;
        TILE_FEATURES_CACHE[2] = blockAll;
        TILE_FEATURES_CACHE[14] = blockAll;
        TILE_FEATURES_CACHE[18] = blockAll;
        TILE_FEATURES_CACHE[19] = blockAll;
        TILE_FEATURES_CACHE[20] = blockAll;
        TILE_FEATURES_CACHE[21] = blockAll;
        TILE_FEATURES_CACHE[4] = blockAll;
        TILE_FEATURES_CACHE[5] = blockAll;
        TILE_FEATURES_CACHE[52] = blockAll;
        TILE_FEATURES_CACHE[53] = blockAll;

        List<TileFeature> blockLower = List.copyOf(List.of(TileFeature.BLOCK_LOWER));
        TILE_FEATURES_CACHE[43] = blockLower;
        TILE_FEATURES_CACHE[44] = blockLower;
        TILE_FEATURES_CACHE[45] = blockLower;
        TILE_FEATURES_CACHE[46] = blockLower;

        TILE_FEATURES_CACHE[48] = List.copyOf(List.of(
                TileFeature.BLOCK_UPPER, TileFeature.LIFE, TileFeature.BUMPABLE
        ));

        TILE_FEATURES_CACHE[49] = List.copyOf(List.of(
                TileFeature.BUMPABLE, TileFeature.BLOCK_UPPER
        ));

        TILE_FEATURES_CACHE[3] = List.copyOf(List.of(
                TileFeature.BLOCK_ALL, TileFeature.SPAWNER
        ));

        TILE_FEATURES_CACHE[8] = List.copyOf(List.of(
                TileFeature.BLOCK_ALL, TileFeature.SPECIAL, TileFeature.BUMPABLE, TileFeature.ANIMATED
        ));

        TILE_FEATURES_CACHE[11] = List.copyOf(List.of(
                TileFeature.BLOCK_ALL, TileFeature.BUMPABLE, TileFeature.ANIMATED
        ));

        TILE_FEATURES_CACHE[6] = List.copyOf(List.of(
                TileFeature.BLOCK_ALL, TileFeature.BREAKABLE
        ));

        TILE_FEATURES_CACHE[7] = List.copyOf(List.of(
                TileFeature.BLOCK_ALL, TileFeature.BUMPABLE
        ));

        TILE_FEATURES_CACHE[15] = List.copyOf(List.of(
                TileFeature.PICKABLE, TileFeature.ANIMATED
        ));

        TILE_FEATURES_CACHE[50] = List.copyOf(List.of(
                TileFeature.BLOCK_ALL, TileFeature.SPECIAL, TileFeature.BUMPABLE
        ));

        TILE_FEATURES_CACHE[51] = List.copyOf(List.of(
                TileFeature.BLOCK_ALL, TileFeature.LIFE, TileFeature.BUMPABLE
        ));
    }

    public static List<TileFeature> getTileType(int index) {
        if (index < 0 || index >= TILE_FEATURES_CACHE.length) {
            return Collections.emptyList();
        }
        return TILE_FEATURES_CACHE[index];
    }
}