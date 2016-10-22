package kdas.i_nterface.locusreverb;

/**
 * Created by Interface on 22/10/16.
 */

public class Peers {

    String name, num, alphabet;

    Peers(String name, String num){
        this.name = name;
        this.num = num;
        alphabet = String.valueOf(name.charAt(0));
    }
}
