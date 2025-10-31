package com.example.appwork.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appwork.model.NewsCategory;
import com.example.appwork.ui.fragment.NewsListFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<NewsCategory> categories;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<NewsCategory> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        NewsCategory category = categories.get(position);
        return NewsListFragment.newInstance(category.getType());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
