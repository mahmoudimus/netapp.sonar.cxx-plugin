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
package org.sonar.plugins.cxx.veraxx;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.cxx.TestUtils;

import java.io.File;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CxxVeraxxSensorTest {
  private CxxVeraxxSensor sensor;
  private SensorContext context;
  private Project project;

  @Before
  public void setUp() {
    project = mockProject();
    RuleFinder ruleFinder = TestUtils.mockRuleFinder();
    sensor = new CxxVeraxxSensor(ruleFinder, new Settings(), mock(RulesProfile.class));
    context = mock(SensorContext.class);
    org.sonar.api.resources.File resourceMock = mock(org.sonar.api.resources.File.class);
    when(context.getResource((org.sonar.api.resources.File) anyObject())).thenReturn(resourceMock);
  }

  @Test
  public void shouldReportCorrectViolations() {
    sensor.analyse(project, context);
    verify(context, times(10)).saveViolation(any(Violation.class));
  }

  private Project mockProject() {
    Project project = TestUtils.mockProject();

    // works only with relative paths... should look at this later
    List<File> sourceFiles = project.getFileSystem().getSourceFiles();
    sourceFiles.clear();
    sourceFiles.add(new File("sources/application/main.cpp"));
    sourceFiles.add(new File("sources/tests/SAMPLE-test.cpp"));
    sourceFiles.add(new File("sources/tests/SAMPLE-test.h"));
    sourceFiles.add(new File("sources/tests/main.cpp"));
    sourceFiles.add(new File("sources/utils/code_chunks.cpp"));
    sourceFiles.add(new File("sources/utils/utils.cpp"));

    List<File> sourceDirs = project.getFileSystem().getSourceDirs();
    sourceDirs.clear();
    sourceDirs.add(new File("sources"));

    return project;
  }
}
