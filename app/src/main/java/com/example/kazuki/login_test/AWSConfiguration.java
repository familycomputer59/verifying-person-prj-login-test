package com.example.kazuki.login_test;

import com.amazonaws.regions.Regions;

/**
 * Created by kazuki on 2016/07/14.
 */
public class AWSConfiguration {
    //////////////////////
    // Config Param
    //////////////////////
    public static final String USER_POOL_ID = "";// TODO: 2016/07/29 Must Change
    public static final String CLIENT_ID = "";// TODO: 2016/07/29 Must Change
    public static final String CLIENT_SECRET = "";// TODO: 2016/07/29 Must Change
    public static final String IDENTITY_POOL_ID = "";// TODO: 2016/07/29 Must Change
    public static final Regions AMAZON_COGNITO_REGION = Regions.US_EAST_1;
    // Note that spaces are not allowed in the table name
    public static final String TEST_TABLE_NAME = "";// TODO: 2016/07/29 Must Change

}
