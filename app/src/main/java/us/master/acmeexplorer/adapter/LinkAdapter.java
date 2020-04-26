package us.master.acmeexplorer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import java.util.List;
import us.master.acmeexplorer.R;
import us.master.acmeexplorer.entity.Link;

public class LinkAdapter extends BaseAdapter {

    private Context context;
    private List<Link> links;

    public LinkAdapter(Context context, List<Link> links) {
        this.context = context;
        this.links = links;
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public Link getItem(int position) {
        return links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return links.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.link_item, parent, false);
        }

        Link link = getItem(position);
        ImageView linkImage = convertView.findViewById(R.id.resourceImage);
        TextView linkDescription = convertView.findViewById(R.id.linkDescription);
        CardView cardView = convertView.findViewById(R.id.cardView);

        linkDescription.setText(link.getDescription());
        linkImage.setImageResource(link.getResourceImageView());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, link.getIntentClass());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
