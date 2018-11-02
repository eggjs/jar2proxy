package com.ali.jar2proxy.astparser.runtime;

import com.ali.jar2proxy.astparser.Reflect;
import com.ali.jar2proxy.astparser.model.description.FieldDescription;
import com.ali.jar2proxy.astparser.model.description.ModelDescription;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.*;

public class DefaultValueFiller {

  private static Logger logger = LogManager.getLogger();

  public static void fill(Map<String, Object> classMap, URLClassLoader classLoader) {
    Iterator<Map.Entry<String, Object>> iter = classMap.entrySet().iterator();
    while(iter.hasNext()) {
      ModelDescription model = (ModelDescription) iter.next().getValue();

      // abstract can't be serialized
      if (Boolean.TRUE.equals(model.getAbstractClass())) {
        continue;
      }

      try {
        Class clz = classLoader.loadClass(model.getCanonicalName());
        // date & the class instance the same time
        long now = System.currentTimeMillis();
        Object instance = Reflect.getInstance(clz);
        if (model.getFields().isEmpty()) {
          continue;
        }
        for (FieldDescription field : model.getFields()) {
          if (Boolean.TRUE.equals(field.getIsStatic()) || Boolean.TRUE.equals(field.getIsTransient())) {
            continue;
          }
          try {
            String hump = field.getFieldName().substring(0, 1).toUpperCase() + field.getFieldName().substring(1);
            Method md = null;
            // normal type
            // public boolean isSyncCheck
            // possible methods:
            // => getIsSyncCheck
            // => isIsSyncCheck
            // => isSyncCheck
            List<String> methodNames = new ArrayList<String>();
            methodNames.add("get" + hump);
            if ("boolean".equals(field.getCanonicalName())) {
              methodNames.add("is" + hump);
            }
            methodNames.add(field.getFieldName());
            for (String methodName : methodNames) {
              try {
                md = clz.getMethod(methodName);
                // successfuly find get method, then break the loop
                break;
              } catch (NoSuchMethodException e) {
                logger.debug("method " + methodName + " not found.");
              }
            }
            if (md == null) {
              logger.info("Get method for field " + model.getCanonicalName() + "#" + field.getFieldName() + " not found.");
              continue;
            }

            Object obj = md.invoke(instance);
            if (obj != null) {
              if (field.getCanonicalName().equals("java.util.Date")) {
                long that = ((Date) obj).getTime();
                if (that == now) {
                  field.setDefaultValue("new Date()");
                } else {
                  field.setDefaultValue(obj);
                }
              } else {
                try {
                  // try json format
                  JSONObject.toJSONString(obj, true);
                  field.setDefaultValue(obj);
                } catch(Throwable ex) {
                  logger.error("stringify " + field.getFieldName() + " field value failed", ex);
                }
              }
            }
          } catch (Throwable ex) {
            logger.error("get field " + field.getFieldName() + " default value failed", ex);
          }
        }
      } catch (Throwable ex) {
        logger.error("init class " + model.getCanonicalName() + " failed", ex);
      }
    }
  }
}
