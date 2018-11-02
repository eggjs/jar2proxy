package com.ali.jar2proxy.astparser;

import com.sun.tools.javac.code.Symbol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Reflect {

  public static Object getField(Class clz, Object instance, String name) throws NoSuchFieldException, IllegalAccessException {
    Field field = clz.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(instance);
  }

  public static Symbol.ClassSymbol getTsym(Object obj) throws NoSuchFieldException, IllegalAccessException {
    return (Symbol.ClassSymbol) Reflect.getField(obj.getClass(), obj, "tsym");
  }

  public static Symbol getSym(Object obj) throws NoSuchFieldException, IllegalAccessException {
    return (Symbol) Reflect.getField(obj.getClass(), obj, "sym");
  }

  /**
   * @hessian
   * @param cl
   * @return
   * @throws Exception
   */
  public static Object getInstance(Class cl) throws Exception {
    Constructor _constructor = null;
    Object[] _constructorArgs = null;
    Constructor[] constructors = cl.getDeclaredConstructors();
    long bestCost = Long.MAX_VALUE;

    for (int i = 0; i < constructors.length; i++) {
      Class []param = constructors[i].getParameterTypes();
      long cost = 0;

      for (int j = 0; j < param.length; j++) {
        cost = 4 * cost;

        if (Object.class.equals(param[j]))
          cost += 1;
        else if (String.class.equals(param[j]))
          cost += 2;
        else if (int.class.equals(param[j]))
          cost += 3;
        else if (long.class.equals(param[j]))
          cost += 4;
        else if (param[j].isPrimitive())
          cost += 5;
        else
          cost += 6;
      }

      if (cost < 0 || cost > (1 << 48))
        cost = 1 << 48;

      cost += param.length << 48;

      if (cost < bestCost) {
        _constructor = constructors[i];
        bestCost = cost;
      }
    }

    if (_constructor != null) {
      _constructor.setAccessible(true);
      Class []params = _constructor.getParameterTypes();
      _constructorArgs = new Object[params.length];
      for (int i = 0; i < params.length; i++) {
        _constructorArgs[i] = getParamArg(params[i]);
      }
    }

    if (_constructor != null)
      return _constructor.newInstance(_constructorArgs);
    else
      return cl.newInstance();
  }

  /**
   * Creates a map of the classes fields.
   */
  protected static Object getParamArg(Class cl)
  {
    if (! cl.isPrimitive())
      return null;
    else if (boolean.class.equals(cl))
      return Boolean.FALSE;
    else if (byte.class.equals(cl))
      return new Byte((byte) 0);
    else if (short.class.equals(cl))
      return new Short((short) 0);
    else if (char.class.equals(cl))
      return new Character((char) 0);
    else if (int.class.equals(cl))
      return Integer.valueOf(0);
    else if (long.class.equals(cl))
      return Long.valueOf(0);
    else if (float.class.equals(cl))
      return Float.valueOf(0);
    else if (double.class.equals(cl))
      return Double.valueOf(0);
    else
      throw new UnsupportedOperationException();
  }

}
