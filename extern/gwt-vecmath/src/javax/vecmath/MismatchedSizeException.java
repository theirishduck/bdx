/*
 * $RCSfile: MismatchedSizeException.java,v $
 *
 * Copyright 1997-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 * $Revision: 1.5 $
 * $Date: 2008/02/28 20:18:50 $
 * $State: Exp $
 */

package javax.vecmath;

/**
 * Indicates that an operation cannot be completed properly because of a
 * mismatch in the sizes of object attributes.
 */
@SuppressWarnings("serial")
public class MismatchedSizeException extends RuntimeException {

	/**
	 * Create the exception object with default values.
	 */
	public MismatchedSizeException() {
	}

	/**
	 * Create the exception object that outputs a message.
	 * 
	 * @param str
	 *          the message string to be output.
	 */
	public MismatchedSizeException(String str) {

		super(str);
	}

}