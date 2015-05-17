package com.humanbymistake.alpha.data;

public class Num implements Value
{
  protected double value;

  public Num()
  {
    this(0);
  }

  public Num(double initValue)
  {
    this.value = initValue;
  }

  public void set(double newValue)
  {
    this.value = newValue;
  }
  public double get() { return this.value; }

  public Value toText()
  {
    return new Text("" + get());
  }

  public Value toNum()
  {
    return this;
  }

  public Value toBit()
  {
    return new Bit(get() == 0);
  }

  public Value toTree()
  {
    Tree t = new Tree();
    t.set("/", "_" + toText());
    return t;
  }
}
