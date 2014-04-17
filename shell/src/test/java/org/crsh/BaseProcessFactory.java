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
package org.crsh;

import org.crsh.shell.ShellProcessContext;
import org.crsh.shell.ShellResponse;
import org.crsh.text.Text;

import java.io.IOException;

public abstract class BaseProcessFactory {

  public static BaseProcessFactory NOOP = new BaseProcessFactory() {
    @Override
    public BaseProcess create(String request) {
      return new BaseProcess(request);
    }
  };

  public static BaseProcessFactory ECHO = new BaseProcessFactory() {
    @Override
    public BaseProcess create(String request) {
      return new BaseProcess(request) {
        @Override
        public void process(String request, ShellProcessContext processContext) throws IOException {
          if ("bye".equals(request)) {
            processContext.end(ShellResponse.close());
          } else {
            processContext.write(Text.create(request));
            processContext.end(ShellResponse.ok());
          }
        }
      };
    }
  };

  public abstract BaseProcess create(String request);

}
