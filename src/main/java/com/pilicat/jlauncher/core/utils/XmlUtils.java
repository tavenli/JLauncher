package com.pilicat.jlauncher.core.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtils {

	   /**
     * 帮你快速获得一个 DocumentBuilder，方便 XML 解析。
     * 
     * @return 一个 DocumentBuilder 对象
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder xmls() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    /**
     * 快捷的解析 XML 文件的帮助方法，它会主动关闭输入流
     * 
     * @param ins
     *            XML 文件输入流
     * @return Document 对象
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    public static Document xml(InputStream ins) throws SAXException, IOException, ParserConfigurationException {
        try {
            return xmls().parse(ins);
        }        
        finally {
            safeClose(ins);
        }
    }

    /**
     * 快捷的解析 XML 文件的帮助方法
     * 
     * @param xmlFile
     *            XML 文件
     * @return Document 对象
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    public static Document xml(File xmlFile) throws SAXException, IOException, ParserConfigurationException {
    	return xmls().parse(xmlFile);
    }

    /**
     * 获取某元素下某节点的全部文本内容
     * 
     * @param ele
     *            元素
     * @param subTagName
     *            子元素名
     * @return 内容，null 表示子元素不存在, 空串表示元素没有对应文本内容
     */
    public static String get(Element ele, String subTagName) {
        Element sub = firstChild(ele, subTagName);
        if (null == sub)
            return null;
        return sub.getTextContent()==null ? "" : sub.getTextContent();
    }

    /**
     * 获取 XML 元素内第一个子元素
     * 
     * @param ele
     *            XML 元素
     * @return 子元素，null 表示不存在
     */
    public static Element firstChild(Element ele) {
        return firstChild(ele,null);
    }

    /**
     * 获取 XML 元素内第一个名字所有符合一个正则表达式的子元素
     * 
     * @param ele
     *            XML 元素
     * @param regex
     *            元素名称正则表达式
     * @return 子元素，null 表示不存在
     */
    public static Element firstChild(Element ele, String regex) {
    	Element[] tag = new Element[1];
    	NodeList nodeList = ele.getChildNodes();
        // 循环子
        int len = nodeList.getLength(); 
        // 正则式
        Pattern p = null == regex ? null : Pattern.compile(regex);

        for (int i = 0; i < len; i++) {
        	Node node = nodeList.item(i);
            if (node instanceof Element){
            	Element element = (Element) node;
                if (null == p || p.matcher(element.getTagName()).find()){
                	 tag[0] = element;
                }
            }
        }
        
        return  tag[0];
    }

    /**
     * 获取 XML 元素内最后一个子元素
     * 
     * @param ele
     *            XML 元素
     * @return 子元素，null 表示不存在
     */
    public static Element lastChild(Element ele) {
        return lastChild(ele, null);
    }

    /**
     * 获取 XML 元素内最后一个名字所有符合一个正则表达式的子元素
     * 
     * @param ele
     *            XML 元素
     * @param regex
     *            元素名称正则表达式
     * @return 子元素，null 表示不存在
     */
    public static Element lastChild(Element ele, String regex) {
    	Element[] tag = new Element[1];
    	NodeList nodeList = ele.getChildNodes();
        // 循环子
        int len = nodeList.getLength();
        // 正则式
        Pattern p = null == regex ? null : Pattern.compile(regex);
        
        //
        for (int i = len + -1; i >= 0; i--) {
        	Node node = nodeList.item(i);
            if (node instanceof Element){
            	Element element = (Element) node;
                if (null == p || p.matcher(element.getTagName()).find()){
                	tag[0] = element;
                }
            }
        }

        return tag[0];
    }

    /**
     * 获取 XML 元素内所有子元素
     * 
     * @param ele
     *            XML 元素
     * @return 一个子元素的列表
     */
    public static List<Element> childrens(Element ele) {
        return childrens(ele, null);
    }

    /**
     * 获取 XML 元素内名字符合一个正则表达式的元素
     * 
     * @param ele
     *            XML 元素
     * @param regex
     *            元素名称正则表达式
     * @return 一个子元素的列表
     */
    public static List<Element> childrens(Element ele, String regex) {
    	NodeList nodeList = ele.getChildNodes();
        // 循环子
        int len = nodeList.getLength();
        List<Element> list = new ArrayList<Element>(len);        
        // 正则式
        Pattern p = null == regex ? null : Pattern.compile(regex);
        
        for (int i = 0; i < len; i++) {
        	Node node = nodeList.item(i);
            if (node instanceof Element){
            	Element tag = (Element) node;
                if (null == p || p.matcher(tag.getTagName()).find()){
                	list.add(tag);
                }
            }
        }

        return list;
    }



    /**
     * 获取该 XML 元素内所有的属性的值，按照Map的形式返回
     * 
     * @param ele
     *            XML 元素
     * @return 所有属性的值
     */
    public static Map<String, String> getAttrs(Element ele) {
        NamedNodeMap nodeMap = ele.getAttributes();
        Map<String, String> attrs = new HashMap<String, String>(nodeMap.getLength());
        for (int i = 0; i < nodeMap.getLength(); i++) {
            attrs.put(nodeMap.item(i).getNodeName(), nodeMap.item(i)
                                                            .getNodeValue());
        }
        return attrs;
    }

    /**
     * 从 XML 元素中得到指定属性的值，如该指定属性不存在，则返回Null
     * 
     * @param ele
     *            XML 元素
     * @return 该指定属性的值
     */
    public static String getAttr(Element ele, String attrName) {
        Node node = ele.getAttributes().getNamedItem(attrName);
        return node != null ? node.getNodeValue() : null;
    }
    
    
    /**
     * 关闭一个可关闭对象，可以接受 null。如果成功关闭，返回 true，发生异常 返回 false
     * 
     * @param cb
     *            可关闭对象
     * @return 是否成功关闭
     */
    public static boolean safeClose(Closeable cb) {
        if (null != cb)
            try {
                cb.close();
            }
            catch (IOException e) {
                return false;
            }
        return true;
    }
    
}
