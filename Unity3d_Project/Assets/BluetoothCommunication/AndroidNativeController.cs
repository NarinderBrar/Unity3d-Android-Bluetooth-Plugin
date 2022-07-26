using UnityEngine;
using UnityEngine.UI;

public class AndroidNativeController : MonoBehaviour
{
    [Header("Debug")]
    public Text Status;
    private int emptyDataCount = 0;

    [Header("Data")]
    public int[] indexs = new int [] {0,2,4};
    public char[] dataFormate = new char[] { 'a','0','b','0','s','0','0','0','0'};

    char[] dataFormated = new char[9]{'A','0','B','0','S','0','0','0','0'};

    public void Start()
    {
        Status.text = "Starting Thread...";
        AndroidNative.StartThread();
    }

    public void onCallBackThread()
    {
        Status.text = "Thread Started";
        InvokeRepeating("callArduino", 1, 0.5f);
    }

    void callArduino()
    {
        AndroidNative.WriteValues("0");
    }

    public void stop()
    {
        CancelInvoke("callArduino");
    }

    public void CallBackValues(string data)
    {
            char[] dataArr = data.ToCharArray();
            int dataLength = dataArr.Length;

            for (int i = 0; i < dataLength; i++)
            {
                for (int j = 0; j < indexs.Length; j++)
                {
                    if (dataArr[i] == dataFormate[indexs[j]])
                    {
                        if (dataArr[i] == 'a')
                        {
                            if (i + 1 < dataLength)
                            {
                                dataFormated[1] = dataArr[i + 1];
                            }
                        }
                        else if (dataArr[i] == 'b')
                        {
                            if (i + 1 < dataLength)
                            {
                                dataFormated[3] = dataArr[i + 1];
                            }

                            if (i - 1 > -1)
                            {
                                dataFormated[1] = dataArr[i - 1];
                            }
                        }
                        else if (dataArr[i] == 's')
                        {
                            if (i + 4 < dataLength)
                            {
                                dataFormated[6] = dataArr[i + 1];
                                dataFormated[7] = dataArr[i + 2];
                                dataFormated[8] = dataArr[i + 3];
                                dataFormated[9] = dataArr[i + 4];
                            }

                            if (i - 1 > -1)
                            {
                                dataFormated[3] = dataArr[i - 1];
                            }
                    }
                    }
                }
            }

            Status.text = new string(dataFormated);

            if (data.Length != 0)
            {
                emptyDataCount = 0;
            }
            else
            {
                emptyDataCount++;
                Status.text = "Received " + emptyDataCount + " EmptyData";
            }

    }
    public void Exit()
    {
        Application.Quit();
    }
}
