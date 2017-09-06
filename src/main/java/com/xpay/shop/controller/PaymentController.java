package com.xpay.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.xpay.shop.service.XPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能说明:聚合支付控制类
 *
 * @author dwei
 */
@Controller
@RequestMapping(value = "/payment")
public class PaymentController {

    @Autowired
    XPayService XPayService;

    @RequestMapping("/api_payment")
    @ResponseBody
    public JSONObject api_payment(HttpServletRequest request) throws Exception {
        String reqsn = String.valueOf(System.currentTimeMillis());
        return XPayService.pay(1, reqsn, "A01", "标题", "备注", "", "123", "http://baidu.com", "");
    }

}
