package net.sf.redi.wordnet.princeton.en.domain;

import net.sourceforge.jaxor.*;
import net.sourceforge.jaxor.util.*;


public interface IndexwordEntity extends net.sourceforge.jaxor.api.EntityInterface {


    public String getPos();

    public void setPos(String arg);

    public String getLemma();

    public void setLemma(String arg);

    public Integer getIndexWordId();

    public void setIndexWordId(Integer arg);


}
