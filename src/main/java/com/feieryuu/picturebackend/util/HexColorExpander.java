package com.feieryuu.picturebackend.util;

public class HexColorExpander {

    public static String expandHexColor(String compressed) {
        // 移除前缀并初始化参数
        String input = compressed.replaceFirst("^0x", "");
        int length = input.length();

        // 特殊长度处理
        if (length == 3) {
            return "0x000000";
        }

        StringBuilder expanded = new StringBuilder(6); // 预分配6字符容量
        int position = 0;

        // 处理三个颜色通道
        for (int channel = 0; channel < 3; channel++) {
            // 处理长度不足的情况
            if (position >= length) {
                expanded.append("00");
                continue;
            }

            char current = input.charAt(position);

            // 零值简写处理
            if (current == '0') {
                expanded.append("00");
                position++;
            }
            // 正常值处理
            else {
                // 获取双字符值，不足时补零
                if (position + 1 < length) {
                    expanded.append(current).append(input.charAt(position + 1));
                } else {
                    expanded.append(current).append('0');
                }
                position += 2;
            }
        }

        return "0x" + expanded.toString();
    }


    public static void main(String[] args) {
        // 测试用例
        System.out.println(expandHexColor("000"));     // 0x000000
        System.out.println(expandHexColor("0a00"));    // 0x00a000
        System.out.println(expandHexColor("a0b40"));   // 0xa0b400
        System.out.println(expandHexColor("0ab0"));    // 0x00ab00
        System.out.println(expandHexColor("00ab"));   // 0x0000ab
        System.out.println(expandHexColor("0ab00"));  // 0x00ab00
    }
}
