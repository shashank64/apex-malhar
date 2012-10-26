/**
 * Copyright (c) 2012-2012 Malhar, Inc. All rights reserved.
 */
package com.malhartech.lib.algo;

import com.malhartech.api.OperatorConfiguration;
import com.malhartech.dag.TestCountAndLastTupleSink;
import com.malhartech.dag.TestSink;
import com.malhartech.lib.testbench.*;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Functional tests for {@link com.malhartech.lib.testbench.EventGenerator}. <p>
 * <br>
 * Load is generated and the tuples are outputted to ensure that the numbers are roughly in line with the weights<br>
 * <br>
 * Benchmarks:<br>
 * String schema generates over 11 Million tuples/sec<br>
 * HashMap schema generates over 1.7 Million tuples/sec<br>
 * <br>
 * DRC checks are validated<br>
 *
 */
public class AllAfterMatchTest
{
  private static Logger log = LoggerFactory.getLogger(AllAfterMatchTest.class);

  /**
   * Test node logic emits correct results
   */
  @Test
  @SuppressWarnings("SleepWhileInLoop")
  public void testNodeProcessing() throws Exception
  {
    testNodeProcessingSchema(new AllAfterMatch<String, Integer>());
    testNodeProcessingSchema(new AllAfterMatch<String, Double>());
    testNodeProcessingSchema(new AllAfterMatch<String, Float>());
    testNodeProcessingSchema(new AllAfterMatch<String, Short>());
    testNodeProcessingSchema(new AllAfterMatch<String, Long>());
  }

  public void testNodeProcessingSchema(AllAfterMatch oper)
  {
    TestSink<HashMap<String, Number>> allSink = new TestSink<HashMap<String, Number>>();
    oper.allafter.setSink(allSink);
    oper.setup(new OperatorConfiguration());
    oper.setKey("a");
    oper.setValue(3.0);
    oper.setTypeEQ();

    oper.beginWindow();
    HashMap<String, Number> input = new HashMap<String, Number>();
    input.put("a", 2);
    input.put("b", 20);
    input.put("c", 1000);
    oper.data.process(input);
    input.clear();
    input.put("a", 3);
    oper.data.process(input);


    input.clear();
    input.put("b", 6);
    oper.data.process(input);

    input.clear();
    input.put("c", 9);
    oper.data.process(input);

    oper.endWindow();

    Assert.assertEquals("number emitted tuples", 3, allSink.collectedTuples.size());
    for (Object o: allSink.collectedTuples) {
      for (Map.Entry<String, Number> e: ((HashMap<String, Number>)o).entrySet()) {
        if (e.getKey().equals("a")) {
          Assert.assertEquals("emitted value for 'a' was ", new Double(3), e.getValue().doubleValue());
        }
        else if (e.getKey().equals("b")) {
          Assert.assertEquals("emitted tuple for 'b' was ", new Double(6), e.getValue().doubleValue());
        }
        else if (e.getKey().equals("c")) {
          Assert.assertEquals("emitted tuple for 'c' was ", new Double(9), e.getValue().doubleValue());
        }
      }
    }
  }
}
