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

package org.crsh.cli.impl.lang;

import org.crsh.cli.descriptor.ArgumentDescriptor;
import org.crsh.cli.descriptor.CommandDescriptor;
import org.crsh.cli.descriptor.Description;
import org.crsh.cli.impl.descriptor.CommandDescriptorImpl;
import org.crsh.cli.impl.descriptor.IntrospectionException;
import org.crsh.cli.descriptor.OptionDescriptor;
import org.crsh.cli.descriptor.ParameterDescriptor;
import org.crsh.cli.SyntaxException;
import org.crsh.cli.impl.invocation.CommandInvoker;
import org.crsh.cli.impl.invocation.InvocationException;
import org.crsh.cli.impl.invocation.InvocationMatch;
import org.crsh.cli.impl.invocation.ParameterMatch;
import org.crsh.cli.impl.invocation.Resolver;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ClassDescriptor<T> extends CommandDescriptorImpl<T> {

  /** . */
  private final Class<T> type;

  /** . */
  private final Map<String, MethodDescriptor<T>> methods;

  ClassDescriptor(Class<T> type, Map<String, MethodDescriptor<T>> methods, Description info) throws IntrospectionException {
    super(type.getSimpleName().toLowerCase(), info);

    //
    this.methods = methods;
    this.type = type;
  }

  @Override
  protected void addParameter(ParameterDescriptor parameter) throws IntrospectionException {

    // Check we can add the option
    if (parameter instanceof OptionDescriptor) {
      OptionDescriptor option = (OptionDescriptor)parameter;
      Set<String> blah = new HashSet<String>();
      for (String optionName : option.getNames()) {
        blah.add((optionName.length() == 1 ? "-" : "--") + optionName);
      }
      for (MethodDescriptor<T> method : methods.values()) {
        Set<String> diff = new HashSet<String>(method.getOptionNames());
        diff.retainAll(blah);
        if (diff.size() > 0) {
          throw new IntrospectionException("Cannot add method " + method.getName() + " because it has common "
          + " options with its class: " + diff);
        }
      }
    }

    //
    super.addParameter(parameter);
  }

  @Override
  public CommandInvoker<T, ?> getInvoker(final InvocationMatch<T> match) {

    if (Runnable.class.isAssignableFrom(type)) {
      return new CommandInvoker<T, Void>() {
        @Override
        public InvocationMatch<T> getMatch() {
          return match;
        }
        @Override
        public Class<Void> getReturnType() {
          return Void.class;
        }
        @Override
        public Type getGenericReturnType() {
          return Void.class;
        }
        @Override
        public Class<?>[] getParameterTypes() {
          return new Class<?>[0];
        }
        @Override
        public Type[] getGenericParameterTypes() {
          return new Type[0];
        }
        @Override
        public Void invoke(Resolver resolver, T command) throws InvocationException, SyntaxException {
          configure(match, command);
          Runnable runnable = Runnable.class.cast(command);
          try {
            runnable.run();
          }
          catch (Exception e) {
            throw new InvocationException(e);
          }
          return null;
        }
      };
    } else {
      return null;
    }
  }

  void configure(InvocationMatch<T> classMatch, T command) throws InvocationException, SyntaxException {
    for (ParameterDescriptor parameter : getParameters()) {
      ParameterMatch match = classMatch.getParameter(parameter);
      if (match == null) {
        if (parameter.isRequired()) {
          if (parameter instanceof ArgumentDescriptor) {
            ArgumentDescriptor argument = (ArgumentDescriptor)parameter;
            throw new SyntaxException("Missing argument " + argument.getName());
          } else {
            OptionDescriptor option = (OptionDescriptor)parameter;
            throw new SyntaxException("Missing option " + option.getNames());
          }
        }
      } else {
        Object value = match.computeValue();
        Field f = ((ClassFieldBinding)parameter.getBinding()).getField();
        try {
          f.setAccessible(true);
          f.set(command, value);
        }
        catch (Exception e) {
          throw new InvocationException(e.getMessage(), e);
        }
      }
    }
  }

  @Override
  public CommandDescriptor<T> getOwner() {
    return null;
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public Map<String, ? extends MethodDescriptor<T>> getSubordinates() {
    return methods;
  }

  public MethodDescriptor<T> getSubordinate(String name) {
    return methods.get(name);
  }
}
