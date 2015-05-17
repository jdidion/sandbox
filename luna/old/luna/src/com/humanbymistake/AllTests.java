package com.humanbymistake;

import junit.framework.TestCase;

public class AllTests extends TestCase
{
  public void testParser()
    throws Exception
  {
    Document
    assertTrue(com.humanbymistake.alpha.Parser.get("<test></test>") != null);
  }

  public AllTests(String initName)
  {
    super(initName);
  }
}
