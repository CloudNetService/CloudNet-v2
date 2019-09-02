package de.dytanic.cloudnet.lib.scheduler;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class TaskEntry<T> {

	private final TaskEntryFuture<T> future;
	protected volatile Callable<T> task;
	protected volatile T value = null;
	protected Consumer<T> consumer;
	protected long delayTimeOut, repeat, delay;
	protected boolean completed = false;

	public TaskEntry(Callable<T> task, Consumer<T> complete, long delay, long repeat) {

		this.task = task;
		this.consumer = complete;
		this.delay = delay;
		this.delayTimeOut = System.currentTimeMillis() + delay;
		this.repeat = repeat;
		this.future = new TaskEntryFuture<>(this, false);
	}


	protected void invoke() throws Exception {

		if (task == null)
			return;

		T val = task.call();

		value = val;

		if (consumer != null)
			consumer.accept(val);

		if (repeat != -1 && repeat != 0) repeat--;

		if (repeat != 0)
			this.delayTimeOut = System.currentTimeMillis() + delay;
		else {
			completed = true;

			if (future.waits) {
				synchronized (future) {
					future.notifyAll();
				}
			}
		}
	}


	public Consumer<T> getConsumer() {
		return consumer;
	}


	public long getDelayTimeOut() {
		return delayTimeOut;
	}


	public long getRepeat() {
		return repeat;
	}


	protected TaskEntryFuture<T> drop() {
		return future;
	}


	public boolean isCompleted() {
		return completed;
	}

}