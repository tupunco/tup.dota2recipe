package tup.dota2recipe.adapter;

import java.util.List;

import tup.dota2recipe.R;
import tup.dota2recipe.entity.HeroItem;
import tup.dota2recipe.util.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 推荐使用英雄 Adapter
 * 
 * @author tupunco
 */
public final class HeroImagesAdapter extends BaseAdapter {
    private final class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    private final DisplayImageOptions mImageLoadOptions;
    private final LayoutInflater mInflater;
    private final List<HeroItem> mComponents;

    public HeroImagesAdapter(Context context, DisplayImageOptions imageLoadOptions,
            List<HeroItem> items) {
        super();

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoadOptions = imageLoadOptions;
        mComponents = items;
    }

    @Override
    public int getCount() {
        return mComponents.size();
    }

    @Override
    public Object getItem(int position) {
        return mComponents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        final ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.fragment_itemsdetail_hero_grid_item, parent, false);

            holder = new ViewHolder();
            holder.text = (TextView) view.findViewById(R.id.text_hero_name);
            holder.image = (ImageView) view.findViewById(R.id.image_hero);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HeroItem item = (HeroItem) getItem(position);
        ImageLoader.getInstance().displayImage(Utils.getHeroImageUri(item.keyName),
                holder.image, mImageLoadOptions);
        holder.text.setText(item.name_l);

        return view;
    }
}
