package cmx.crestmuse.jp.ensemblesupporter_slave;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mizun on 2018/07/06.
 */

public class CodeChange {

    public List<Integer> TimeTable = new ArrayList<Integer>();
    public CodeChangeThread codeChangethread = new CodeChangeThread();
    private boolean threadflag = false;

    public SparseArray<List<Integer>> time2note = new SparseArray<List<Integer>>();

    public long StartTime;
    public long currentTime;
    public long nextTime;
    public int CodeIndex = 0;
    public List<Integer> codes;
    public int MidiNumber[] = new int[12];
    //出力音変更に関係するメソッド
    //region 出力する音を変更するためのメソッド
    public void codeset(){
        currentTime = TimeTable.get(CodeIndex);
        codes =  time2note.get(TimeTable.get(CodeIndex));

        if(codes.size() == 3) {
            for (int j = 0; j < 9; j++) {
                if (codes.size() > j)
                    MidiNumber[j] = codes.get(j) - 12; //-1オクターブ
                else if(codes.size() <= j && codes.size()*2 > j)
                    MidiNumber[j] = codes.get(j % codes.size()); //中間オクターブ
                else
                    MidiNumber[j] = codes.get(j % codes.size()) + 12; //+1オクターブ
            }
            /*
            if(!currentView) {
                // 変更したいレイアウトを取得する
                ScrollView layout = (ScrollView) findViewById(R.id.view2);
                // レイアウトのビューをすべて削除する
                layout.removeAllViews();
                // レイアウトをR.layout.sampleに変更する
                getLayoutInflater().inflate(R.layout.activity_main, layout);
                currentView = true;
            }*/
        } else if(codes.size() == 4){
            for (int j = 0; j < 12; j++) {
                if (codes.size() > j)
                    MidiNumber[j] = codes.get(j) - 12; //-1オクターブ
                else if(codes.size() <= j && codes.size()*2 > j)
                    MidiNumber[j] = codes.get(j % codes.size()); //中間オクターブ
                else
                    MidiNumber[j] = codes.get(j % codes.size()) + 12; //+1オクターブ
            }/*
            if(currentView) {
                // 変更したいレイアウトを取得する
                ScrollView layout = (ScrollView) findViewById(R.id.view);
                // レイアウトのビューをすべて削除する
                layout.removeAllViews();
                // レイアウトをR.layout.sampleに変更する
                getLayoutInflater().inflate(R.layout.activity_main2, layout);
            }
            */
        }
        CodeIndex++;
        if(TimeTable.size() > CodeIndex) {
            nextTime = TimeTable.get(CodeIndex);
            Log.d("test","nextTime="+nextTime);
        }
        else{
            CodeIndex = 0;
            Log.d("test","nextTime="+nextTime);
            try {
                Thread.sleep(235); //5ms早めに処理
                StartTime = System.currentTimeMillis();
                codeset();
            } catch(Exception e){

            }
        }

    }

    public void CodeChangeStart(){
        threadflag = true;
        StartTime = System.currentTimeMillis();
        codeChangethread.start();
    }

    public class CodeChangeThread extends Thread{
        public void run(){
            try{
                while(StartTime != 0 && threadflag) {
                    Thread.sleep(nextTime - (System.currentTimeMillis() - StartTime) - 5); //処理時間のことも踏まえて5ms早めに処理し始める
                    codeset();
                }
            }catch(Exception e){

            }
        }
    }
}
