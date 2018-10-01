package ducic.plumbum.com.bjp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.fragment.CommentedPostsFragment;
import ducic.plumbum.com.bjp.fragment.LikedPostsFragment;
import ducic.plumbum.com.bjp.utils.CustomTabHelper;
import ducic.plumbum.com.bjp.utils.TimelineDetails;

/**
 * Project Name: 	<Bharatiya>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 * Date of Creation:    <21/03/2018>
 */
public class ProfileActivity extends AppCompatActivity implements CommentedPostsFragment.OnListFragmentInteractionListener, LikedPostsFragment.OnListFragmentInteractionListener {

    SharedPreferences sp;
    CommentedPostsFragment eaf;
    LikedPostsFragment ff;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Voted");
        tabLayout.getTabAt(1).setText("Commented");

//        viewPager.setCurrentItem(1);
    }


    public void setupViewPager(ViewPager upViewPager) {
        eaf = new CommentedPostsFragment();
        ff = new LikedPostsFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ff, "Voted");
        adapter.addFragment(eaf, "Commented");
        upViewPager.setAdapter(adapter);
    }

    public void getAllPosts(int counter) {

    }

    @Override
    public void onListFragmentInteraction(TimelineDetails item) {
        String url = item.getUrl();
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        open(url);
    }

    private void open(String url) {
        CustomTabHelper mCustomTabHelper = new CustomTabHelper();
        if (mCustomTabHelper.getPackageName(this).size() != 0) {
            CustomTabsIntent customTabsIntent =
                    new CustomTabsIntent.Builder()
                            .build();
            customTabsIntent.intent.setPackage(mCustomTabHelper.getPackageName(this).get(0));
            customTabsIntent.launchUrl(this, Uri.parse(url));
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_inside, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if(id == R.id.action_shout){
//            // TODO: FILL THIS SECTION
//        }else{
//            finish();
//        }
        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
