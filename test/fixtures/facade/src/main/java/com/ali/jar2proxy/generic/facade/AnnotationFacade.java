package com.ali.jar2proxy.generic.facade;

import com.ali.jar2proxy.generic.facade.annotation.*;
import com.ali.jar2proxy.generic.model.TMgetPopCountResult;

public interface AnnotationFacade {

  @BizAction(name = VccTwoPhaseConstant.USER_FREEZE, commitMethod = VccTwoPhaseConstant.METHOD_COMMIT)
  @ZoneRoute(uidGenerator = "xy")
  TMgetPopCountResult freeze(final String txId,
                             final Long actionId,
                             @BizActionParameter(paramName = "userId") @ZoneRouteParam String userId);

}
