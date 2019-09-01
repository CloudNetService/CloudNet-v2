package de.dytanic.cloudnet.setup.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
			while (inputStream.read() != -1) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				bufferedReader.lines().forEach(System.out::println);
			}
			this.countDownLatch.countDown();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
