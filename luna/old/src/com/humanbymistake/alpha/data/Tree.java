package com.humanbymistake.alpha.data;

import org.w3c.dom.*;

import com.humanbymistake.alpha.Parser;
import com.humanbymistake.util.StringUtil;

public class Tree implements Value
{
  public static final int INDENT_WIDTH = 2;

  protected Node value;

  public Tree()
  {
    this(null);
  }
  public Tree(String initValue)
  {
    this.value = parseTree(initValue);
  }

  public void set(String newValue)
  {
    this.value = parseTree(newValue);
  }
  public String get()
  {
    Text asText = toText();
    return (String) asText.get();
  }

  protected Node resolveTreeRef(String treeRef)
  {
    // REDTAG: Make "TreeRef" type
    // REDTAG: "/1/3/14/2/-1/6.x" should return the attribute "x" on 1's 3rd child...
  }

  protected Node parseTree(String treeString)
  {
    Element root = Parser.get(treeString).getDocumentElement();
    if (root instanceof Node)
    {
      return (Node) root;
    }
    else
    {
      throw RuntimeException("Attempt to parse tree without a node root element.");
    }
  }

  protected static String toTextHelper(Node node, int indent)
  {
    StringBuffer stringBuffer = new StringBuffer();

    stringBuffer.append(StringUtil.repeat(" ", indent));
    stringBuffer.append("<" + node.getTagName());

    NamedNodeMap attributes = node.getAttributes();
    for (int i=0; i < attributes.getLength(); i++)
    {
      Node attribute = attributes.item(i);
      if (i > 0)
      {
        stringBuffer.append(",");
      }
      stringBuffer.append(" " + attribute.getNodeName() + "=" + attribute.getNodeValue());
    }

    stringBuffer.append(">\n");

    NodeList children = node.getChildNodes();
    for (int i=0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      stringBuffer.append(toTextHelper(child, indent + INDENT_WIDTH));
    }

    stringBuffer.append(StringUtil.repeat(" ", indent));
    stringBuffer.append("</" + node.getTagName() + ">\n");

    return stringBuffer.toString();
  }

  public Value toText()
  {
    return new Text(toTextHelper(0));
  }

  public Value toNum()
  {
    return new Num(value.getChildNodes().getLength());
  }

  public Value toBit()
  {
    return new Bit(value.getChildNodes().getLength() > 0);
  }

  public Value toTree()
  {
    return this;
  }
}
