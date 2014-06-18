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
import org.sonar.plugins.cxx.TestUtils;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ValgrindReportParserTest {
  private ValgrindReportParser parser;

  @Before
  public void setUp() {
    parser = new ValgrindReportParser();
  }

  @Test
  public void shouldParseCorrectNumberOfErrors() throws javax.xml.stream.XMLStreamException {
    File report = TestUtils.loadResource("valgrind-reports/valgrind-result-SAMPLE.xml");
    Set<ValgrindError> valgrindErrors = parser.parseReport(report);
    assertEquals(valgrindErrors.size(), 6);
  }

  @Test(expected = javax.xml.stream.XMLStreamException.class)
  public void shouldThrowWhenGivenAnIncompleteReport_1() throws javax.xml.stream.XMLStreamException {
    // error contains no kind-tag
    File report = TestUtils.loadResource("valgrind-reports/incorrect-valgrind-result_1.xml");
    parser.parseReport(report);
  }

  @Test(expected = javax.xml.stream.XMLStreamException.class)
  public void shouldThrowWhenGivenAnIncompleteReport_2() throws javax.xml.stream.XMLStreamException {
    // error contains no what- or xwhat-tag
    File report = TestUtils.loadResource("valgrind-reports/incorrect-valgrind-result_2.xml");
    parser.parseReport(report);
  }

  @Test(expected = javax.xml.stream.XMLStreamException.class)
  public void shouldThrowWhenGivenAnIncompleteReport_3() throws javax.xml.stream.XMLStreamException {
    // error contains no stack-tag
    File report = TestUtils.loadResource("valgrind-reports/incorrect-valgrind-result_3.xml");
    parser.parseReport(report);
  }
}
