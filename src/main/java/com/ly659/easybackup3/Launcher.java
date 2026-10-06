package com.ly659.easybackup3;

import java.io.File;

public class Launcher {
    public static EasyBackup3 easyBackup3 = new EasyBackup3();

    // ========== 全局常量 =========== //
    public static String temp_path = "%temp%\\EasyBackup3";     // 临时目录


    static void main() {
        // 检查运行环境
        File temp_path = new File(Launcher.temp_path);
        if (!temp_path.exists()) {
            temp_path.mkdir();
        } else if (temp_path.isFile() || temp_path.isDirectory()) {
            temp_path.delete();
            temp_path.mkdir();
        }

        // 启动主程序
        easyBackup3.open();
    }
}
