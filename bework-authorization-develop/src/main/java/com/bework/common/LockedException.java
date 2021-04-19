package com.bework.common;

import org.springframework.security.core.AuthenticationException;

public class LockedException extends AuthenticationException {
	// ~ Constructors
	// ===================================================================================================

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a <code>UsernameNotFoundException</code> with the specified message.
	 *
	 * @param msg the detail message.
	 */
	public LockedException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a {@code UsernameNotFoundException} with the specified message and root
	 * cause.
	 *
	 * @param msg the detail message.
	 * @param t root cause
	 */
	public LockedException(String msg, Throwable t) {
		super(msg, t);
	}
}
