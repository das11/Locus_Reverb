package kdas.i_nterface.locusreverb;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Interface on 31/08/16.
 */
public class timeline_day_data {

    //java.util.List<List<String>> base1 = new ArrayList<List<String>>();
    List<String> base1 = new ArrayList<>();
    List<String> base2 = new ArrayList<>();
    String temp;
    Location point_location;

    timeline_day_data(List<String> base1, List<String> base2, Location point_location){

        this.base1 = base1;
        temp = base1.get(3);
        Log.d("base 1", base1.toString() + "\n" + temp);
        this.base2 = base2;
        this.point_location = point_location;
    }
}
