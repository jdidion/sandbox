package net.sf.redi.wordnet.princeton.en.domain;

import net.sourceforge.jaxor.*;
import net.sourceforge.jaxor.util.*;


public interface SynsetpointerEntity extends net.sourceforge.jaxor.api.EntityInterface {


    public Integer getTargetOffset();

    public void setTargetOffset(Integer arg);

    public Integer getSynsetId();

    public void setSynsetId(Integer arg);

    public String getTargetPos();

    public void setTargetPos(String arg);

    public String getPointerType();

    public void setPointerType(String arg);

    public Integer getSourceIndex();

    public void setSourceIndex(Integer arg);

    public Integer getTargetIndex();

    public void setTargetIndex(Integer arg);

    public Integer getSynsetPointerId();

    public void setSynsetPointerId(Integer arg);


}
