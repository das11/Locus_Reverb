package kdas.i_nterface.locusreverb;

/**
 * Created by Interface on 22/10/16.
 */

public class Peers {

    String name, uid, num, alphabet;

    Peers(String name, String uid, String num){
        this.name = name;
        this.num = num;
        this.uid = uid;
        alphabet = String.valueOf(name.charAt(0));
    }
}
