using UnityEngine;
using System.Collections;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class BluetoothConnector : MonoBehaviour
{
	public Text Status;

	private float loadingProgress;

    public void Initialize()
	{
        AndroidNative.Initialize();
    }

    public void onCallBackBluetoothInfo(string msg)
    {
        Status.text = msg;
        Connect();
    }

    public void Connect()
	{
        AndroidNative.Connect();
	}

    public void onCallBackConnecting(string msg)
    {
        Status.text = "Connecting...";
        Invoke("AttemptToConnect", 5);
    }

    void AttemptToConnect()
    {
        Status.text = "Failed to connect! Try again";
        AndroidNative.CheckConnection();
    }

    public void onCallBackConnected(string msg)
	{
		Status.text = msg;

        if (msg == "Connected")
        {
            Status.text = "Loading ....";
            CancelInvoke("CheckConnection");
            StartCoroutine(LoadScene());
        }
	}

	IEnumerator LoadScene()
	{
		yield return new WaitForSeconds(2);

		yield return null;
		AsyncOperation asyncOperation = SceneManager.LoadSceneAsync(1);

		while (!asyncOperation.isDone)
		{
            Status.text = "Loading progress: " + (asyncOperation.progress * 100) + "%";
			yield return null;
		}
	}

    public void Exit()
    {
        Application.Quit();
    }
}
