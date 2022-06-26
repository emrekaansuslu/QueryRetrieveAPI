package com.icterra.midas.ae.queryretrieve.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author arda.fakili
 * @date 28.04.2020
 */
public class ServiceExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger("DicomDAO");
    private static Class aClass;

    @Override
    public void uncaughtException(Thread t, Throwable e) {

//        LOGGER.error("Unhandled Exception has occured in thread " + t.getName(), e.toString());
        LOGGER.error("Unhandled Exception : " + e.toString());
        System.out.println("Exception has occured, " + aClass.getName() + " is rebooting. Exception: " + e.toString());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        new Thread(() -> {
            try {
                Method meth = aClass.getMethod("main", String[].class);
                String[] params = null; // init params accordingly
                meth.invoke(null, (Object) params);
            } catch (NoSuchMethodException | IllegalAccessException exception) {
                exception.printStackTrace();
            } catch (InvocationTargetException invocationTargetException) {
                uncaughtException(Thread.currentThread(), invocationTargetException);
            }
        }).start();

//        Never use it
//        System.gc();

    }

    public void registerUncaughtExceptionHandler(Class aClass) {
        this.aClass = aClass;
        Thread.setDefaultUncaughtExceptionHandler(new ServiceExceptionHandler());
    }
}
