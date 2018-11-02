package com.ali.jar2proxy.generic.enums;

/**
 * 枚举了当前支持的各种产生评论的主题的类型；<br>
 * 1. 数据库查询的时候使用使用value值<br>
 * 2. 使用缓存的时候，使用name做为前缀;<br>
 * <p>
 * 数据库处理的时候不用前缀的两个原因：<br>
 * 1. 过长的前缀可能会超过字符数限制；<br>
 * 2. 简单的使用value前缀担心不同主题下会产生相同的id<br>
 * 
 * PS: 数字是为了给DB存贮使用的，使用列表存贮、存贮直接用name即可
 * @author tianchi.ya
 * @version $Id: TOPIC_TYPE.java,v 0.1 2014年8月18日 上午9:58:30 tianchi.ya Exp $
 */
public enum TOPIC_TYPE {
    // README:
    // 和数据仓库的约定，类型的值位数最大不能超过4位

    //----------------------------------//
    // 资讯相关的值从0-999                  //
    //----------------------------------//
    /** 新闻资讯的类型值 */
    INFO_TYPE_NEWS(0), /** 公告类型 */
    INFO_TYPE_ANNOUNCEMENT(1), /** 研报类型 */
    INFO_TYPE_RESEARCH_REPORT(2),

    //--------------------------------------//
    // 股吧，概念吧， XXX吧相关的 1000~1999         //
    //--------------------------------------//
    /** 股票的类型值 */
    STOCK(1000), /** 概念吧的类型值 */
    CONCEPT(1001), /** 话题类型  */
    TALK_THEME(1002), /** 用户个体数据 */
    PERSONAL(1003),

    //--------------------------------------//
    // 基金相关的 2000~2999         //
    //--------------------------------------//
    /** 基金的类型值 */
    FUND(2000), /** 基金经理的类型值 */
    FUND_MANAGER(2001), /** 基金公告的类型值 */
    FUND_INFO_ANNOUNCEMENT(2002),

    //-------------------//
    // 运营活动 3000~3999 //
    /** 猜涨跌 */
    GUESS_TREND(3000),
    //-------------------//

    //---------------------------------------//
    // 观点和回复都设置为topicType, 即都为支持的类型
    /** 观点类型 */
    COMMENT(9001), /** 评论类型 */
    REPLY(9002),
    //--------------------------------------//

    //---------------------------------------//
    // 达客 4000~4099-------------------------//
    /** 达客 */
    DAKE(4000),
    //--------------------------------------//
    ;

    private int _value;

    private TOPIC_TYPE(int value) {
        this._value = value;
    }

    public int value() {
        return _value;
    }

    public static TOPIC_TYPE valueOf(int value) {
        switch (value) {
            //----------------------------------//
            // 资讯相关的值从0-999                  //
            //----------------------------------//
            case 0:
                return INFO_TYPE_NEWS;
            case 1:
                return INFO_TYPE_ANNOUNCEMENT;
            case 2:
                return INFO_TYPE_RESEARCH_REPORT;

            //--------------------------------------//
            // 股吧，概念吧， XXX吧相关的 1000~1999         //
            //--------------------------------------//
            case 1000:
                return STOCK;
            case 1001:
                return CONCEPT;
            case 1002:
                return TALK_THEME;
            case 1003:
                return PERSONAL;

            //--------------------------------------//
            // 基金相关的 2000~2999         //
            //--------------------------------------//
            case 2000:
                return FUND;
            case 2001:
                return FUND_MANAGER;
            case 2002:
                return FUND_INFO_ANNOUNCEMENT;

            //-------------------//
            // 运营活动 3000~3999 //
            //-------------------//
            case 3000:
                return GUESS_TREND;

            //---------------------------------------//
            // 观点和回复都设置为topicType, 即都为支持的类型
            //--------------------------------------//
            case 9001:
                return COMMENT;
            case 9002:
                return REPLY;

            //---------------------------------------//
            // 达客 4000~4099-------------------------//
            //--------------------------------------//
            case 4000:
                return DAKE;
            default:
                throw new RuntimeException("Invalid TOPIC_TYPE value with: " + value);

        }
    }
}
