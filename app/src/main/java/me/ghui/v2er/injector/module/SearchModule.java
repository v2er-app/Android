package me.ghui.v2er.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.module.home.SearchContract;
import me.ghui.v2er.module.home.SearchFragment;
import me.ghui.v2er.module.home.SearchPresenter;
import me.ghui.v2er.network.bean.BingSearchResultInfo;
import me.ghui.v2er.network.bean.SoV2EXSearchResultInfo;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.richtext.RichText;
import me.ghui.v2er.util.ViewHolderFontHelper;
import me.ghui.v2er.util.FontSizeUtil;

/**
 * Created by ghui on 02/06/2017.
 */

@Module
public class SearchModule {
    private SearchFragment mFragment;

    public SearchModule(SearchFragment fragment) {
        mFragment = fragment;
    }

    @Provides
    public SearchContract.IPresenter providePresenter() {
        return new SearchPresenter(mFragment);
    }

    @Provides
    public LoadMoreRecyclerView.Adapter<SoV2EXSearchResultInfo.Hit> provideAdapter() {
        return new CommonLoadMoreAdapter<SoV2EXSearchResultInfo.Hit>(mFragment.getContext(), R.layout.item_bing_search) {
            @Override
            protected void convert(ViewHolder holder, SoV2EXSearchResultInfo.Hit hit, int position) {
                // Use helper for cleaner code
                ViewHolderFontHelper.setTextWithSize(holder, R.id.search_result_title_tv,
                        hit.getSource().getTitle(), FontSizeUtil.getTitleSize());

                String footnote = hit.getSource().getCreator() + " 于 " + hit.getSource().getTime() + " 发表, " + hit.getSource().getReplies() + " 回复";
                ViewHolderFontHelper.setTextWithSize(holder, R.id.search_result_footnote_tv,
                        footnote, FontSizeUtil.getSubTextSize());

                ViewHolderFontHelper.setTextWithSize(holder, R.id.search_result_content_tv,
                        "", FontSizeUtil.getContentSize());
                RichText.from(hit.getSource().getContent())
                        .supportUrlClick(false)
                        .into(holder.getTextView(R.id.search_result_content_tv));
            }
        };
    }
}
