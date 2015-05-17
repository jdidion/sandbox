package net.sf.redi.wordnet.princeton.en.domain;

import net.sourceforge.jaxor.*;
import net.sourceforge.jaxor.util.*;


public interface SynsetEntity extends net.sourceforge.jaxor.api.EntityInterface {


    public String getPos();

    public void setPos(String arg);

    public Integer getFileOffset();

    public void setFileOffset(Integer arg);

    public java.lang.Byte getIsAdjCluster();

    public void setIsAdjCluster(java.lang.Byte arg);

    public String getGloss();

    public void setGloss(String arg);

    public Integer getSynsetId();

    public void setSynsetId(Integer arg);


}
