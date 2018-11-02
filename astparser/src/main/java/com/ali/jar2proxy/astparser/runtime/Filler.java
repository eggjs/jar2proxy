package com.ali.jar2proxy.astparser.runtime;

import com.ali.jar2proxy.astparser.model.ParserOutput;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Filler {

  private ParserOutput output;
  private URLClassLoader classLoader;
  private String sourcedir;

  public Filler(String sourcedir, ParserOutput output) {
    this.sourcedir = sourcedir;
    this.output = output;
  }

  /**
   * load all *.jar except *-sources.jar
   * @throws MalformedURLException
   */
  public void init() throws MalformedURLException {
    File[] files = new File(this.sourcedir).listFiles();
    List<URL> urls = new ArrayList<URL>();
    for (File file : files) {
      if (file.isFile() && file.getName().matches(".*\\.jar$") && !file.getName().matches(".*sources\\.jar$")) {
        urls.add(new URL("file://" + file.getAbsolutePath()));
      }
    }
    this.classLoader = new URLClassLoader(urls.toArray(new URL[ urls.size() ]));
  }

  public void fill() throws MalformedURLException {
    this.init();
    DefaultValueFiller.fill(this.output.getClassMap(), this.classLoader);
    EnumFiller.fill(this.output.getEnumMap(), this.classLoader);
  }


}
