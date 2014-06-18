/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010 Neticoa SAS France
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cxx.valgrind;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ValgrindErrorTest {
  ValgrindError error;
  ValgrindError equalError;
  ValgrindError otherError;

  @Before
  public void setUp() {
    error = new ValgrindError("kind", "text", new ValgrindStack());
    equalError = new ValgrindError("kind", "text", new ValgrindStack());
    otherError = new ValgrindError("otherkind", "othertext", new ValgrindStack());
  }

  @Test
  public void errorDoesntEqualsNull() {
    assert (!error.equals(null));
  }

  @Test
  public void errorDoesntEqualsMiscObject() {
    assert (!error.equals("string"));
  }

  @Test
  public void errorEqualityIsReflexive() {
    assert (error.equals(error));
    assert (otherError.equals(otherError));
    assert (equalError.equals(equalError));
  }

  @Test
  public void errorEqualityWorksAsExpected() {
    assert (error.equals(equalError));
    assert (!error.equals(otherError));
  }

  @Test
  public void errorHashWorksAsExpected() {
    assert (error.hashCode() == equalError.hashCode());
    assert (error.hashCode() != otherError.hashCode());
  }

  @Test
  public void stringRepresentationShouldResembleValgrindsStandard() {
    Map<String, ValgrindError> ioMap = new HashMap<String, ValgrindError>();

    ioMap.put("\n\n", new ValgrindError("", "", new ValgrindStack()));
    ioMap.put("description\n\n", new ValgrindError("kind", "description", new ValgrindStack()));

    for (Map.Entry<String, ValgrindError> entry : ioMap.entrySet()) {
      assertEquals(entry.getKey(), entry.getValue().toString());
    }
  }

  @Test
  public void getKindWorks() {
    String KIND = "kind";
    assertEquals(new ValgrindError(KIND, "", new ValgrindStack()).getKind(), KIND);
  }
}
