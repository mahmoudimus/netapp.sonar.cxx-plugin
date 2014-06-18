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

public class ValgrindFrameTest {
  ValgrindFrame frame;
  ValgrindFrame equalFrame;
  ValgrindFrame otherFrame;

  @Before
  public void setUp() {
    frame = new ValgrindFrame("", "", "lala", "", "lala", "111");
    equalFrame = new ValgrindFrame("", "", "lala", "", "lala", "111");
    otherFrame = new ValgrindFrame("", "", "haha", "", "haha", "111");
  }

  @Test
  public void frameDoesntEqualsNull() {
    assert (!frame.equals(null));
  }

  @Test
  public void frameDoesntEqualsMiscObject() {
    assert (!frame.equals("string"));
  }

  @Test
  public void frameEqualityIsReflexive() {
    assert (frame.equals(frame));
    assert (otherFrame.equals(otherFrame));
    assert (equalFrame.equals(equalFrame));
  }

  @Test
  public void frameEqualityWorksAsExpected() {
    assert (frame.equals(equalFrame));
    assert (!frame.equals(otherFrame));
  }

  @Test
  public void frameHashWorksAsExpected() {
    assert (frame.hashCode() == equalFrame.hashCode());
    assert (frame.hashCode() != otherFrame.hashCode());
  }

  @Test
  public void stringRepresentationShouldResembleValgrindsStandard() {
    Map<String, ValgrindFrame> ioMap = new HashMap<String, ValgrindFrame>();

    ioMap.put("0xDEADBEAF: main() (main.cc:1)",
        new ValgrindFrame("0xDEADBEAF", "libX.so", "main()", "src", "main.cc", "1"));
    ioMap.put("0xDEADBEAF: main() (main.cc:1)",
        new ValgrindFrame("0xDEADBEAF", "libX.so", "main()", null, "main.cc", "1"));
    ioMap.put("0xDEADBEAF: main() (main.cc)",
        new ValgrindFrame("0xDEADBEAF", "libX.so", "main()", null, "main.cc", ""));
    ioMap.put("0xDEADBEAF: ??? (main.cc:1)",
        new ValgrindFrame("0xDEADBEAF", "libX.so", null, "src", "main.cc", "1"));
    ioMap.put("0xDEADBEAF: ??? (in libX.so)",
        new ValgrindFrame("0xDEADBEAF", "libX.so", null, "src", null, "1"));
    ioMap.put("0xDEADBEAF: ???",
        new ValgrindFrame("0xDEADBEAF", null, null, null, null, ""));
    ioMap.put("???: ???",
        new ValgrindFrame(null, null, null, null, null, ""));

    for (Map.Entry<String, ValgrindFrame> entry : ioMap.entrySet()) {
      assertEquals(entry.getKey(), entry.getValue().toString());
    }
  }
}
