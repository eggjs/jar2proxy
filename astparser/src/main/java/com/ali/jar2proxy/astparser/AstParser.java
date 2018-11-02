package com.ali.jar2proxy.astparser;

import com.ali.jar2proxy.astparser.model.ParserInput;
import com.ali.jar2proxy.astparser.model.ParserOutput;
import com.ali.jar2proxy.astparser.model.description.EnumDescription;
import com.ali.jar2proxy.astparser.model.description.ModelDescription;
import com.ali.jar2proxy.astparser.model.description.ProxyDescription;
import com.ali.jar2proxy.astparser.runtime.Filler;
import com.alibaba.fastjson.JSONObject;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;
import com.sun.tools.javadoc.ClassDocImpl;
import com.sun.tools.javadoc.DocEnv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author coolme200
 */
public class AstParser {

  public static final ThreadLocal<AstParser> threadLocal = new ThreadLocal<AstParser>();
  private Logger logger = LogManager.getLogger();
  private ParserInput input;
  private ParserOutput output = new ParserOutput();
  private Map<String, Object> historyTypeCache = new HashMap<String, Object>();
  private DocEnv docEnv;

  /**
   * parse command params to ParserInput
   * @param args
   * @return
   */
  public ParserInput readArgs(String[] args) {
    this.input = new ParserInput();
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (!arg.startsWith("-")) {
        continue;
      }
      String key = arg.substring(1);
      String value = args[++i];
      if (value == null) {
        break;
      }
      if (ParserInput.ARG_SOURCE.equals(key)) {
        input.setSourcepath(value);
      }
      if (ParserInput.ARG_OUTPUT.equals(key)) {
        input.setOutput(value);
      }
      if (ParserInput.ARG_PROXY.equals(key)) {
        input.setProxy(new ArrayList<String>(Arrays.asList(value.split(":"))));
      }
    }
    return input;
  }

  /**
   * list all doc's superTypes
   * @param doc
   * @param superTypesMap
   */
  private void parseSuperTypes(ClassDoc doc, Map<String, Type> superTypesMap) {
    Type[] interfaceTypes = doc.interfaceTypes();
    for (Type type : interfaceTypes) {
      superTypesMap.put(type.qualifiedTypeName(), type);
      this.parseSuperTypes(type.asClassDoc(), superTypesMap);
    }
    Type superclassType = doc.superclassType();
    if (superclassType != null && !"java.lang.Object".equals(superclassType.toString())) {
      superTypesMap.put(superclassType.toString(), superclassType);
      this.parseSuperTypes(superclassType.asClassDoc(), superTypesMap);
    }
  }

  public void addType(final ClassDoc doc) {
    // primitive type will return null classDoc
    if (doc == null) {
      return;
    }
    final String qualifiedTypeName = doc.qualifiedTypeName();
    // Ignore if the type has been processed
    if (historyTypeCache.containsKey(qualifiedTypeName)) {
      return;
    }
    historyTypeCache.put(qualifiedTypeName, doc);

    try {
      // Bootstap ClassLoader is not extends ClassLoader
      // java.lang.String invoke getClassLoader() will return null
      Class clz = Class.forName(qualifiedTypeName);
      if (clz.getClassLoader() == null) {
        logger.debug(qualifiedTypeName + " jvm Bootstap ClassLoader check, classLoader is null");
        return;
      }
    } catch (ClassNotFoundException ex) {
      logger.debug(qualifiedTypeName + " jvm type check, class not found");
    }

    final Map<String, Type> superTypesMap = new HashMap<String, Type>();
    this.parseSuperTypes(doc, superTypesMap);

    if (doc.isAnnotationType()
      || doc.isAnnotationTypeElement()
      || doc.isException()
      || doc.isPrimitive()
    ) {
      logger.debug(qualifiedTypeName + " annotation & exception & primitive check, skip.");
      return;
    }

    if (superTypesMap.containsKey("java.lang.Enum")) {
      logger.debug(qualifiedTypeName + " add as enum");
      // Enum object must be java.lang.Enum's sub Class
      this.output.addEnum(qualifiedTypeName, new ParserOutput.DescriptionParser() {
        public Object exec() throws Exception {
          EnumDescription desc = new EnumDescription();
          desc.parse(doc);
          return desc;
        }
      });

    } else if (doc.isInterface()) {

      if (this.input.getProxy().indexOf(qualifiedTypeName) != -1) {
        logger.debug(qualifiedTypeName + " add as proxy");
        this.output.addProxy(qualifiedTypeName, new ParserOutput.DescriptionParser() {
          public Object exec() throws Exception {
            ProxyDescription desc = new ProxyDescription();
            desc.parse(doc);
            return desc;
          }
        });
      }

    } else {

      // object should have one supertype at least
      if (superTypesMap.size() > 0) {

        if (superTypesMap.containsKey("java.io.Serializable")) {
          logger.debug(qualifiedTypeName + " add as model");
          // All serialization object must implement java.io.Serializable
          this.output.addClass(qualifiedTypeName, new ParserOutput.DescriptionParser() {
            public Object exec() throws Exception {
              ModelDescription desc = new ModelDescription();
              desc.setSuperTypesMap(superTypesMap);
              desc.parse(doc);
              return desc;
            }
          });
        } else {
          logger.debug(qualifiedTypeName + " is not implement interface java.io.Serializable, skip add model.");
        }
        //
        Iterator<String> iter = superTypesMap.keySet().iterator();
        while (iter.hasNext()) {
          String typeName = iter.next();
          if ("java.io.Serializable".equals(typeName)) {
            continue;
          }
//            if (!profile.getDeclareMap().containsKey(typeName)) {
//              profile.getDeclareMap().put(typeName, new ArrayList());
//            }
//            profile.getDeclareMap().get(typeName).add(qualifiedTypeName);
        }
      } else {
        logger.debug(qualifiedTypeName + " without implement any interface like java.io.Serializable.");
      }
    }
  }

  /**
   * parse entry
   * @throws Exception
   */
  public void execute(RootDoc root, DocEnv docEnv) throws Exception {
    this.docEnv = docEnv;
    this.input.validate();
    ClassDocImpl[] classes = (ClassDocImpl[]) root.classes();
    for (final ClassDocImpl doc : classes) {
      this.addType(doc);
    }
  }

  public static void main(String[] args) throws Exception {

    AstParser parser = new AstParser();
    parser.readArgs(args);
    AstParser.threadLocal.set(parser);

    String subpackages = parser.input.getSubpackages();
    parser.logger.info("subpackages: " + subpackages);

    String[] docArgs = new String[] {
      // Some uncontrollable files can be filtered by this configuration
      // "-exclude", "",
      "-encoding", "UTF-8",
      "-doclet", DocletEntry.class.getName(),
      "-sourcepath", parser.input.getSourcepath(),
      "-subpackages", subpackages,
    };
    com.sun.tools.javadoc.Main.execute(docArgs);

    //
    Filler filler = new Filler(parser.getInput().getSourcepath(), parser.getOutput());
    filler.fill();

    parser.write();
  }

  private void write() throws IOException {
    String jsonString = JSONObject.toJSONString(this.getOutput(), true);
    File file = new File(this.getInput().getOutput());
    if (!file.exists()) {
      file.createNewFile();
    }
    FileWriter fw = new FileWriter(file);
    fw.write(jsonString);
    fw.flush();
    fw.close();
  }

  public ParserOutput getOutput() {
    return output;
  }

  public DocEnv getDocEnv() {
    return docEnv;
  }

  public ParserInput getInput() {
    return input;
  }

  public void setInput(ParserInput input) {
    this.input = input;
  }

}
