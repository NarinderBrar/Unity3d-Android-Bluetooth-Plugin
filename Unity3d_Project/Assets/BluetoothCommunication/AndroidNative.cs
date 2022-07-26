using UnityEngine;

public static class AndroidNative
{
    public static void CallNativePlugin(string methodName, params object[] args)
    {
        AndroidJavaClass unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

        AndroidJavaObject unityActivity = unityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity");
        AndroidJavaObject bridge = new AndroidJavaObject("com.chitkara.mynativemodule.Bridge");

        bridge.CallStatic(methodName, args);
    }

	public static void Initialize()
	{
        CallNativePlugin("Initialize");
    }

	public static void Connect()
	{
		CallNativePlugin("Connect");
	}

    public static void CheckConnection()
    {
        CallNativePlugin("isConnected");
    }

    public static void StartThread()
    {
        CallNativePlugin("startThread");
    }

    public static void WriteValues(string value)
	{
		CallNativePlugin("WriteValues", value);
	}

    public static void PrintString()
    {
        object[] parameters = new object[2];
        parameters[0] = "unityActivity";
        parameters[1] = "This is an call to native android!";

        CallNativePlugin("PrintString", parameters);
    }
}