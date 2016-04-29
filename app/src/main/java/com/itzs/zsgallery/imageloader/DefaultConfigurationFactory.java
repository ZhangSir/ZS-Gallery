package com.itzs.zsgallery.imageloader;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.os.Environment;

import com.itzs.zsgallery.imageloader.decoder.BaseImageDecoder;
import com.itzs.zsgallery.imageloader.decoder.ImageDecoder;
import com.itzs.zsgallery.imageloader.downloader.BaseImageDownloader;
import com.itzs.zsgallery.imageloader.downloader.ImageDownloader;

/**
 * Factory for providing of default options for  configuration
 */
public class DefaultConfigurationFactory {

	/** Creates default implementation of task executor */
	public static Executor createExecutor(int threadPoolSize, int threadPriority) {
		BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
		return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue,
				createThreadFactory(threadPriority, "zs-pool-"));
	}

	/** Creates default implementation of task distributor */
	public static Executor createTaskDistributor() {
		return Executors.newCachedThreadPool(createThreadFactory(Thread.NORM_PRIORITY, "zs-pool-d-"));
	}

	/**
	 * Creates default implementation of DiskCache depends on incoming parameters
	 */
	public static LruDiskCache createDiskCache(Context context, long diskCacheSize) {
		File cacheDir = createReserveDiskCacheDir(context);
		LruDiskCache diskCache = new LruDiskCache(cacheDir, diskCacheSize);
		return diskCache;
	}

	/** Creates reserve disk cache folder which will be used if primary disk cache folder becomes unavailable */
	private static File createReserveDiskCacheDir(Context context) {
		File cacheDir = new File(Environment.getExternalStorageDirectory(), "ZSImageManager");
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		return cacheDir;
	}

	/**
	 * Creates default implementation of MemoryCache<br />
	 * Default cache size = 1/8 of available app memory.
	 */
	public static LruMemoryCache createMemoryCache(Context context, int memoryCacheSize) {
		if (memoryCacheSize == 0) {
			memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
		}
		return new LruMemoryCache(context, memoryCacheSize);
	}

	/** Creates default implementation of ImageDownloader */
	public static ImageDownloader createImageDownloader(Context context) {
		return new BaseImageDownloader(context);
	}

	/** Creates default implementation of ImageDecoder */
	public static ImageDecoder createImageDecoder() {
		return new BaseImageDecoder();
	}

	/** Creates default implementation of {@linkplain ThreadFactory thread factory} for task executor */
	private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
		return new DefaultThreadFactory(threadPriority, threadNamePrefix);
	}

	private static class DefaultThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger(1);

		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		private final int threadPriority;

		DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
			this.threadPriority = threadPriority;
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon()) t.setDaemon(false);
			t.setPriority(threadPriority);
			return t;
		}
	}
}
