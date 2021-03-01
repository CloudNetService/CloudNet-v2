package eu.cloudnetservice.cloudnet.v2.setup.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public final class StreamThread implements Runnable {

    private final CountDownLatch countDownLatch;
    private final BufferedReader reader;

    public StreamThread(CountDownLatch countDownLatch, InputStream inputStream) {
        this.countDownLatch = countDownLatch;
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    }

    @Override
    public void run() {
        try (BufferedReader reader = this.reader) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.countDownLatch.countDown();
        }
    }
}
