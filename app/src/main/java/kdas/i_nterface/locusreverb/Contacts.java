package kdas.i_nterface.locusreverb;

/**
 * Created by Interface on 15/08/16.
 */
public class Contacts {

    String contact_name, contact_num, contact_alph;

    Contacts(String contact_name, String contact_num){
        this.contact_name = contact_name;
        this.contact_num = contact_num;
        contact_alph = String.valueOf(contact_name.charAt(0));
    }
}
