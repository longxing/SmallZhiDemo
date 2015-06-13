package com.voice.voicesoundwave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * trie数据结构
 * 
 * @author Chang
 *
 */
public class TrieTree {
    //根节点
    protected TrieNode mRoot;
    protected int mTrieIndex;

    protected class TrieNode{
        protected int mWord;
        protected int mPrefixes;
        protected char mChineseWord;
        protected List<TrieNode> mNodes;
        TrieNode() {
            mWord = 0;
            mPrefixes = 0;
            mNodes = new ArrayList<TrieNode>();
        }
    }
    
    public TrieTree () {
        mRoot = new TrieNode();
    }

    public TrieTree (int treeCode) {
        mRoot = new TrieNode();
        mTrieIndex = treeCode;
    }
    
    /**
     * 列出树中所有的词
     * 
     * @return 所有的词语
     */
    public List< String> listAllWords() {

        final List< String> words = new ArrayList< String>();
        final List<TrieNode> nodes = mRoot.mNodes;

        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) != null) {
                final String word = "" + nodes.get(i).mChineseWord;
                depthFirstSearchWords(words, nodes.get(i), word);
            }
        }        
        return words;
    }
    
    
    /** 
     * 深度优先查找所有的词
     * 
     * @param words 词语
     * @param node 树的节点
     * @param wordSegment
     */
    private void depthFirstSearchWords(List<String> words, TrieNode node, String wordSegment) {
        final List<TrieNode> nodes = node.mNodes;
        boolean hasChildren = false;
        
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) != null) {
                hasChildren = true;
                final String newWord = wordSegment + nodes.get(i).mChineseWord;               
                depthFirstSearchWords(words, nodes.get(i), newWord);
            }            
        }
        
        if (!hasChildren) {
            words.add(wordSegment);
        }
    }
    
    
    /**
     * @param prefix 前缀的词
     * @return 包含输入前缀词的所有词的个数
     */
    public int countPrefixes(String prefix) {
        return countPrefixes(mRoot, prefix);
    }

    private int countPrefixes(TrieNode node, String prefixSegment) {
        if (prefixSegment.length() == 0) { //reach the last character of the word
            return node.mPrefixes;
        }

        final char c = prefixSegment.charAt(0);

        boolean isFind = false;
        final Iterator<TrieNode> listiterator = node.mNodes.listIterator();
        int index = 0;
        
        while(listiterator.hasNext()) {
            
            final TrieNode currentNode = (TrieNode)listiterator.next();
            
            if(currentNode.mChineseWord == c) {
                isFind = true;
                break;
            }
            index++;
        } 
        
        if (!isFind) {
            return 0;
        } else {

            return countPrefixes(node.mNodes.get(index), prefixSegment.substring(1));

        }        

    }

    /**
     * @param word 词语
     * @return 书中词语的个数
     */
    public int countWords(String word) {
        return countWords(mRoot, word);
    }    

    private int countWords(TrieNode node, String wordSegment) {
        
        if (wordSegment.length() == 0) {
            return node.mWord;
        }

        final char c = wordSegment.charAt(0);
        
        boolean isFind = false;
        final Iterator<TrieNode> listiterator = node.mNodes.listIterator();
        int index = 0;
        
        while(listiterator.hasNext()) {
            final TrieNode currentNode = (TrieNode)listiterator.next();
            
            if(currentNode.mChineseWord == c) {
                isFind = true;
                break;
            }
            index++;
        } 
        
        //如果字没有存在
        if (!isFind) {
            return 0;
        } else {
            return countWords(node.mNodes.get(index), wordSegment.substring(1));

        }        

    }
    
    /**
     * 在树里添加一个词语
     * 
     * @param word 被添加的词语
     */
    public void addWord(String word) {
        addWord(mRoot, word);
    }


    /** 
     * 在指定的地方添加汉字
     * @param node 指定的节点
     * @param word 要添加的词语
     */
    private void addWord(TrieNode node, String word) {
    	//如果所有的字都被添加了
        if (word.length() == 0) { 
            node.mWord ++;
        } else {
            boolean isFind = false;
            node.mPrefixes ++;
            
            //取词语的首字
            char c = word.charAt(0);
//            if(!isChinese(c)){
//                c = Character.toLowerCase(c);
//            }
            
            //检查汉字是不是已经在节点中
            final Iterator<TrieNode> listiterator = node.mNodes.listIterator();
            int index = 0;
            
            while(listiterator.hasNext()) {
                
                final TrieNode currentNode = (TrieNode)listiterator.next();
                if(currentNode.mChineseWord == c) {
                    isFind = true;
                    break;
                }
                index++;
            } 
            
            //如果在什么都不干，往下添加
            //如果不在就在当前位置添加新节点
            TrieNode newNode;
            if(isFind){
                newNode = node.mNodes.get(index);              
            }else{
                newNode = new TrieNode();
                newNode.mChineseWord = c;
                node.mNodes.add(newNode);
            }
            
            addWord(newNode, word.substring(1)); //下一个字符
        }
    }
    
    private boolean isChinese(char c){
        return (c >= 0x4e00) && (c <= 0x9fbb);
    }
    
    /**
     * 返回指定字段前缀匹配最长的单词。
     * 
     * @param word 词组
     * @return 最大匹配的词组
     */
    public List<String> getMaxMatchWord(String word) {
        
        final List<String> matchWordList = new ArrayList<String>();
        String s = "";
        String temp = "";// 记录最近一次匹配最长的单词
        final char[] w = word.toCharArray();
        TrieNode node = mRoot;
        int testWordLength = 0;
        for (int i = 0; i < w.length; i++) {
        	
            char c = w[i];
            
            //检查是否为汉字
//            if(!isChinese(c)){
//                c = Character.toLowerCase(c);
//            }
            
            boolean isFind = false;
            boolean isWord = false;
            //检查汉字是不是已经在节点中
            final Iterator<TrieNode> listiterator = node.mNodes.listIterator();
            int index = 0;
            while(listiterator.hasNext()) {
                final TrieNode currentNode = (TrieNode)listiterator.next();
                if(currentNode.mChineseWord == c) {
                    isFind = true;
                    break;
                }
                index++;
            } 
            
            if (!isFind) {

            	// 如果是一个单词，则返回
                if (node.mWord != 0){
                	testWordLength = 0;
                    matchWordList.add(s);
                    if(s.contains(temp)){
                        temp = "";
                    }
                    isWord = true;
                }
                
                if(!"".equals(s)) {
                	s = "";
                    node = mRoot;
                    if(isWord) {
                    	i--;
                    	isWord = false;
                    } else {
                    	i=i-testWordLength;
                    	testWordLength = 0;
                    }
                    
                }
                    
            } else {
                if (node.mWord != 0){
                    temp = s;
                }
                
                
                s += c;
                node = node.mNodes.get(index);
                boolean isFinish = node.mWord == 1;
                testWordLength++;
                if(i == w.length - 1 && !"".equals(s) && isFinish){
                	testWordLength = 0;
                    matchWordList.add(s);
                }
            }
            
        }
     // trie中存在比指定单词更长（包含指定词）的单词
        if (node.mWord == 0 && !"".equals(temp)){
              matchWordList.add(temp);
        }
           
        return matchWordList;
    }
    
  public static void main(String args[]) {
  TrieTree trie = new TrieTree(1);

  trie.addWord("预订一下");
  trie.addWord("预定一下");
  trie.addWord("预订下");
  trie.addWord("预订个");
  trie.addWord("预定下");
  trie.addWord("预定个");
  trie.addWord("预约一个");
  trie.addWord("预约");
  trie.addWord("查下");
  trie.addWord("查一个");
  trie.addWord("查一下");
  trie.addWord("查个");
  trie.addWord("旅店");
  trie.addWord("刘德华");
  trie.addWord("电影");
  
  TrieTree trie2 = new TrieTree(1);
//  trie2.addWord("查下");
//  trie2.addWord("查一个");
//  trie2.addWord("查一下");
//  trie2.addWord("查个");
  System.out.println(trie.mRoot.mPrefixes);
  System.out.println(trie.mRoot.mWord);



  List<String> list = trie.listAllWords();
  Iterator<String> listiterator = list.listIterator();

  while(listiterator.hasNext()) {
      String s = (String)listiterator.next();
      System.out.println(s);
  }


//  int count = trie.countPrefixes("预订");
//  int count1=trie.countWords("预定下");
//  System.out.println("the count of c prefixes:"+count);
//  System.out.println("the count of china countWords:"+count1);
//  List<String> maxMatch = trie.getMaxMatchWord("帮我预约一个保定查一个附近的旅店");
//  System.out.println("==========================================");
//  System.out.println(maxMatch);
  
  List<String> maxMatch2 = trie.getMaxMatchWord("刘德华电影");
  System.out.println("==========================================");
  System.out.println(maxMatch2);

}
    

//    public static void main(String args[]) {
//        TrieTree trie = new TrieTree(1);
//    	TrieTree trie = new TrieTree();
//        trie.addWord("Ԥ��һ��");
//        trie.addWord("Ԥ��һ��");
//        trie.addWord("Ԥ����");
//        trie.addWord("Ԥ����");
//        trie.addWord("Ԥ����");
//        trie.addWord("Ԥ����");
//        trie.addWord("ԤԼһ��");
//        trie.addWord("ԤԼ");
//        trie.addWord("����");
//        trie.addWord("��һ��");
//        trie.addWord("��һ��");
//        trie.addWord("���");
//        trie.addWord("�õ�");
        
//        TrieTree trie2 = new TrieTree(1);
//        trie2.addWord("����");
//        trie2.addWord("��һ��");
//        trie2.addWord("��һ��");
//        trie2.addWord("���");
//        System.out.println(trie.mRoot.mPrefixes);
//        System.out.println(trie.mRoot.mWord);
//
//
//
//        List<String> list = trie.listAllWords();
//        Iterator<String> listiterator = list.listIterator();
//
//        while(listiterator.hasNext()) {
//            String s = (String)listiterator.next();
//            System.out.println(s);
//        }
//
//
//        int count = trie.countPrefixes("Ԥ��");
//        int count1=trie.countWords("Ԥ����");
//        System.out.println("the count of c prefixes:"+count);
//        System.out.println("the count of china countWords:"+count1);
//        List<String> maxMatch = trie.getMaxMatchWord("����ԤԼһ��������һ��������õ�");
//        System.out.println("==========================================");
//        System.out.println(maxMatch);
//   
//    }
}
