package team.CowsAndHorses.util;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;

public class QrCodeUtil {

    public static String url = "https://api.qrtool.cn";
    public static byte[] generateQrCode(String text) {
        return cn.hutool.http.HttpUtil.downloadBytes(url + "/?text=" + text);
    }
}
