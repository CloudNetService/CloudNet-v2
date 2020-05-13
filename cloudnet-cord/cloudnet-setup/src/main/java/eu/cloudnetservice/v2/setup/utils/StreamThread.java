package eu.cloudnetservice.v2.setup.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public final class StreamThread implements Runnable {

    private final CountDownLatch countDownLatch;
    private final Reader reader;

    public StreamThread(CountDownLatch countDownLatch, InputStream inputStream) {
        this.countDownLatch = countDownLatch;
        this.reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

    }

    @Override
    public void run() {
        try (Reader reader = this.reader) {
            char[] buffer = new char[1024];
            int length = 0;
            while ((length = reader.read(buffer)) != -1) {
                System.out.print(String.copyValueOf(buffer, 0, length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.countDownLatch.countDown();
        }
    }
}
