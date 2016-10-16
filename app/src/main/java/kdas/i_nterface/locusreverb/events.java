package kdas.i_nterface.locusreverb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Interface on 18/08/16.
 */
public class events {

    //Boolean[] events = new Boolean[3];
    List<String> m_events = new ArrayList<>();
    int day_c;

    events(List<String> events, int day_c){
        for(int i = 0; i < events.size(); ++i){
            m_events.add(i, events.get(i));
        }
        this.day_c = day_c;

    }
}
