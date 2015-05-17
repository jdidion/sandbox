package net.sf.redi.wordnet.princeton.en.domain;

import net.sourceforge.jaxor.*;
import net.sourceforge.jaxor.util.*;


public interface SynsetwordEntity extends net.sourceforge.jaxor.api.EntityInterface {


    public String getWord();

    public void setWord(String arg);

    public Integer getWordIndex();

    public void setWordIndex(Integer arg);

    public Integer getSynsetId();

    public void setSynsetId(Integer arg);

    public Integer getSynsetWordId();

    public void setSynsetWordId(Integer arg);


}
