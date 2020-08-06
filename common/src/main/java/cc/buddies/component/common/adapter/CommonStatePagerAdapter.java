package cc.buddies.component.common.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 继承自FragmentStatePagerAdapter，Fragment在onDetach()后会销毁View同时销毁Fragment实例，
 * 在下次再onAttach该Fragment的时候，会创建新的实例，走onCreate方法。
 * <p>
 * 通用的FragmentStatePagerAdapter，使用BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT方式实现。
 */
public class CommonStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> data;
    private List<String> titles;

    public CommonStatePagerAdapter(FragmentManager fm, List<Fragment> data) {
        this(fm, data, null);
    }

    public CommonStatePagerAdapter(FragmentManager fm, List<Fragment> data, List<String> titles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.data = data;
        this.titles = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return data != null && data.size() > position ? data.get(position) : new Fragment();
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles != null && titles.size() > position ? titles.get(position) : "";
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
