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
package org.sonar.plugins.cxx.pclint;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.cxx.TestUtils;
import org.sonar.api.utils.SonarException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CxxPCLintSensorTest {
  private SensorContext context;
  private Project project;
  private RulesProfile profile;
  private RuleFinder ruleFinder;

  @Before
  public void setUp() {
    project = TestUtils.mockProject();
    ruleFinder = TestUtils.mockRuleFinder();
    profile = mock(RulesProfile.class);
    context = mock(SensorContext.class);
    File resourceMock = mock(File.class);
    when(context.getResource((File) anyObject())).thenReturn(resourceMock);
  }

  @Test
  public void shouldReportCorrectViolations() {
    Settings settings = new Settings();
    settings.setProperty(CxxPCLintSensor.REPORT_PATH_KEY, "pclint-reports/pclint-result-SAMPLE.xml");
    CxxPCLintSensor sensor = new CxxPCLintSensor(ruleFinder, settings, profile);
    sensor.analyse(project, context);
    verify(context, times(10)).saveViolation(any(Violation.class));
  }

  @Test
  public void shouldReportCorrectMisra2004Violations() {
    Settings settings = new Settings();
    settings.setProperty(CxxPCLintSensor.REPORT_PATH_KEY, "pclint-reports/pclint-result-MISRA2004-SAMPLE.xml");
    CxxPCLintSensor sensor = new CxxPCLintSensor(ruleFinder, settings, profile);
    sensor.analyse(project, context);
    verify(context, times(29)).saveViolation(any(Violation.class));
  }

  @Test(expected=SonarException.class)
  public void shouldThrowExceptionWhenMisra2004DescIsWrong() {
    Settings settings = new Settings();
    settings.setProperty(CxxPCLintSensor.REPORT_PATH_KEY, "pclint-reports/incorrect-pclint-MISRA2004-desc.xml");
    CxxPCLintSensor sensor = new CxxPCLintSensor(ruleFinder, settings, profile);
    sensor.analyse(project, context);
  }

  @Test(expected=SonarException.class)
  public void shouldThrowExceptionWhenMisra2004RuleDoNotExist() {
    Settings settings = new Settings();
    settings.setProperty(CxxPCLintSensor.REPORT_PATH_KEY, "pclint-reports/incorrect-pclint-MISRA2004-rule-do-not-exist.xml");
    CxxPCLintSensor sensor = new CxxPCLintSensor(ruleFinder, settings, profile);
    sensor.analyse(project, context);
  }

  @Test
  public void shouldNotRemapMisra1998Rules() {
    Settings settings = new Settings();
    settings.setProperty(CxxPCLintSensor.REPORT_PATH_KEY, "pclint-reports/pclint-result-MISRA1998-SAMPLE.xml");
    CxxPCLintSensor sensor = new CxxPCLintSensor(ruleFinder, settings, profile);
    sensor.analyse(project, context);
    verify(context, times(1)).saveViolation(any(Violation.class));
  }
}
