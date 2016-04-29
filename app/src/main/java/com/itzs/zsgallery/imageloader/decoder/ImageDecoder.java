package com.itzs.zsgallery.imageloader.decoder;

import java.io.IOException;


import android.graphics.Bitmap;

import com.itzs.zsgallery.imageloader.ImageViewAware;
import com.itzs.zsgallery.imageloader.downloader.ImageDownloader;

/**
 * Provide decoding image to result {@link Bitmap}.
 */
public interface ImageDecoder {

	/**
	 * Decodes image to {@link Bitmap} according target size and other parameters.
	 *
	 * @return
	 * @throws IOException
	 */
	Bitmap decode(String uri, ImageViewAware imageAware, ImageDownloader downloader, Object extraForDownloader) throws IOException;
}
