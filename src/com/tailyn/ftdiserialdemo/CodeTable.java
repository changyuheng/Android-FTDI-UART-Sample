package com.tailyn.ftdiserialdemo;

import java.util.HashMap;
import java.util.Map;

public class CodeTable {
    public static final Map<String, Byte> JAPANESE;

    static {
    JAPANESE = new HashMap<String, Byte>();

    JAPANESE.put("▁", (byte) 0x80);
    JAPANESE.put("▁", (byte) 0x81);
    JAPANESE.put("▂", (byte) 0x82);
    JAPANESE.put("▂", (byte) 0x83);
    JAPANESE.put("▄", (byte) 0x84);
    JAPANESE.put("▅", (byte) 0x85);
    JAPANESE.put("▆", (byte) 0x86);
    JAPANESE.put("▇", (byte) 0x87);
    JAPANESE.put("▏", (byte) 0x88);
    JAPANESE.put("▎", (byte) 0x89);
    JAPANESE.put("▍", (byte) 0x8A);
    JAPANESE.put("▌", (byte) 0x8B);
    JAPANESE.put("▋", (byte) 0x8C);
    JAPANESE.put("▊", (byte) 0x8D);
    JAPANESE.put("▉", (byte) 0x8E);
    JAPANESE.put("┼", (byte) 0x8F);

    JAPANESE.put("┴", (byte) 0x90);
    JAPANESE.put("┬", (byte) 0x91);
    JAPANESE.put("┤", (byte) 0x92);
    JAPANESE.put("├", (byte) 0x93);
    JAPANESE.put("─", (byte) 0x94);
    JAPANESE.put("╴", (byte) 0x95);
    JAPANESE.put("║", (byte) 0x96);
    JAPANESE.put("│", (byte) 0x97);
    JAPANESE.put("┌", (byte) 0x98);
    JAPANESE.put("┐", (byte) 0x99);
    JAPANESE.put("└", (byte) 0x9A);
    JAPANESE.put("┘", (byte) 0x9B);
    JAPANESE.put("", (byte) 0x9C);
    JAPANESE.put("", (byte) 0x9D);
    JAPANESE.put("", (byte) 0x9E);
    JAPANESE.put("", (byte) 0x9F);

    JAPANESE.put(" ", (byte) 0xA0);
    JAPANESE.put("。", (byte) 0xA1);
    JAPANESE.put("┌", (byte) 0xA2);
    JAPANESE.put("┘", (byte) 0xA3);
    JAPANESE.put("､", (byte) 0xA4);
    JAPANESE.put("‚", (byte) 0xA5);
    JAPANESE.put("ｦ", (byte) 0xA6);
    JAPANESE.put("ｧ", (byte) 0xA7);
    JAPANESE.put("ｨ", (byte) 0xA8);
    JAPANESE.put("ｩ", (byte) 0xA9);
    JAPANESE.put("ｪ", (byte) 0xAA);
    JAPANESE.put("ｫ", (byte) 0xAB);
    JAPANESE.put("ｬ", (byte) 0xAC);
    JAPANESE.put("ｭ", (byte) 0xAD);
    JAPANESE.put("ｮ", (byte) 0xAE);
    JAPANESE.put("ｯ", (byte) 0xAF);

    JAPANESE.put("ｰ", (byte) 0xB0);
    JAPANESE.put("ｱ", (byte) 0xB1);
    JAPANESE.put("ｲ", (byte) 0xB2);
    JAPANESE.put("ｳ", (byte) 0xB3);
    JAPANESE.put("ｴ", (byte) 0xB4);
    JAPANESE.put("ｵ", (byte) 0xB5);
    JAPANESE.put("ｶ", (byte) 0xB6);
    JAPANESE.put("ｷ", (byte) 0xB7);
    JAPANESE.put("ｸ", (byte) 0xB8);
    JAPANESE.put("ｹ", (byte) 0xB9);
    JAPANESE.put("ｺ", (byte) 0xBA);
    JAPANESE.put("ｻ", (byte) 0xBB);
    JAPANESE.put("ｼ", (byte) 0xBC);
    JAPANESE.put("ｽ", (byte) 0xBD);
    JAPANESE.put("ｾ", (byte) 0xBE);
    JAPANESE.put("ｿ", (byte) 0xBF);

    JAPANESE.put("ﾀ", (byte) 0xC0);
    JAPANESE.put("ﾁ", (byte) 0xC1);
    JAPANESE.put("ﾂ", (byte) 0xC2);
    JAPANESE.put("ﾃ", (byte) 0xC3);
    JAPANESE.put("ﾄ", (byte) 0xC4);
    JAPANESE.put("ﾅ", (byte) 0xC5);
    JAPANESE.put("ﾆ", (byte) 0xC6);
    JAPANESE.put("ﾇ", (byte) 0xC7);
    JAPANESE.put("ﾈ", (byte) 0xC8);
    JAPANESE.put("ﾉ", (byte) 0xC9);
    JAPANESE.put("ﾊ", (byte) 0xCA);
    JAPANESE.put("ﾋ", (byte) 0xCB);
    JAPANESE.put("ﾌ", (byte) 0xCC);
    JAPANESE.put("ﾍ", (byte) 0xCD);
    JAPANESE.put("ﾎ", (byte) 0xCE);
    JAPANESE.put("ﾏ", (byte) 0xCF);

    JAPANESE.put("ﾐ", (byte) 0xD0);
    JAPANESE.put("ﾑ", (byte) 0xD1);
    JAPANESE.put("ﾒ", (byte) 0xD2);
    JAPANESE.put("ﾓ", (byte) 0xD3);
    JAPANESE.put("ﾔ", (byte) 0xD4);
    JAPANESE.put("ﾕ", (byte) 0xD5);
    JAPANESE.put("ﾖ", (byte) 0xD6);
    JAPANESE.put("ﾗ", (byte) 0xD7);
    JAPANESE.put("ﾘ", (byte) 0xD8);
    JAPANESE.put("ﾙ", (byte) 0xD9);
    JAPANESE.put("ﾚ", (byte) 0xDA);
    JAPANESE.put("ﾛ", (byte) 0xDB);
    JAPANESE.put("ﾜ", (byte) 0xDC);
    JAPANESE.put("ﾝ", (byte) 0xDD);
    JAPANESE.put("ﾞ", (byte) 0xDE);
    JAPANESE.put("ﾟ", (byte) 0xDF);

    JAPANESE.put("=", (byte) 0xE0);
    JAPANESE.put("╞", (byte) 0xE1);
    JAPANESE.put("‡", (byte) 0xE2);
    JAPANESE.put("╡", (byte) 0xE3);
    JAPANESE.put("◢", (byte) 0xE4);
    JAPANESE.put("◣", (byte) 0xE5);
    JAPANESE.put("◥", (byte) 0xE6);
    JAPANESE.put("◤", (byte) 0xE7);
    JAPANESE.put("♠", (byte) 0xE8);
    JAPANESE.put("♥", (byte) 0xE9);
    JAPANESE.put("♦", (byte) 0xEA);
    JAPANESE.put("♣", (byte) 0xEB);
    JAPANESE.put("●", (byte) 0xEC);
    JAPANESE.put("○", (byte) 0xED);
    JAPANESE.put("／", (byte) 0xEE);
    JAPANESE.put("＼", (byte) 0xEF);

    JAPANESE.put("×", (byte) 0xF0);
    JAPANESE.put("円", (byte) 0xF1);
    JAPANESE.put("年", (byte) 0xF2);
    JAPANESE.put("月", (byte) 0xF3);
    JAPANESE.put("日", (byte) 0xF4);
    JAPANESE.put("時", (byte) 0xF5);
    JAPANESE.put("分", (byte) 0xF6);
    JAPANESE.put("秒", (byte) 0xF7);
    JAPANESE.put("〒", (byte) 0xF8);
    JAPANESE.put("市", (byte) 0xF9);
    JAPANESE.put("区", (byte) 0xFA);
    JAPANESE.put("町", (byte) 0xFB);
    JAPANESE.put("村", (byte) 0xFC);
    JAPANESE.put("人", (byte) 0xFD);
    JAPANESE.put("▓", (byte) 0xFE);
    }
}
