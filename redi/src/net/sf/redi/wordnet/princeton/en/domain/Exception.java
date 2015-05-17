package net.sf.redi.wordnet.princeton.en.domain;

import net.sourceforge.jaxor.*;
import net.sourceforge.jaxor.util.*;


public interface Exception extends net.sourceforge.jaxor.api.EntityInterface {
    public String getPos();
    public void setPos(String arg);
    public String getLemma();
    public void setLemma(String arg);
    public String getException();
    public void setException(String arg);
    public Integer getExceptionId();
    public void setExceptionId(Integer arg);
}