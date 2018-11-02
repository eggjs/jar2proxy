package com.ali.jar2proxy.astparser;

public class ParserTest {

  public static void main(String[] args) throws Exception {
    String[] docArgs = new String[] {
        "-source", "/var/folders/k2/8t5wvpp91mz1056nkms3w7gw0000gn/T/1540189508159",
        "-output", "/Users/coolme200/Downloads/proxy-ast.json",
        "-proxy", "com.ali.jar2proxy.normal.facade.SubClassFacade:" +
                  "com.ali.jar2proxy.normal.facade.UserFacade:" +
                  "com.ali.jar2proxy.generic.facade.TopicConcernFacade:" +
                  "com.ali.jar2proxy.extend.facade.UserConsultFacade:" +
                  "com.ali.jar2proxy.generic.facade.AnnotationFacade"
    };
    AstParser.main(docArgs);
  }

}
