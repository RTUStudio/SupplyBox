package kr.rtuserver.supplybox.data;


public record BlockCoordinate(Integer x, Integer y, Integer z) {

    public BlockCoordinate(long packed) {
        this((int) ((packed << 37) >> 37), (int) (packed >>> 54), (int) ((packed << 10) >> 37));
    }

    public long getBlockKey() {
        return ((long) x & 0x7FFFFFF) | (((long) z & 0x7FFFFFF) << 27) | ((long) y << 54);
    }

    public long getChunkKey() {
        return (long) (x >> 4) & 0xffffffffL | ((long) (z >> 4) & 0xffffffffL) << 32;
    }

}
