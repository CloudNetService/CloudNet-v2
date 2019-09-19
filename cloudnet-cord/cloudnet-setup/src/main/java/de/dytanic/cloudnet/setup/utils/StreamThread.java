package de.dytanic.cloudnet.setup.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public final class StreamThread implements Runnable {

    private final CountDownLatch countDownLatch;
    private final InputStream inputStream;

    public StreamThread(CountDownLatch countDownLatch, InputStream inputStream) {
        this.countDownLatch = countDownLatch;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                System.out.print(new String(buffer, 0, length, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.countDownLatch.countDown();
    }
}
