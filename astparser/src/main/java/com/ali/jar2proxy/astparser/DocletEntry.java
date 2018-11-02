package com.ali.jar2proxy.astparser;

import com.sun.javadoc.RootDoc;
import com.sun.tools.javadoc.DocEnv;

import java.lang.reflect.Field;

public class DocletEntry {

  public static boolean start(RootDoc root) throws Throwable {
    try {

      Field envField = root.getClass().getSuperclass().getDeclaredField("env");
      envField.setAccessible(true);
      DocEnv env = (DocEnv) envField.get(root);
      AstParser.threadLocal.get().execute(root, env);

    } catch (Throwable ex) {
      ex.printStackTrace();
    }
    return false;
  }

}
