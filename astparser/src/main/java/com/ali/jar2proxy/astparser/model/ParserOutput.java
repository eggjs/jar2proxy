package com.ali.jar2proxy.astparser.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserOutput {

  private Logger logger = LogManager.getLogger();
  private Map<String, Object> proxyMap = new HashMap<String, Object>();
  private Map<String, Object> enumMap = new HashMap<String, Object>();
  private Map<String, Object> classMap = new HashMap<String, Object>();
  private Map<String, List<String>> declareMap = new HashMap<String, List<String>>();

  public void addProxy(String name, DescriptionParser parser) {
    if (!this.proxyMap.containsKey(name)) {
      try {
        this.proxyMap.put(name, parser.exec());
      } catch (Exception ex) {
        if (logger.isDebugEnabled()) {
          ex.printStackTrace();
        }
        logger.error(name, ex);
      }

    }
  }

  public void addEnum(String name, DescriptionParser parser) {
    if (!this.enumMap.containsKey(name)) {
      try {
        this.enumMap.put(name, parser.exec());
      } catch (Exception ex) {
        if (logger.isDebugEnabled()) {
          ex.printStackTrace();
        }
        logger.error(name, ex);
      }

    }
  }

  public void addClass(String name, DescriptionParser parser) {
    if (!this.classMap.containsKey(name)) {
      try {
        this.classMap.put(name, parser.exec());
      } catch (Exception ex) {
        if (logger.isDebugEnabled()) {
          ex.printStackTrace();
        }
        logger.error(name, ex);
      }
    }
  }

  public Map<String, Object> getProxyMap() {
    return proxyMap;
  }

  public void setProxyMap(Map<String, Object> proxyMap) {
    this.proxyMap = proxyMap;
  }

  public Map<String, Object> getEnumMap() {
    return enumMap;
  }

  public void setEnumMap(Map<String, Object> enumMap) {
    this.enumMap = enumMap;
  }

  public Map<String, Object> getClassMap() {
    return classMap;
  }

  public void setClassMap(Map<String, Object> classMap) {
    this.classMap = classMap;
  }

  public Map<String, List<String>> getDeclareMap() {
    return declareMap;
  }

  public void setDeclareMap(Map<String, List<String>> declareMap) {
    this.declareMap = declareMap;
  }

  public interface DescriptionParser {
    Object exec() throws Exception;
  }

}
