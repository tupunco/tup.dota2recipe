package tup.dota2recipe.adapter;

import java.util.List;

import tup.dota2recipe.R;
import tup.dota2recipe.entity.ItemsItem;
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
 * 
 * @author tupunco
 * 
 */
public class ItemsImagesAdapter extends BaseAdapter {
    private final class ViewHolder {
        public TextView name;
        public TextView cost;
        public ImageView image;
    }

    private final DisplayImageOptions mImageLoadOptions;
    private final LayoutInflater mInflater;
    private final List<ItemsItem> mComponents;

    public ItemsImagesAdapter(Context context, DisplayImageOptions imageLoadOptions,
            List<ItemsItem> items) {
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
            view = mInflater.inflate(R.layout.fragment_itemsdetail_components_grid_item,
                    parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.text_items_name);
            holder.cost = (TextView) view.findViewById(R.id.text_items_cost);
            holder.image = (ImageView) view.findViewById(R.id.image_items);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        if (holder != null) {
            final ItemsItem item = (ItemsItem) getItem(position);
            ImageLoader.getInstance().displayImage(Utils.getItemsImageUri(item.keyName),
                    holder.image, mImageLoadOptions);
            holder.name.setText(item.dname_l);
            holder.cost.setText(String.valueOf(item.cost));
        }
        return view;
    }
}
