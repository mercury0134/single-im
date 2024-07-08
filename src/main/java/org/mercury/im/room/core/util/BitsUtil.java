package org.mercury.im.room.core.util;

public class BitsUtil {

    /**
     * 判断一个整数的指定位是否为1
     *
     * @param x 整数
     * @param i 位的索引，从0开始，表示要检查的位
     * @return 如果第i位是1，则返回true，否则返回false
     */
    public static boolean isBitSet(int x, int i) {
        int mask = 1 << i;
        return (x & mask) != 0;
    }

    /**
     * 设置一个整数的指定位为1
     *
     * @param x 整数
     * @param i 位的索引，从0开始，表示要设置为1的位
     * @return 设置指定位为1后的整数
     */
    public static int setBit(int x, int i) {
        int mask = 1 << i;

        return x | mask;
    }

    /**
     * 设置一个整数的指定位为0
     *
     * @param x 整数
     * @param i 位的索引，从0开始，表示要设置为0的位
     * @return 设置指定位为0后的整数
     */
    public static int clearBit(int x, int i) {
        int mask = 1 << i;

        return x & ~mask;
    }

}
