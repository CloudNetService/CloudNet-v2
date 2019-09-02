package de.dytanic.cloudnet.setup.utils;

import java.io.*;
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
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = inputStream.read(buffer)) != -1) {
				os.write(buffer, 0 ,length);
			}
			System.out.println(os.toString());
			this.countDownLatch.countDown();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
