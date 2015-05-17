package com.humanbymistake.alpha.data;

public class Bit implements Value
{
  protected boolean value;

  public Bit()
  {
    this(false);
  }

  public Bit(boolean initValue)
  {
    this.value = initValue;
  }

  public void set(boolean newValue)
  {
    this.value = newValue;
  }
  public boolean get() { return this.value; }

  public Value toText()
  {
    return new Text(get() ? "true" : "false");
  }

  public Value toNum()
  {
    return new Num(get() ? 1 : 0);
  }

  public Value toBit()
  {
    return this;
  }

  public Value toTree()
  {
    Tree t = new Tree();
    t.set("/", toText());
    return t;
  }
}
