package com.example.top_up;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NotificationsPagerAdapter extends FragmentStateAdapter {

    public NotificationsPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new NewNotificationsFragment() : new ReadNotificationsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}