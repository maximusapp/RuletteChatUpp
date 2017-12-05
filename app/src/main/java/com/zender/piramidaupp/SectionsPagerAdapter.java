package com.zender.piramidaupp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ОрганизациЯ on 02.10.2017.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter{
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ChatFragment chatFragment = new ChatFragment(mPeopleRVAdapter);
                return chatFragment;

            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            case 2:
                ReguestsFragment reguestsFragment = new ReguestsFragment();
                return reguestsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "Чаты";

            case 1:
                return "Контакты";

            case 2:
                return "Комнаты";

            default:
                return null;
        }

    }
}
