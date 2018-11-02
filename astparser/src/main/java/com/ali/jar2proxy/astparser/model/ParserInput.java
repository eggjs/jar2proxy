package com.ali.jar2proxy.astparser.model;

import com.ali.jar2proxy.astparser.EmptyProxyException;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class ParserInput {

  public static final String ARG_SOURCE = "source";
  public static final String ARG_OUTPUT = "output";
  public static final String ARG_PROXY = "proxy";

  private String sourcepath;
  private String output;
  private List<String> proxy;
  private String subpackages;

  public String getSubpackages() {
    File file = new File(this.sourcepath);
    File[] subFiles = file.listFiles();
    String subpackages = "";
    for (File subFile : subFiles) {
      if (!subFile.isDirectory() || subFile.getName().indexOf("META-INF") != -1) {
        continue;
      }
      File[] secFiles = subFile.listFiles();
      for (File secFile : secFiles) {
        if (secFile.isDirectory()) {
          subpackages += ":" + subFile.getName() + "." + secFile.getName();
        }
      }
    }
    return subpackages;
  }

  public String getSourcepath() {
    return sourcepath;
  }

  public void setSourcepath(String sourcepath) {
    this.sourcepath = sourcepath;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public List<String> getProxy() {
    return proxy;
  }

  public void setProxy(List<String> proxy) {
    this.proxy = proxy;
  }

  public void validate() throws FileNotFoundException {
    if (!new File(this.sourcepath).exists()) {
      throw new FileNotFoundException("Specified sources dir is not found.");
    }
    if (this.proxy == null || this.proxy.size() == 0) {
      throw new EmptyProxyException();
    }
  }

  public String toString() {
    return JSONObject.toJSONString(this);
  }


}
