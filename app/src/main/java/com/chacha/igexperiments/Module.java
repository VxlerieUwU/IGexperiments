package com.chacha.igexperiments;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Module implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        XposedPreferences.loadPreferences();
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedPreferences.loadPreferences();
        XposedPreferences.reloadPrefs();

        String className, methodName;
        if(XposedPreferences.getPrefs().getBoolean("useGithub", true))
            XposedBridge.log("(IGExperiments) Using class name from Github");
         else
            XposedBridge.log("(IGExperiments) Using class name from preferences");

         className = XposedPreferences.getPrefs().getString("className", "");
         methodName = XposedPreferences.getPrefs().getString("methodName", "");


        if(className.equals("")){
            XposedBridge.log("(IGExperiments) No class name found, using default");
            className = Utils.DEFAULT_CLASS_TO_HOOK; // Change this if you want to update the app
            methodName = Utils.DEFAULT_METHOD_TO_HOOK;
        }

        if(lpparam.packageName.equals(Utils.IG_PACKAGE_NAME)) {
            XposedBridge.log("(IGExperiments) Hooking class: " + className);
            XposedBridge.log("(IGExperiments) Hooking method: " + methodName);
            XposedHelpers.findAndHookMethod(className, lpparam.classLoader, methodName,
                    XposedHelpers.findClass("com.instagram.service.session.UserSession", lpparam.classLoader),
                    XC_MethodReplacement.returnConstant(true));
        }

        if(lpparam.packageName.equals(Utils.MY_PACKAGE_NAME)) {
            findAndHookMethod(Utils.MY_PACKAGE_NAME + ".MainActivity", lpparam.classLoader,
                    "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }
    }
}
