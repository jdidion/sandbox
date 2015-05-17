package com.humanbymistake.util;

public class StringUtil
{
  public static String repeat(String s, int count)
  {
    StringBuffer stringBuffer = StringBuffer();
    for (int i=0; i < count; i++)
    {
      stringBuffer.append(s);
    }
    return stringBuffer.toString();
  }
}
