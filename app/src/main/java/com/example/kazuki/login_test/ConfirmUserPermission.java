package com.example.kazuki.login_test;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Created by kazuki on 2016/07/14.
 */
public class ConfirmUserPermission {
    private final static String LOG_TAG = ConfirmUserPermission.class.getSimpleName();

    private static final String FUNCTION_NAME = "requiredauthority";
    private static final Charset CHARSET_UTF8 =
            Charset.forName("UTF-8");
    private static final CharsetEncoder ENCODER = CHARSET_UTF8.newEncoder();
    private static final CharsetDecoder DECODER = CHARSET_UTF8.newDecoder();
    private static final String DEFAULT_REQUEST_CONTENTS = "{\n  \"key1\" : \"%s\"\n}";
    private final Context mContext;
    private final ConfirmUserPermissionListener mListener;

    private String mUserName;

    public interface ConfirmUserPermissionListener
    {
        void OnConfirmUserPermission(String userPermission);
    }

    public ConfirmUserPermission(String userName, Context context , ConfirmUserPermissionListener listener) {
        mUserName = userName;
        mContext = context;
        mListener = listener;
    }

    public void invokeFunction() {
        final String requestPayload = String.format(DEFAULT_REQUEST_CONTENTS, mUserName);
        new AsyncTask<Void, Void, InvokeResult>() {
            @Override
            protected InvokeResult doInBackground(Void... params) {
                try {
//                RequestClass requestClass = new RequestClass(requestPayload);
                    final ByteBuffer payload =
                            ENCODER.encode(CharBuffer.wrap(requestPayload));

                    final InvokeRequest invokeRequest =
                            new InvokeRequest()
                                    .withFunctionName(FUNCTION_NAME)
                                    .withInvocationType(InvocationType.RequestResponse)
                                    .withPayload(payload);
                    CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
                            mContext, AWSConfiguration.identityPoolId, AWSConfiguration.AMAZON_COGNITO_REGION);

                    ClientConfiguration clientConfiguration = new ClientConfiguration();
                    AWSLambdaClient awsLambdaClient = new AWSLambdaClient(cognitoProvider, clientConfiguration);
                    awsLambdaClient.setRegion(Region.getRegion(AWSConfiguration.AMAZON_COGNITO_REGION));

                    final InvokeResult invokeResult = awsLambdaClient.invoke(invokeRequest);

                    return invokeResult;

                } catch (final Exception e) {
                    Log.e(LOG_TAG, "AWS Lambda invocation failed : " + e.getMessage(), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final InvokeResult invokeResult) {
                String resultPayload = null;
                try {
                    final int statusCode = invokeResult.getStatusCode();
                    final String functionError = invokeResult.getFunctionError();
                    final String logResult = invokeResult.getLogResult();

                    if (statusCode != 200) {
                        Log.e(LOG_TAG, "AWS Lambda Function Error: " + invokeResult.getFunctionError());
                    } else {
                        final ByteBuffer resultPayloadBuffer = invokeResult.getPayload();
                        resultPayload = DECODER.decode(resultPayloadBuffer).toString();
                    }

                    if (functionError != null) {
                        Log.e(LOG_TAG, "AWS Lambda Function Error: " + functionError);
                    }

                    if (logResult != null) {
                        Log.d(LOG_TAG, "AWS Lambda Log Result: " + logResult);
                    }
//                    resultPayload = resultClass.getAuthority();
                } catch (final Exception e) {
                    Log.e(LOG_TAG, "Unable to decode results. " + e.getMessage(), e);
                } finally {
                    mListener.OnConfirmUserPermission(resultPayload);
                }
            }
        }.execute();
    }
}



