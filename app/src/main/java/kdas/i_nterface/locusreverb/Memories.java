package kdas.i_nterface.locusreverb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.gigamole.navigationtabstrip.NavigationTabStrip;

public class Memories extends AppCompatActivity {

    FragmentPagerAdapter VPadapter;
    private static String[] tabTitles = new String[]{"TAB 1", "TAB 2", "TAB 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);

        ViewPager vp = (ViewPager)findViewById(R.id.viewPager);
        VPadapter = new pagerAdapter(getSupportFragmentManager());
        vp.setAdapter(VPadapter);

        //TabLayout tabLayout = (TabLayout)findViewById(R.id.tab);
        //tabLayout.setupWithViewPager(vp);

        NavigationTabStrip ts = (NavigationTabStrip)findViewById(R.id.tab);
        ts.setTitles("Friends", "Professional", "Family");
        ts.setTitleSize(25);
        ts.setViewPager(vp);

    }

    public static class pagerAdapter extends FragmentPagerAdapter{


        public pagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0 :
                    return Frag_friends.newInstance("", "");
                case 1 :
                    return Frag_professional.newInstance("", "");
                case 2 :
                    return Frag_family.newInstance("","");
                default  :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}
