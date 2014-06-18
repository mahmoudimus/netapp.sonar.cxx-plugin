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
package org.sonar.plugins.cxx.rats;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RuleFinder;
import org.sonar.plugins.cxx.utils.CxxReportSensor;
import org.sonar.plugins.cxx.utils.CxxUtils;

import java.io.File;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class CxxRatsSensor extends CxxReportSensor {
  private static final String MISSING_RATS_TYPE = "fixed size global buffer";
  public static final String REPORT_PATH_KEY = "sonar.cxx.rats.reportPath";
  private static final String DEFAULT_REPORT_PATH = "rats-reports/rats-result-*.xml";
  private RulesProfile profile;

  /**
   * {@inheritDoc}
   */
  public CxxRatsSensor(RuleFinder ruleFinder, Settings conf, RulesProfile profile) {
    super(ruleFinder, conf);
    this.profile = profile;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return super.shouldExecuteOnProject(project)
      && !profile.getActiveRulesByRepository(CxxRatsRuleRepository.KEY).isEmpty();
  }

  @Override
  protected String reportPathKey() {
    return REPORT_PATH_KEY;
  }

  @Override
  protected String defaultReportPath() {
    return DEFAULT_REPORT_PATH;
  }

  @Override
  protected void processReport(Project project, SensorContext context, File report)
      throws org.jdom.JDOMException, java.io.IOException
  {
    try
    {
      SAXBuilder builder = new SAXBuilder(false);
      Element root = builder.build(report).getRootElement();

      List<Element> vulnerabilities = root.getChildren("vulnerability");
      for (Element vulnerability : vulnerabilities) {
        String type = getVulnerabilityType(vulnerability.getChild("type"));
        String message = vulnerability.getChild("message").getTextTrim();

        List<Element> files = vulnerability.getChildren("file");

        for (Element file : files) {
          String fileName = file.getChild("name").getTextTrim();

          List<Element> lines = file.getChildren("line");
          for (Element lineElem : lines) {
            String line = lineElem.getTextTrim();
            saveViolation(project, context, CxxRatsRuleRepository.KEY,
                fileName, line, type, message);
          }
        }
      }
    } catch (org.jdom.input.JDOMParseException e) {
      // when RATS fails the XML file might be incomplete
      CxxUtils.LOG.error("Ignore incomplete XML output from RATS '{}'", e.toString());
    }
  }

  private String getVulnerabilityType(Element child) {
    if (child != null) {
      return child.getTextTrim();
    }
    return MISSING_RATS_TYPE;
  }
}
