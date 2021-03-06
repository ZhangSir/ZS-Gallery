package com.itzs.zsgallery.imageloader;

/**
 * Presents the reason why image loading and displaying was failed
 */
public class FailReason {

	private final FailType type;

	private final Throwable cause;

	public FailReason(FailType type, Throwable cause) {
		this.type = type;
		this.cause = cause;
	}

	/** @return  Fail type */
	public FailType getType() {
		return type;
	}

	/** @return Thrown exception/error, can be <b>null</b> */
	public Throwable getCause() {
		return cause;
	}

	/** Presents type of fail while image loading */
	public static enum FailType {
		/** Input/output error. Can be caused by network communication fail or error while caching image on file system. */
		IO_ERROR,
		/**
		 * Error while
		 * decode image to Bitmap}
		 */
		DECODING_ERROR,
		/**
		 *  Network
		 * downloads are denied} and requested image wasn't cached in disk cache before.
		 */
		NETWORK_DENIED,
		/** Not enough memory to create needed Bitmap for image */
		OUT_OF_MEMORY,
		/** Unknown error was occurred while loading image */
		UNKNOWN
	}
}