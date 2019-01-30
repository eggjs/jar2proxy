package com.ali.jar2proxy.astparser.runtime;

import com.ali.jar2proxy.astparser.model.description.EnumDescription;
import com.ali.jar2proxy.astparser.model.description.EnumElementDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;

/**
 * A Java Enum is a special Java type used to define collections of constants.
 * @author coolme200
 */
public class EnumFiller {

  private static Logger logger = LogManager.getLogger();

  public static void fill(Map<String, Object> enumMap, URLClassLoader classLoader) {
    Iterator<Map.Entry<String, Object>> iter = enumMap.entrySet().iterator();
    while (iter.hasNext()) {
      EnumDescription desc = (EnumDescription) iter.next().getValue();
      try {
        Class clz = classLoader.loadClass(desc.getCanonicalName());
        for (EnumElementDescription emf : desc.getFields()) {
          // Returns the enum constant of the specified enum type with the specified name.
          // The name must match exactly an identifier used to declare an enum constant in this type.
          Object em = clz.getMethod("valueOf", String.class).invoke(clz, emf.getFieldName());
          for (EnumElementDescription field : desc.getEnumFields()) {
            try {
              Field ef = em.getClass().getDeclaredField(field.getFieldName());
              ef.setAccessible(true);
              Object val = ef.get(em);
              emf.getEnumValue().put(field.getFieldName(), val);
            } catch (Throwable ex) {
              logger.error("enum field " + desc.getCanonicalName() + "#" + field.getFieldName() + " failed.", ex);
            }
          }
        }
      } catch (Throwable ex) {
        logger.error("enum " + desc.getCanonicalName() + " failed.", ex);
      }
    }
  }
}
