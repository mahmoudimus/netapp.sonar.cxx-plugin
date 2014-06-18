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
package org.sonar.plugins.cxx.utils;

import org.apache.tools.ant.DirectoryScanner;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.cxx.CxxLanguage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 */
public abstract class CxxReportSensor implements Sensor {
  private RuleFinder ruleFinder;
  protected Settings conf = null;
  
  public CxxReportSensor() {
  }
  
  /**
   * {@inheritDoc}
   */
  public CxxReportSensor(Settings conf) {
    this.conf = conf;
  }

  /**
   * {@inheritDoc}
   */
  public CxxReportSensor(RuleFinder ruleFinder, Settings conf) {
    this.ruleFinder = ruleFinder;
    this.conf = conf;
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return CxxLanguage.KEY.equals(project.getLanguageKey());
  }

  /**
   * {@inheritDoc}
   */
  public void analyse(Project project, SensorContext context) {
    try {
      List<File> reports = getReports(conf, project.getFileSystem().getBasedir().getPath(),
          reportPathKey(), defaultReportPath());
      for (File report : reports) {
        CxxUtils.LOG.info("Processing report '{}'", report);
        try{
          processReport(project, context, report);
        }
        catch(EmptyReportException e){
          CxxUtils.LOG.warn("The report '{}' seems to be empty, ignoring.", report);
        }
      }

      if (reports.isEmpty()) {
        handleNoReportsCase(context);
      }
    } catch (Exception e) {
      String msg = new StringBuilder()
          .append("Cannot feed the data into sonar, details: '")
          .append(e)
          .append("'")
          .toString();
      throw new SonarException(msg, e);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  public String getStringProperty(String name, String def) {
      String value = conf.getString(name);
      if (value == null)
          value = def;
      return value;
  }

  protected List<File> getReports(Settings conf,
      String baseDirPath,
      String reportPathPropertyKey,
      String defaultReportPath) {
    String reportPath = conf.getString(reportPathPropertyKey);
    if (reportPath == null) {
      reportPath = defaultReportPath;
    }

    CxxUtils.LOG.debug("Using pattern '{}' to find reports", reportPath);

    DirectoryScanner scanner = new DirectoryScanner();
    String[] includes = new String[1];
    includes[0] = reportPath;
    scanner.setIncludes(includes);
    scanner.setBasedir(new File(baseDirPath));
    scanner.scan();
    String[] relPaths = scanner.getIncludedFiles();

    List<File> reports = new ArrayList<File>();
    for (String relPath : relPaths) {
      reports.add(new File(baseDirPath, relPath));
    }

    return reports;
  }

  protected void saveViolation(Project project, SensorContext context, String ruleRepoKey,
      String file, String line, String ruleId, String msg) {
    RuleQuery ruleQuery = RuleQuery.create()
        .withRepositoryKey(ruleRepoKey)
        .withKey(ruleId);
    Rule rule = ruleFinder.find(ruleQuery);
    if (rule != null) {
      org.sonar.api.resources.File resource =
          org.sonar.api.resources.File.fromIOFile(new File(file), project);
      if (context.getResource(resource) != null) {
        Violation violation = Violation.create(rule, resource).setMessage(msg);
        if (line != null){
          try{
            int linenr = Integer.parseInt(line);
            violation.setLineId(linenr);
          }catch(java.lang.NumberFormatException nfe){
            CxxUtils.LOG.warn("Skipping invalid line number: {}", line);
          }
        }
        context.saveViolation(violation);
      } else {
        CxxUtils.LOG.debug("Cannot find the file '{}', skipping violation '{}'", file, msg);
      }
    } else {
      CxxUtils.LOG.warn("Cannot find the rule {}, skipping violation", ruleId);
    }
  }
  
  protected void processReport(Project project, SensorContext context, File report)
      throws Exception
  {
  }

  protected void handleNoReportsCase(SensorContext context) {
  }

  protected String reportPathKey() {
    return "";
  };

  protected String defaultReportPath() {
    return "";
  };
}
