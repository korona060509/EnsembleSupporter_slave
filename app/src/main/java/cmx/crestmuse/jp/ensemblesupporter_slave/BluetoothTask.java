package cmx.crestmuse.jp.ensemblesupporter_slave;

/**
 * Created by korona on 2017/04/11.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

public class BluetoothTask {
    private static final String TAG = "BluetoothTask";

    /**
     * UUIDはサーバと一致している必要がある。
     * - 独自サービスのUUIDはツールで生成する。（ほぼ乱数）
     * - 注：このまま使わないように。
     */
    String mUUID = "17fcf242-f86d-4e35-805e-";
    UUID APP_UUID;


    private MainActivity activity;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice = null;
    private BluetoothSocket bluetoothSocket;
    public InputStream btIn;
    public OutputStream btOut;

    private CodeData_Read cd = new CodeData_Read();
    private CodeChange codeChange;
    //public CodeChange.CodeChangeThread codeChangethread = new CodeChange.CodeChangeThread();


    public int sendflag = 0;
    public long currenttime = 0;
    public long starttime = 0;
    public int attacktiming_switch = 0;
    private String condition = "off";


    public BluetoothTask(MainActivity activity) {
        this.activity = activity;
    }

    /**
     * Bluetoothの初期化。
     */
    public void init() {
        // BTアダプタ取得。取れなければBT未実装デバイス。
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String address = getBluetoothMacAddress();
        address = address.replace(":", "");
        mUUID = mUUID + address;
        APP_UUID = UUID.fromString(mUUID);
        Log.d("test","UUID="+APP_UUID);
        if (bluetoothAdapter == null) {
            activity.errorDialog("This device is not implement Bluetooth.");
            return;
        }
        // BTが設定で有効になっているかチェック。
        if (!bluetoothAdapter.isEnabled()) {
            // TODO: ユーザに許可を求める処理。
            activity.errorDialog("This device is disabled Bluetooth.");
            return;
        }
    }
    /**
     * @return ペアリング済みのデバイス一覧を返す。デバイス選択ダイアログ用。
     */
    public Set<BluetoothDevice> getPairedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    /**
     * 非同期で指定されたデバイスの接続を開始する。
     * - 選択ダイアログから選択されたデバイスを設定される。
     * @param device 選択デバイス
     */
    public void Connect(BluetoothDevice device, CodeChange CDR) {
        bluetoothDevice = device;
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(APP_UUID);
            codeChange = CDR;
            new ConnectTask().execute();
        } catch (IOException e) {
            Log.e(TAG,e.toString(),e);
            activity.errorDialog(e.toString());
        }
    }

    public void signalsend(int times) {
        /*
        new SignalSend().execute("お試し");
        */
        try {

            byte[] buff = new byte[512];
            /*btOut.write("11111111".getBytes(StandardCharsets.UTF_8));
            */


            btOut.write("1".getBytes(StandardCharsets.UTF_8));
            //String str = new String(buff,0,n);
            //Log.d("test",""+str);



        } catch (Throwable t) {
            doClose();
        }
    }

    public void doSend() {
        new WriteTask().execute();
    }

    public void doRead() {
        new ReadTask().execute();
    }

    public void doStart() {
        new StartTask().execute();
    }
    /**
     * 非同期でBluetoothの接続を閉じる。
     */
    public void doClose() {
        new CloseTask().execute();
    }

    /**
     * AsyncTaskは非同期処理のための
     * Bluetoothと接続を開始する非同期タスク。
     * - 時間がかかる場合があるのでProcessDialogを表示する。
     * - 双方向のストリームを開くところまで。
     */
    private class ConnectTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected void onPreExecute() {
            activity.showWaitDialog("Connect Bluetooth Device.");
        }
//非同期処理の前に行われる処理
        @Override
        protected Object doInBackground(Void... params) {
            //非同期で処理を実行
            try {
                bluetoothSocket.connect();
                btIn = bluetoothSocket.getInputStream();
                btOut = bluetoothSocket.getOutputStream();
            } catch (Throwable t) {
                doClose();
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            //接続エラーが起きた場合エラーダイアログを表示
            doStart();
            if (result instanceof Throwable) {
                Log.e(TAG,result.toString(),(Throwable)result);
                activity.errorDialog(result.toString());
            } else {
                activity.hideWaitDialog();
            }
        }
    }

    /**
     * Bluetoothと接続を終了する非同期タスク。
     * - 不要かも知れないが念のため非同期にしている。
     */
    private class CloseTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                try{btOut.close();}catch(Throwable t){/*ignore*/}
                try{btIn.close();}catch(Throwable t){/*ignore*/}
                bluetoothSocket.close();
            } catch (Throwable t) {
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                Log.e(TAG,result.toString(),(Throwable)result);
                activity.errorDialog(result.toString());
            }
        }
    }



    private class WriteTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
                try {
                    Log.d("test","お試し");
                    String str = "ABC";
                    btOut.write(str.getBytes(StandardCharsets.UTF_8));
                    Log.d("test","メッセージ送信"+str);
                } catch (Throwable t) {
                    doClose();
                }
        }
    }

    public class ReadTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            try {
                /*
                //一回目の受信送信
                byte[] buff = new byte[512];
                int n = btIn.read(buff);
                String str = new String(buff, 0, n);
                btOut.write(str.getBytes(StandardCharsets.UTF_8));
                n = btIn.read(buff);
                */
                Log.d("test", "スタート成功");

                codeChange.CodeChangeStart();
                /*
                while(true) {
                    Log.d("test", "テスト");
                    buff = new byte[512];
                    n = btIn.read(buff);
                    str = new String(buff, 0, n);
                    btOut.write(str.getBytes(StandardCharsets.UTF_8));
                    Log.d("test", "" + str);
                    Log.d("test", "" + times);
                }
                */


            } catch (Throwable t) {
                doClose();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {

        }
    }
    public class StartTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            try {

                //一回目の受信送信
                byte[] buff = new byte[512];
                int n = btIn.read(buff);
                String str = new String(buff, 0, n);
                btOut.write(str.getBytes(StandardCharsets.UTF_8));
                n = btIn.read(buff);

            } catch (Throwable t) {
                doClose();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            Log.d("test", "スタート成功"+codeChange.nextTime);
            codeChange.CodeChangeStart();
        }
    }

    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
                mServiceField.setAccessible(true);

                Object btManagerService = mServiceField.get(bluetoothAdapter);

                if (btManagerService != null) {
                    bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
                }
            } catch (NoSuchFieldException e) {

            } catch (NoSuchMethodException e) {

            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            }
        } else {
            bluetoothMacAddress = bluetoothAdapter.getAddress();
        }
        return bluetoothMacAddress;
    }



}

