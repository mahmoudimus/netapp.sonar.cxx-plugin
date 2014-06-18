/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2011 Waleri Enns and CONTACT Software GmbH
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
package org.sonar.cxx.preprocessor;

import java.util.HashMap;
import java.util.Map;

public class MapChain<K, V> {
  private Map<K, V> highPrioMap = new HashMap<K, V>();
  private Map<K, V> lowPrioMap = new HashMap<K, V>();
  private Map<K, V> highPrioDisabled = new HashMap<K, V>();
  private Map<K, V> lowPrioDisabled = new HashMap<K, V>();

  public V get(Object key) {
    V value = highPrioMap.get(key);
    return value != null ? value : lowPrioMap.get(key);
  }

  public V putHighPrio(K key, V value) {
    return highPrioMap.put(key, value);
  }

  public V putLowPrio(K key, V value) {
    return lowPrioMap.put(key, value);
  }

  public V removeLowPrio(K key) {
    return lowPrioMap.remove(key);
  }

  public void clearLowPrio() {
    lowPrioMap.clear();
  }

  public void disable(K key) {
    move(key, lowPrioMap, lowPrioDisabled);
    move(key, highPrioMap, highPrioDisabled);
  }

  public void enable(K key) {
    move(key, lowPrioDisabled, lowPrioMap);
    move(key, highPrioDisabled, highPrioMap);
  }

  private void move(K key, Map<K, V> from, Map<K, V> to) {
    V value = from.remove(key);
    if (value != null) {
      to.put(key, value);
    }
  }
}
