package com.ly659.easybackup3.connect_helper;

import com.ly659.easybackup3.BackupUI;
import com.ly659.easybackup3.Launcher;
import com.ly659.easybackup3.connect_helper.packages.HandShake;
import com.ly659.easybackup3.connect_helper.packages.ThumbTraveler;
import com.ly659.easybackup3.packets.ItemInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 这是一个用于管理PC端应用和App端网络连接和数据传输的类。
 */
public class ConnectHelper {
    /**
     * 用于创建一个线程，新的App端连接实例。
     */
    private static class AppThread implements Runnable {
        private final Socket appSocket;     // 用于与App端通信的套接字

        private ObjectInputStream objectInputStream;
        private ObjectOutputStream objectOutputStream;

        // 握手成功标记
        private boolean connectAvailable = false;

        // 此App端对应的PC端窗口对象
        private BackupUI backupUI;


        public AppThread(Socket AppSocket) {
            this.appSocket = AppSocket;
            backupUI = new BackupUI();      // 初始化UI窗口实例
        }

        private void receive() throws IOException, ClassNotFoundException {
            if (!connectAvailable) return;

            // 读取信息
            Object obj = objectInputStream.readObject();

            // 判断信息类型，执行对应操作
            if (obj instanceof ThumbTraveler thumbTraveler) {
                // 封装项目信息
                ItemInfo item = new ItemInfo(thumbTraveler.getId(),
                        thumbTraveler.saveImage(Launcher.temp_path));
                // 将其添加到集合
                backupUI.getItems().add(item);
            }


        }

        private Object receive_return()  throws IOException, ClassNotFoundException {
            if (!connectAvailable) return null;

            // 读取信息并返回
            return objectInputStream.readObject();
        }

        public void send(Serializable obj) throws IOException {
            if (!connectAvailable) return;

            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
        }

        @Override
        public void run() {
            // 初始化IO流
            try {
                InputStream inputStream = appSocket.getInputStream();
                OutputStream outputStream = appSocket.getOutputStream();
                objectInputStream = new ObjectInputStream(inputStream);
                objectOutputStream = new ObjectOutputStream(outputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 1. 等待并接收App发送的握手消息
            Object handPacket;
            try {
                handPacket = receive_return();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // 2. 发送握手信息给App，完成握手
            if (handPacket instanceof HandShake handShake) {
                try {
                    send(handShake);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                connectAvailable = true;
                backupUI = new BackupUI();      // 创建UI对象
                Launcher.easyBackup3.addDevice(handShake, backupUI);    // 将设备添加到UI界面的列表

                // 连接正式建立，开始监听并处理App的所有消息
                try {
                    while (true) {
                        receive();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        }
    }


    //private final String hostIp;        // 本机IP
    private final int hostPort;         // 本机软件端口号

    // 客户端（AppThread）线程池
    private final ExecutorService appThreadPool;

    // 套接字
    ServerSocket sSocket;       // 服务端Socket

    public ConnectHelper(int hostPort) {
        //this.hostIp = hostIp;
        this.hostPort = hostPort;

        // 初始化线程池
        appThreadPool = Executors.newCachedThreadPool();

    }

    /**
     * 等待并尝试与App端建立连接。
     */
    public void onConnect() {
        new Thread(() -> {
            try {
                // 创建服务端socket
                sSocket = new ServerSocket(hostPort);

                // 等待一个客户端的连接，并启动新线程与其通信
                while (true) {
                    Socket socket = sSocket.accept();
                    appThreadPool.submit(new AppThread(socket));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    /**
     * 断开与所有App端的连接，并执行清理操作。
     */
    public void onDisconnect() throws IOException {
        appThreadPool.shutdown();
        sSocket.close();

    }

}
