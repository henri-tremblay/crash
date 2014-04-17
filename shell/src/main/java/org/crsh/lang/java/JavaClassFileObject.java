/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.crsh.lang.java;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/** @author Julien Viet */
class JavaClassFileObject extends SimpleJavaFileObject {

  /** . */
  private ByteArrayOutputStream baos;

  /** . */
  private final String className;

  JavaClassFileObject(String className) throws URISyntaxException {
    super(new URI("whatever", null, '/' + className.replace('.', '/') + ".class", null), Kind.CLASS);

    //
    this.className = className;
  }

  String getClassName() {
    return className;
  }

  public byte[] getBytes() {
    return baos != null ? baos.toByteArray() : null;
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    if (baos != null) {
      throw new IOException("Already open");
    } else {
      return baos = new ByteArrayOutputStream();
    }
  }
}
