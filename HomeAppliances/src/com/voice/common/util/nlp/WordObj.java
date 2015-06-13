package com.voice.common.util.nlp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogManager;

public class WordObj implements Cloneable {
    private String word;
    private ArrayList<WordObj> parents;
    private ArrayList<WordObj> sons;
    private String type;
    private WordObj binding;
    private HashMap<String, WordObj> mSameHash;
    private int depth;

    public WordObj(String word) {
        this.word = word;
        sons = new ArrayList<WordObj>();
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setSons(ArrayList<WordObj> sons) {
        this.sons = sons;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setParent(WordObj parent) {
        // this.parent = parent;
        if (parents == null) {
            parents = new ArrayList<WordObj>();
        }
        parent.addSon(this);
        parents.add(parent);

        if (type != null && !type.equals(parent.getType())) {
            System.err.println("type confict :" + type + " " + parent.getType() + "  " + this + "  " + parent);
        }
        type = parent.getType();

        // if (word.equals("阳台")) {
        // System.out.println("阳台 parent "+parent);
        // }

        // while (parent != null) {
        // type = parent.getWord();
        // parent = parent.getParent();
        // }

    }

    public ArrayList<WordObj> getParent() {
        return parents;
    }

    public void addSon(WordObj son) {
        sons.add(son);
    }

    public ArrayList<WordObj> getSons() {
        return sons;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return word + "--" + depth;
    }

    public void SetBind(WordObj bind) {
        this.binding = bind;
    }

    public WordObj getBind() {
        return binding;
    }

    public HashMap<String, WordObj> getSameWords() {
        return mSameHash;
    }

    public void setHashMap(HashMap<String, WordObj> hash) {
        this.mSameHash = hash;
    }

    public void addSame(WordObj obj) {
        if (mSameHash == null) {
            mSameHash = new HashMap<String, WordObj>();
        }
        this.mSameHash.put(obj.getWord(), obj);
        this.mSameHash.put(this.getWord(), this);
        obj.setHashMap(mSameHash);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }

}
