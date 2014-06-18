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

import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.rules.XMLRuleParser;
import org.sonar.plugins.cxx.utils.CxxAbstractRuleRepository;

/**
 * {@inheritDoc}
 */
public final class CxxPCLintRuleRepository extends CxxAbstractRuleRepository {
  static final String KEY = "pclint";

  /**
   * {@inheritDoc}
   */
  public CxxPCLintRuleRepository(ServerFileSystem fileSystem, XMLRuleParser xmlRuleParser) {
    super(fileSystem, xmlRuleParser, KEY);
    setName(KEY);
  }

  @Override
  protected String fileName() {
    return "/pclint.xml";
  }
}
