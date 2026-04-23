package io.github.aicyi.test.domain;

import io.github.aicyi.commons.lang.BaseBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankExcel extends BaseBean {
    //id	bankEnAbbr	bankEnName	countryCode	serviceCountryCode	bankType	bankCode
    private String id;
    private String bankEnAbbr;
    private String bankEnName;
    private String countryCode;
    private String serviceCountryCode;
    private String bankType;
    private String bankCode;
}