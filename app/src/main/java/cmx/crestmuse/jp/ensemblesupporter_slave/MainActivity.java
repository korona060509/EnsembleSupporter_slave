package cmx.crestmuse.jp.ensemblesupporter_slave;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ScrollView;

import java.util.Set;



public class MainActivity extends Activity implements SensorEventListener {

    public boolean flag = true;
    //region　環境変数一覧
    //View関連の変数
    ScrollView scrollView;
    ScrollView scrollView2;
    public int statusbar_size = 0;
    public int winH = 0;
    public boolean currentView = true; //true = view1, false = view2

    //センサー値関連の変数
    public double angle = 0;


    //Bluetooth関連の変数
    private BluetoothTask bluetoothTask = new BluetoothTask(this);
    public BluetoothDevice device;



    private int OnNote_pointer1;
    private int OnNote_pointer2;

    private boolean threadflag = false;
    //手のポインタID保存用
    private int mPointerID1, mPointerID2;

    //soundpool関連
    private SoundPool soundPool;
    private int[] soundIDList = new int[21];
    public SparseIntArray number2source = new SparseIntArray();

    //クラスインスタンスの生成
    private CodeData_Read codeData_read = new CodeData_Read();
    private OrientationEstimater orientationEstimater = new OrientationEstimater();
    private CodeChange codeChange = new CodeChange();
    //endregion

    //region onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        for (int j = 0; j < 9; j++) {
                codeChange.MidiNumber[j] = 60 + j*2 ;
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        winH = dm.heightPixels;
        Log.i("MainActivity : ", "画面高さ = " + winH);
        scrollView = (ScrollView) findViewById(R.id.view2);

        codeChange.time2note = codeData_read.setFile("ishikawa_chord_midi.csv", this);
        codeChange.TimeTable = codeData_read.TimeTable;
        //codeChange.codeset();

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                angle = (80 + orientationEstimater.fusedOrientation[1] * 180 / Math.PI);
                if (angle > 160)
                    angle = 160;
                else if (angle < 0)
                    angle = 0;

                int position = 0;
                if(currentView) {
                    position = (int) ((2916 - winH + statusbar_size) * angle / 160);
                    //scrollView = (ScrollView) findViewById(R.id.view);
                    scrollView.smoothScrollTo(0, position);
                    //Log.d("test","test");
                }else {
                    position = (int) ((3888 - winH + statusbar_size) * angle / 160);
                    //scrollView2 = (ScrollView) findViewById(R.id.view2);
                    scrollView.smoothScrollTo(0,position);
                    Log.d("test","test2");
                }

                handler.postDelayed(this, 25);



            }
        });

    }//endregion

    //システム起動に関するイベントハンドラ部分
//region システム起動時や終了時に起動するイベントハンドラ
    @SuppressWarnings("deprecation") //非推奨APIに関する警告無視
    @Override
    protected void onResume() {
        super.onResume();
        /*
        // Bluetooth初期化
        bluetoothTask.init();
        // ペアリング済みデバイスの一覧を表示してユーザに選ばせる。
        showDialog(DEVICES_DIALOG);
        */
        soundPool = new SoundPool(36, AudioManager.STREAM_MUSIC,0);
        number2source.put(60,(soundPool.load(getApplicationContext(),R.raw.piano48,0)));
        number2source.put(62,(soundPool.load(getApplicationContext(),R.raw.piano49,0)));
        number2source.put(64,(soundPool.load(getApplicationContext(),R.raw.piano50,0)));
        number2source.put(66,(soundPool.load(getApplicationContext(),R.raw.piano51,0)));
        number2source.put(68,(soundPool.load(getApplicationContext(),R.raw.piano52,0)));
        number2source.put(70,(soundPool.load(getApplicationContext(),R.raw.piano53,0)));
        number2source.put(72,(soundPool.load(getApplicationContext(),R.raw.piano54,0)));
        number2source.put(74,(soundPool.load(getApplicationContext(),R.raw.piano55,0)));
        number2source.put(76,(soundPool.load(getApplicationContext(),R.raw.piano56,0)));
        number2source.put(78,(soundPool.load(getApplicationContext(),R.raw.piano57,0)));
        number2source.put(60,(soundPool.load(getApplicationContext(),R.raw.piano58,0)));
        number2source.put(62,(soundPool.load(getApplicationContext(),R.raw.piano59,0)));
        number2source.put(64,(soundPool.load(getApplicationContext(),R.raw.piano60,0)));
        number2source.put(66,(soundPool.load(getApplicationContext(),R.raw.piano61,0)));
        number2source.put(68,(soundPool.load(getApplicationContext(),R.raw.piano62,0)));
        number2source.put(70,(soundPool.load(getApplicationContext(),R.raw.piano63,0)));
        number2source.put(72,(soundPool.load(getApplicationContext(),R.raw.piano64,0)));
        number2source.put(74,(soundPool.load(getApplicationContext(),R.raw.piano65,0)));
        number2source.put(76,(soundPool.load(getApplicationContext(),R.raw.piano66,0)));
        number2source.put(78,(soundPool.load(getApplicationContext(),R.raw.piano67,0)));
        number2source.put(60,(soundPool.load(getApplicationContext(),R.raw.piano68,0)));
        number2source.put(62,(soundPool.load(getApplicationContext(),R.raw.piano69,0)));
        number2source.put(64,(soundPool.load(getApplicationContext(),R.raw.piano70,0)));
        number2source.put(66,(soundPool.load(getApplicationContext(),R.raw.piano71,0)));
        number2source.put(68,(soundPool.load(getApplicationContext(),R.raw.piano72,0)));
        number2source.put(70,(soundPool.load(getApplicationContext(),R.raw.piano73,0)));
        number2source.put(72,(soundPool.load(getApplicationContext(),R.raw.piano74,0)));
        number2source.put(74,(soundPool.load(getApplicationContext(),R.raw.piano75,0)));
        number2source.put(76,(soundPool.load(getApplicationContext(),R.raw.piano76,0)));
        number2source.put(78,(soundPool.load(getApplicationContext(),R.raw.piano77,0)));


    }

    @Override
    protected void onStart() {
        super.onStart();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensorAccelLinear = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //Sensor sensorPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        Sensor sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor sensorRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        /*
        Sensor sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        */
        sensorManager.registerListener(this, sensorAccelLinear, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorGyro, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_FASTEST);
        //sensorManager.registerListener(this, sensorPressure, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorRotation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        orientationEstimater.onSensorEvent(event);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onWindowFocusChanged( boolean hasFocus ) {
        super.onWindowFocusChanged(hasFocus);
        final Rect rect = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        statusbar_size = rect.top;
        Log.d("test","statusbar="+rect.top);

    }//endregion

//region 画面タッチイベントハンドラ
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int eventAction = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        float y = event.getY(pointerIndex) - statusbar_size;
        int scvalue = scrollView.getScrollY();
        float totalY = scvalue + y;


        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                Log.d("test", "ACTION_DOWN" + totalY);
                mPointerID1 = pointerId;
                mPointerID2 = -1;
                //region コード構成音が3つの場合
                if (currentView) {//コード構成音が3つの時
                    if (totalY >= 0 && totalY <= 324 * 1) {
                        Log.d("test", "1");
                        playFromSoundPool(codeChange.MidiNumber[8]);
                    } else if (totalY > 324 * 1 && totalY <= 324 * 2) {
                        Log.d("test", "2");
                        playFromSoundPool(codeChange.MidiNumber[7]);
                    } else if (totalY > 324 * 2 && totalY <= 324 * 3) {
                        Log.d("test", "3");
                        playFromSoundPool(codeChange.MidiNumber[6]);
                    } else if (totalY > 324 * 3 && totalY <= 324 * 4) {
                        Log.d("test", "4");
                        playFromSoundPool(codeChange.MidiNumber[5]);
                    } else if (totalY > 324 * 4 && totalY <= 324 * 5) {
                        Log.d("test", "5");
                        playFromSoundPool(codeChange.MidiNumber[4]);
                    } else if (totalY > 324 * 5 && totalY <= 324 * 6) {
                        Log.d("test", "6");
                        playFromSoundPool(codeChange.MidiNumber[3]);
                    } else if (totalY > 324 * 6 && totalY <= 324 * 7) {
                        Log.d("test", "7");
                        playFromSoundPool(codeChange.MidiNumber[2]);
                    } else if (totalY > 324 * 7 && totalY <= 324 * 8) {
                        Log.d("test", "8");
                        playFromSoundPool(codeChange.MidiNumber[1]);
                    } else if (totalY > 324 * 8 && totalY <= 324 * 9) {
                        Log.d("test", "9");
                        playFromSoundPool(codeChange.MidiNumber[0]);
                    }
                } //endregion
                //region コード構成音が4つの場合
                else if (!currentView) {
                    if (totalY >= 0 && totalY <= 324 * 1) {
                        Log.d("test", "1");
                        playFromSoundPool(codeChange.MidiNumber[11]);
                    } else if (totalY > 324 * 1 && totalY <= 324 * 2) {
                        Log.d("test", "2");
                        playFromSoundPool(codeChange.MidiNumber[10]);
                    } else if (totalY > 324 * 2 && totalY <= 324 * 3) {
                        Log.d("test", "3");
                        playFromSoundPool(codeChange.MidiNumber[9]);
                    } else if (totalY > 324 * 3 && totalY <= 324 * 4) {
                        Log.d("test", "4");
                        playFromSoundPool(codeChange.MidiNumber[8]);
                    } else if (totalY > 324 * 4 && totalY <= 324 * 5) {
                        Log.d("test", "5");
                        playFromSoundPool(codeChange.MidiNumber[7]);
                    } else if (totalY > 324 * 5 && totalY <= 324 * 6) {
                        Log.d("test", "6");
                        playFromSoundPool(codeChange.MidiNumber[6]);
                    } else if (totalY > 324 * 6 && totalY <= 324 * 7) {
                        Log.d("test", "7");
                        playFromSoundPool(codeChange.MidiNumber[5]);
                    } else if (totalY > 324 * 7 && totalY <= 324 * 8) {
                        Log.d("test", "8");
                        playFromSoundPool(codeChange.MidiNumber[4]);
                    } else if (totalY > 324 * 8 && totalY <= 324 * 9) {
                        Log.d("test", "9");
                        playFromSoundPool(codeChange.MidiNumber[3]);
                    } else if (totalY > 324 * 9 && totalY <= 324 * 10) {
                        Log.d("test", "10");
                        playFromSoundPool(codeChange.MidiNumber[2]);
                    } else if (totalY > 324 * 10 && totalY <= 324 * 11) {
                        Log.d("test", "11");
                        playFromSoundPool(codeChange.MidiNumber[1]);
                    } else if (totalY > 324 * 11 && totalY <= 324 * 12) {
                        Log.d("test", "12");
                        playFromSoundPool(codeChange.MidiNumber[0]);
                    }
                }//endregion
                break;


            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("test", "ACTION_POINTER_DOWN");
                if (mPointerID2 == -1) {
                    mPointerID2 = pointerId;
                    if (currentView) {//コード構成音が3つの時
                        if (totalY >= 0 && totalY <= 324 * 1) {
                            Log.d("test", "1");
                            playFromSoundPool(codeChange.MidiNumber[8]);
                        } else if (totalY > 324 * 1 && totalY <= 324 * 2) {
                            Log.d("test", "2");
                            playFromSoundPool(codeChange.MidiNumber[7]);
                        } else if (totalY > 324 * 2 && totalY <= 324 * 3) {
                            Log.d("test", "3");
                            playFromSoundPool(codeChange.MidiNumber[6]);
                        } else if (totalY > 324 * 3 && totalY <= 324 * 4) {
                            Log.d("test", "4");
                            playFromSoundPool(codeChange.MidiNumber[5]);
                        } else if (totalY > 324 * 4 && totalY <= 324 * 5) {
                            Log.d("test", "5");
                            playFromSoundPool(codeChange.MidiNumber[4]);
                        } else if (totalY > 324 * 5 && totalY <= 324 * 6) {
                            Log.d("test", "6");
                            playFromSoundPool(codeChange.MidiNumber[3]);
                        } else if (totalY > 324 * 6 && totalY <= 324 * 7) {
                            Log.d("test", "7");
                            playFromSoundPool(codeChange.MidiNumber[2]);
                        } else if (totalY > 324 * 7 && totalY <= 324 * 8) {
                            Log.d("test", "8");
                            playFromSoundPool(codeChange.MidiNumber[1]);
                        } else if (totalY > 324 * 8 && totalY <= 324 * 9) {
                            Log.d("test", "9");
                            playFromSoundPool(codeChange.MidiNumber[0]);
                        }
                    } //endregion
                    //region コード構成音が4つの場合
                    else if (!currentView) {
                        if (totalY >= 0 && totalY <= 324 * 1) {
                            Log.d("test", "1");
                            playFromSoundPool(codeChange.MidiNumber[11]);
                        } else if (totalY > 324 * 1 && totalY <= 324 * 2) {
                            Log.d("test", "2");
                            playFromSoundPool(codeChange.MidiNumber[10]);
                        } else if (totalY > 324 * 2 && totalY <= 324 * 3) {
                            Log.d("test", "3");
                            playFromSoundPool(codeChange.MidiNumber[9]);
                        } else if (totalY > 324 * 3 && totalY <= 324 * 4) {
                            Log.d("test", "4");
                            playFromSoundPool(codeChange.MidiNumber[8]);
                        } else if (totalY > 324 * 4 && totalY <= 324 * 5) {
                            Log.d("test", "5");
                            playFromSoundPool(codeChange.MidiNumber[7]);
                        } else if (totalY > 324 * 5 && totalY <= 324 * 6) {
                            Log.d("test", "6");
                            playFromSoundPool(codeChange.MidiNumber[6]);
                        } else if (totalY > 324 * 6 && totalY <= 324 * 7) {
                            Log.d("test", "7");
                            playFromSoundPool(codeChange.MidiNumber[5]);
                        } else if (totalY > 324 * 7 && totalY <= 324 * 8) {
                            Log.d("test", "8");
                            playFromSoundPool(codeChange.MidiNumber[4]);
                        } else if (totalY > 324 * 8 && totalY <= 324 * 9) {
                            Log.d("test", "9");
                            playFromSoundPool(codeChange.MidiNumber[3]);
                        } else if (totalY > 324 * 9 && totalY <= 324 * 10) {
                            Log.d("test", "10");
                            playFromSoundPool(codeChange.MidiNumber[2]);
                        } else if (totalY > 324 * 10 && totalY <= 324 * 11) {
                            Log.d("test", "11");
                            playFromSoundPool(codeChange.MidiNumber[1]);
                        } else if (totalY > 324 * 11 && totalY <= 324 * 12) {
                            Log.d("test", "12");
                            playFromSoundPool(codeChange.MidiNumber[0]);
                        }
                    }//endregion

                } else if (mPointerID1 == -1) {
                    mPointerID1 = pointerId;
                    if (currentView) {//コード構成音が3つの時
                        if (totalY >= 0 && totalY <= 324 * 1) {
                            Log.d("test", "1");
                            playFromSoundPool(codeChange.MidiNumber[8]);
                        } else if (totalY > 324 * 1 && totalY <= 324 * 2) {
                            Log.d("test", "2");
                            playFromSoundPool(codeChange.MidiNumber[7]);
                        } else if (totalY > 324 * 2 && totalY <= 324 * 3) {
                            Log.d("test", "3");
                            playFromSoundPool(codeChange.MidiNumber[6]);
                        } else if (totalY > 324 * 3 && totalY <= 324 * 4) {
                            Log.d("test", "4");
                            playFromSoundPool(codeChange.MidiNumber[5]);
                        } else if (totalY > 324 * 4 && totalY <= 324 * 5) {
                            Log.d("test", "5");
                            playFromSoundPool(codeChange.MidiNumber[4]);
                        } else if (totalY > 324 * 5 && totalY <= 324 * 6) {
                            Log.d("test", "6");
                            playFromSoundPool(codeChange.MidiNumber[3]);
                        } else if (totalY > 324 * 6 && totalY <= 324 * 7) {
                            Log.d("test", "7");
                            playFromSoundPool(codeChange.MidiNumber[2]);
                        } else if (totalY > 324 * 7 && totalY <= 324 * 8) {
                            Log.d("test", "8");
                            playFromSoundPool(codeChange.MidiNumber[1]);
                        } else if (totalY > 324 * 8 && totalY <= 324 * 9) {
                            Log.d("test", "9");
                            playFromSoundPool(codeChange.MidiNumber[0]);
                        }
                    } //endregion
                    //region コード構成音が4つの場合
                    else if (!currentView) {
                        if (totalY >= 0 && totalY <= 324 * 1) {
                            Log.d("test", "1");
                            playFromSoundPool(codeChange.MidiNumber[11]);
                        } else if (totalY > 324 * 1 && totalY <= 324 * 2) {
                            Log.d("test", "2");
                            playFromSoundPool(codeChange.MidiNumber[10]);
                        } else if (totalY > 324 * 2 && totalY <= 324 * 3) {
                            Log.d("test", "3");
                            playFromSoundPool(codeChange.MidiNumber[9]);
                        } else if (totalY > 324 * 3 && totalY <= 324 * 4) {
                            Log.d("test", "4");
                            playFromSoundPool(codeChange.MidiNumber[8]);
                        } else if (totalY > 324 * 4 && totalY <= 324 * 5) {
                            Log.d("test", "5");
                            playFromSoundPool(codeChange.MidiNumber[7]);
                        } else if (totalY > 324 * 5 && totalY <= 324 * 6) {
                            Log.d("test", "6");
                            playFromSoundPool(codeChange.MidiNumber[6]);
                        } else if (totalY > 324 * 6 && totalY <= 324 * 7) {
                            Log.d("test", "7");
                            playFromSoundPool(codeChange.MidiNumber[5]);
                        } else if (totalY > 324 * 7 && totalY <= 324 * 8) {
                            Log.d("test", "8");
                            playFromSoundPool(codeChange.MidiNumber[4]);
                        } else if (totalY > 324 * 8 && totalY <= 324 * 9) {
                            Log.d("test", "9");
                            playFromSoundPool(codeChange.MidiNumber[3]);
                        } else if (totalY > 324 * 9 && totalY <= 324 * 10) {
                            Log.d("test", "10");
                            playFromSoundPool(codeChange.MidiNumber[2]);
                        } else if (totalY > 324 * 10 && totalY <= 324 * 11) {
                            Log.d("test", "11");
                            playFromSoundPool(codeChange.MidiNumber[1]);
                        } else if (totalY > 324 * 11 && totalY <= 324 * 12) {
                            Log.d("test", "12");
                            playFromSoundPool(codeChange.MidiNumber[0]);
                        }
                    }
                }
                break;
        }//endregion
        return super.onTouchEvent(event);
    }

    //ダイアログ関連部分
    //region ダイアログ
    //ダイアログ関連の変数
    private final static int DEVICES_DIALOG = 1;
    private final static int ERROR_DIALOG = 2;
    private ProgressDialog waitDialog;
    private String errorMessage = "";
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DEVICES_DIALOG) return createDevicesDialog();
        if (id == ERROR_DIALOG) return createErrorDialog();
        return null;
    }
    @SuppressWarnings("deprecation")
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        if (id == ERROR_DIALOG) {
            ((AlertDialog) dialog).setMessage(errorMessage);
        }
        super.onPrepareDialog(id, dialog);
    }

    public Dialog createDevicesDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Select device");

        // ペアリング済みデバイスをダイアログのリストに設定する。
        Set<BluetoothDevice> pairedDevices = bluetoothTask.getPairedDevices();
        final BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);
        String[] items = new String[devices.length];
        for (int i=0;i<devices.length;i++) {
            items[i] = devices[i].getName();
        }

        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 選択されたデバイスを通知する。そのまま接続開始。
                bluetoothTask.Connect(devices[which],codeChange);
            }
        });
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder.create();
    }

    @SuppressWarnings("deprecation")
    public void errorDialog(String msg) {
        if (this.isFinishing()) return;
        this.errorMessage = msg;
        this.showDialog(ERROR_DIALOG);
    }
    public Dialog createErrorDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder.setMessage("");
        alertDialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        return alertDialogBuilder.create();
    }
    //サーバとの接続待ち状態に表示するダイアログ
    public void showWaitDialog(String msg) {
        if (waitDialog == null) {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage(msg);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.show();
    }
    public void hideWaitDialog() {
        waitDialog.dismiss();
    }
    //endregion

    public void playFromSoundPool(int i){
        //再生
        soundPool.play(number2source.get(i),1.0F,1.0F,0,0,1.0F);
    }
}





