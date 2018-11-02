package com.ali.jar2proxy.normal.facade;

import com.ali.jar2proxy.normal.model.ListSubClass;
import com.ali.jar2proxy.normal.model.MapSubClass;

public interface SubClassFacade {

  public ListSubClass queryWithName(MapSubClass param);

  public MapSubClass queryWithName(ListSubClass param);
}
