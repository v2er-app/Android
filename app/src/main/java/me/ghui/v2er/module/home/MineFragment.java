package me.ghui.v2er.module.home;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Page;
import me.ghui.v2er.injector.component.DaggerMineComponent;
import me.ghui.v2er.injector.module.MineModule;
import me.ghui.v2er.module.create.CreateTopicActivity;
import me.ghui.v2er.module.drawer.care.SpecialCareActivity;
import me.ghui.v2er.module.drawer.star.StarActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.NotificationInfo;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.widget.BaseRecyclerView;
import me.ghui.v2er.widget.FollowProgressBtn;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.SectionItemView;

/**
 * 首页我的页面
 */
public class MineFragment extends BaseHomeFragment<MineContract.IPresenter> implements MineContract.IView,
        View.OnClickListener, SectionItemView.OnSectionClickListener {

    @BindView(R.id.mine_root_layout)
    ConstraintLayout mRootLayout;
    @BindView(R.id.mine_avatar_img)
    CircleImageView mAvatarImage;
    @BindView(R.id.mine_username_button)
    Button mUserNameButton;
    @BindView(R.id.mine_user_info_page_button)
    Button mUserInfoPageButton;
    @BindView(R.id.mine_check_in_progress_btn)
    FollowProgressBtn mCheckInButton;

    @BindView(R.id.mine_sec_post)
    SectionItemView mSecPost;
    @BindView(R.id.mine_sec_themes)
    SectionItemView mSecThemes;
    @BindView(R.id.mine_sec_bookmark)
    SectionItemView mSecBookmark;
    @BindView(R.id.mine_sec_focus)
    SectionItemView mSecFocus;
    @BindView(R.id.mine_sec_settings)
    SectionItemView mSecSettings;

    private CheckInPresenter mCheckInPresenter;

    private void initCheckIn() {
        if (UserUtils.isLogin()) {
            mCheckInButton.setVisibility(View.VISIBLE);
            mCheckInPresenter = new CheckInPresenter(new CheckInContract.ICheckInCallBack() {

                @Override
                public void onHasChekIn(String checkInDays) {
                    mCheckInButton.setStatus(FollowProgressBtn.FINISHED,
                            getString(R.string.number_of_days_checked_in, checkInDays), R.drawable.progress_button_done_icon);
                }

                @Override
                public void onCheckInSuccess(String checkInDays) {
                    toast(getString(R.string.number_of_days_check_in_succeed, checkInDays));
                    mCheckInButton.setStatus(FollowProgressBtn.FINISHED,
                            getString(R.string.number_of_days_checked_in, checkInDays), R.drawable.progress_button_done_icon);
                }

                @Override
                public void onCheckInFail() {
                    toast(getString(R.string.problems_with_check_in));
                    mCheckInButton.setStatus(FollowProgressBtn.NORMAL, getString(R.string.check_in),
                            R.drawable.progress_button_checkin_icon);
                }

            });
            mCheckInPresenter.start();
        }
    }

    public static MineFragment newInstance(BaseHomeFragment.RestoreData restoreData) {
        Bundle args = new Bundle();
        if (restoreData != null) {
            args.putSerializable(KEY_DATA, restoreData);
        }
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void startInject() {
        DaggerMineComponent.builder()
                .appComponent(getAppComponent())
                .mineModule(new MineModule(this))
                .build()
                .inject(this);
    }

    private UserInfo userInfo;
    private void initDisplayUserName() {
        userInfo = UserUtils.getUserInfo();
        if (userInfo == null) {
            mUserNameButton.setContentDescription(getText(R.string.please_login_first));
            mUserNameButton.setText(R.string.please_login_first);
            mAvatarImage.setImageResource(R.drawable.default_avatar_drawable);
        } else {
            mUserNameButton.setContentDescription(userInfo.getUserName());
            mUserNameButton.setText(userInfo.getUserName());
            if (getContext() != null) {
                GlideApp.with(getContext())
                        .load(userInfo.getAvatar())
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into(mAvatarImage);
            }
        }
    }

    @Override
    protected void init() {
        hideLoading();
        mAvatarImage.setOnClickListener(this);
        mUserNameButton.setOnClickListener(this);
        mUserInfoPageButton.setOnClickListener(this);
        mCheckInButton.setOnClickListener(this);
        mSecPost.setOnSectionClickListener(this);
        mSecThemes.setOnSectionClickListener(this);
        mSecBookmark.setOnSectionClickListener(this);
        mSecFocus.setOnSectionClickListener(this);
        mSecSettings.setOnSectionClickListener(this);
        initDisplayUserName();
        initCheckIn();
    }

    private void goToUserInfoPage() {
        if (UserUtils.isLogin()) {
            if (getContext() != null) {
                UserHomeActivity.open(userInfo.getUserName(), getContext(),
                        userInfo.getAvatar(), mAvatarImage, mUserNameButton);
            }
        } else {
            if (getContext() != null) {
                Navigator.from(getContext()).to(LoginActivity.class).start();
            }
        }
    }

    /**
     * 发帖
     */
    private void goToPost() {
        if (UserUtils.notLoginAndProcessToLogin(false, getContext())) {
            return;
        }
        if (UserUtils.isLogin()) {
            Navigator.from(getContext()).to(CreateTopicActivity.class).start();
        }
    }

    /**
     * 收藏
     */
    private void goToBookmark() {
        if (UserUtils.notLoginAndProcessToLogin(false, getContext())) {
            return;
        }
        if (UserUtils.isLogin()) {
            Navigator.from(getContext()).to(StarActivity.class).start();
        }
    }

    /**
     * 关注
     */
    private void goToFocus() {
        if (UserUtils.notLoginAndProcessToLogin(false, getContext())) {
            return;
        }
        if (UserUtils.isLogin()) {
            Navigator.from(getContext()).to(SpecialCareActivity.class).start();
        }
    }

    /**
     * 设置
     */
    private void goToSetting() {
        Navigator.from(getContext()).to(Page.SETTING).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_check_in_progress_btn:
                mCheckInButton.startUpdate();
                mCheckInPresenter.checkInToDay();
                break;
            case R.id.mine_avatar_img:
            case R.id.mine_username_button:
            case R.id.mine_user_info_page_button:
                goToUserInfoPage();
                break;
            case R.id.mine_sec_post:
                goToPost();
                break;
            case R.id.mine_sec_bookmark:
                goToBookmark();
                break;
            case R.id.mine_sec_focus:
                goToFocus();
                break;
            case R.id.mine_sec_settings:
                goToSetting();
                break;
            default:
                break;
        }
    }
}