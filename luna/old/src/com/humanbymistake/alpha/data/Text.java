package com.humanbymistake.alpha.data;

public class Text implements Value
{
  protected String value;

  public Text()
  {
    this("");
  }

  public Text(String initValue)
  {
    this.value = initValue;
  }

  public void set(String newValue)
  {
    this.value = newValue;
  }
  public String get() { return this.value; }

  public Value toText()
  {
    return this;
  }

  public Value toNum()
  {
    double n = Double.parseDouble(get());
    return new Num(n);
  }

  public Value toBit()
  {
    return new Bit(
      (get() != null) &&
      (get().length() > 0));
  }

  public Value toTree()
  {
    Tree t = new Tree();
    t.set("/", get());
    return t;
  }
}
