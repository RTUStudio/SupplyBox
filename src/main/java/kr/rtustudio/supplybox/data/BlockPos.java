package kr.rtustudio.supplybox.data;


public record BlockPos(Integer x, Integer y, Integer z) {

    public BlockPos(long packed) {
        this((int) (packed >> 38), (int) (packed << 52 >> 52), (int) (packed << 26 >> 38));
    }

    public long getBlockKey() {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | ((long) y & 0xFFF);
    }

    public long getChunkKey() {
        return ((long) (z >> 4) << 32) | ((x >> 4) & 0xFFFFFFFFL);
    }

}
