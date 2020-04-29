package com.example.myapplication;

import android.util.Log;

/**
 * 日志工具
 */
public class Logger {

    public static void v(String msg){
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        StringBuffer toStringBuffer = new StringBuffer("[").append(
                traceElement.getFileName()).append(" | ").append(
                traceElement.getLineNumber()).append(" | ").append(
                traceElement.getMethodName()).append("()").append("]");
        String TAG = toStringBuffer.toString();
        Log.v(TAG,msg);

    }

    public static void d(String msg){
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        StringBuffer toStringBuffer = new StringBuffer("[").append(
                traceElement.getFileName()).append(" | ").append(
                traceElement.getLineNumber()).append(" | ").append(
                traceElement.getMethodName()).append("()").append("]");
        String TAG = toStringBuffer.toString();
        Log.d(TAG,msg);

    }
    public static void w(String msg){
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        StringBuffer toStringBuffer = new StringBuffer("[").append(
                traceElement.getFileName()).append(" | ").append(
                traceElement.getLineNumber()).append(" | ").append(
                traceElement.getMethodName()).append("()").append("]");
        String TAG = toStringBuffer.toString();
        Log.w(TAG,msg);

    }

    public static void e(String msg){
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        StringBuffer toStringBuffer = new StringBuffer("[").append(
                traceElement.getFileName()).append(" | ").append(
                traceElement.getLineNumber()).append(" | ").append(
                traceElement.getMethodName()).append("()").append("]");
        String TAG = toStringBuffer.toString();
        Log.e(TAG,msg);
    }

    public static void i(String msg) {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        StringBuffer toStringBuffer = new StringBuffer("[").append(
                traceElement.getFileName()).append(" | ").append(
                traceElement.getLineNumber()).append(" | ").append(
                traceElement.getMethodName()).append("()").append("]");
        String TAG = toStringBuffer.toString();
        Log.i( TAG,msg);
    }






}
