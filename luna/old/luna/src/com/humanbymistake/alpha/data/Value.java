package com.humanbymistake.alpha.data;

public interface Value
{
  public Value toText();
  public Value toNum();
  public Value toBit();
  public Value toTree();
}
