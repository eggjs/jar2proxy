package com.ali.jar2proxy.extend.facade;

import com.ali.jar2proxy.extend.model.UccUserCalcConsultRequest;
import com.ali.jar2proxy.extend.model.UserConsultRequest;
import com.ali.jar2proxy.extend.model.UserConsultResult;

/**
 * @author coolme200
 */
public interface UserConsultFacade {

    public UserConsultResult calcConsultVoucher(UccUserCalcConsultRequest voucherCalcConsultRequest);

    public UserConsultResult fetchUser(UserConsultRequest voucherConsultRequest);
}
