package kr.rtustudio.supplybox.data;

public record Point(int x, int z) {

    public Point(long packed) {
        this((int) (packed >> 32), (int) (packed & 0xFFFFFFFFL));
    }

    public long getPointKey() {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

}