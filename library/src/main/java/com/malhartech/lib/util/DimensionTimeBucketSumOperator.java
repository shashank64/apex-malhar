/*
 *  Copyright (c) 2012-2013 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.lib.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.mutable.MutableDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Yan <davidyan@malhar-inc.com>
 */
public class DimensionTimeBucketSumOperator extends DimensionTimeBucketOperator
{
  private static final Logger LOG = LoggerFactory.getLogger(DimensionTimeBucketSumOperator.class);
  private Map<String, Map<String, Number>> dataMap = new HashMap<String, Map<String, Number>>();

  @Override
  public void process(String timeBucket, String key, String field, Number value)
  {
    String finalKey = timeBucket + "|" + key;
    Map<String, Number> m = dataMap.get(finalKey);
    if (value == null) {
      return;
    }
    if (m == null) {
      m = new HashMap<String, Number>();
      m.put(field, new MutableDouble(value));
      dataMap.put(finalKey, m);
    }
    else {
      Number n = m.get(field);
      if (n == null) {
        m.put(field, new MutableDouble(value));
      } else {
        ((MutableDouble)n).add(value);
      }
    }
  }

  @Override
  public void endWindow()
  {
    if (!dataMap.isEmpty()) {
      out.emit(dataMap);
      LOG.info("Number of keyval pairs: {}", dataMap.size());
    }
  }

}
