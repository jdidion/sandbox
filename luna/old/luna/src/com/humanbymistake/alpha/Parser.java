package com.humanbymistake.alpha;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Parser
{
  public static Value
  public static Document get(String sourceSnippet)
    throws Exception
  {
    StringReader stringReader = new StringReader(sourceSnippet);
    return get(new InputSource(stringReader));
  }

  public static Document get(File sourceFile)
    throws Exception
  {
    FileReader fileReader = new FileReader(sourceFile);
    return get(new InputSource(fileReader));
  }

  public static Document get(InputSource inputSource)
    throws Exception
  {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    return documentBuilder.parse(inputSource);
  }

}
