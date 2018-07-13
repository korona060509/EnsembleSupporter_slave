package cmx.crestmuse.jp.ensemblesupporter_slave;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by mizun on 2018/06/11.
 */

public class CodeData_Read {
    private List<Integer> codes;
    public List<Integer> TimeTable = new ArrayList<Integer>();
    public SparseArray<List<Integer>> setFile(String file_name, Context context){
        //ファイル名に指定するための実験日時を取得
        Date date = new Date();

        //csvファイルに保存したコード進行データを配列に格納
        SparseArray<List<Integer>> time2note = new SparseArray<List<Integer>>();

        AssetManager assetManager = context.getResources().getAssets();
        try {
            InputStream is = assetManager.open(file_name);
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line = "";
            String txt = "";
            int time = 0;
            int i = 0;
            while ((line = bufferReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                codes = new ArrayList<Integer>();
                while (st.hasMoreTokens()) {
                    if (i == 0) {
                        time = Integer.parseInt(st.nextToken());
                        i++;
                        Log.d("test","time="+time+",i="+i);
                    } else {
                        codes.add(Integer.parseInt(st.nextToken()));
                        i++;
                        Log.d("test","codes="+codes);
                    }
                }
                TimeTable.add(time);
                time2note.put(time, codes);

                i = 0;
            }

        } catch (IOException e) {
        }
        //Log.d("test2","お試し"+TimeTable.size());
        return(time2note);
    }
    /*多分消していい部分
    public HashMap<Integer,List<Integer>> setFile(String file_name, Context context) {

        //ファイル名に指定するための実験日時を取得
        Date date = new Date();

        //csvファイルに保存したコード進行データを配列に格納
        HashMap<Integer, List<Integer>> time2note = new HashMap<Integer, List<Integer>>();

        AssetManager assetManager = context.getResources().getAssets();
        try {
            InputStream is = assetManager.open(file_name);
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line = "";
            String txt = "";
            int time = 0;
            int i = 0;
            while ((line = bufferReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                codes = new ArrayList<Integer>();
                while (st.hasMoreTokens()) {
                    if (i == 0) {
                        time = Integer.parseInt(st.nextToken());
                        i++;
                        //Log.d("test","time="+time+",i="+i);
                    } else {
                        codes.add(Integer.parseInt(st.nextToken()));
                        i++;
                        //Log.d("test","codes="+codes);
                    }
                }
                TimeTable.add(time);
                time2note.put(time, codes);
                i = 0;
            }

        } catch (IOException e) {
        }
        //Log.d("test2","お試し"+TimeTable.size());
        return(time2note);
    }
*/

}
