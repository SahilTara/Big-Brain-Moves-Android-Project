package com.uottawa.bigbrainmoves.servio.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Service;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {
    private List<Service> services = new ArrayList<>();
    private Context context;

    public ServiceListAdapter(List<Service> services, Context context) {
        this.services.addAll(services);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.service_list_item, viewGroup, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Service service = services.get(position);

        viewHolder.serviceTypeNameText.setText(service.getType());
        viewHolder.serviceAuthorText.setText(String.format("%s(%s)", service.getServiceProviderName(),
                service.getServiceProviderUser()));

        viewHolder.serviceRating.setRating((float) service.getRating());

        viewHolder.parentLayout.setOnClickListener((view) -> {

        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTypeNameText;
        TextView serviceAuthorText;
        RatingBar serviceRating;
        CardView parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTypeNameText = itemView.findViewById(R.id.itemTypeTextView);
            serviceAuthorText = itemView.findViewById(R.id.itemAuthorTextView);
            serviceRating = itemView.findViewById(R.id.itemRating);
            parentLayout = itemView.findViewById(R.id.serviceTypeParentLayout);
        }
    }
}
