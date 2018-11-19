package com.uottawa.bigbrainmoves.servio.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.presenters.ServiceProviderRecyclerPresenter;
import com.uottawa.bigbrainmoves.servio.views.ServiceProviderRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServicesOfferedListAdapter extends RecyclerView.Adapter<ServicesOfferedListAdapter.ViewHolder>
        implements ServiceProviderRecyclerView {
    private List<Service> services = new ArrayList<>();
    private boolean isEditMode;
    private ServiceProviderRecyclerPresenter presenter;

    public ServicesOfferedListAdapter(List<Service> services, boolean isEditMode) {
        this.services.addAll(services);
        this.isEditMode = isEditMode;
    }

    public void setOtherAdapter(ServicesOfferableListAdapter other) {
        presenter = new ServiceProviderRecyclerPresenter(this, other);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.service_provider_provided_service_chip, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Service service = services.get(position);
        viewHolder.chip.setText(service.getType());
        if (isEditMode) {
            editMode(viewHolder, service);
        } else {
            readOnlyMode(viewHolder, service);
        }

    }

    private void editMode(ViewHolder viewHolder, Service service) {
        viewHolder.chip.setCloseIconVisible(true);

        viewHolder.chip.setOnCloseIconClickListener(view -> {
            if (presenter != null) {
                presenter.transferItem(service);
            }
        });


    }

    private void readOnlyMode(ViewHolder viewHolder, Service service) {
        viewHolder.titleText.setText(service.getType());
        viewHolder.ratingText.setText(String.valueOf(service.getRating()));
        viewHolder.chip.setCloseIconVisible(false);

        viewHolder.chip.setOnClickListener(view -> {
            view.setVisibility(View.GONE);
            viewHolder.infoCardLayout.setVisibility(View.VISIBLE);
        });

        viewHolder.closeButton.setOnClickListener(view -> {
            viewHolder.infoCardLayout.setVisibility(View.GONE);
            viewHolder.chip.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public List<Service> getServices() {
        return new ArrayList<>(services);
    }

    @Override
    public void addItem(Service service) {
        services.add(service);
        notifyDataSetChanged();
    }

    @Override
    public void removeItem(Service service) {
        services.remove(service);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView ratingText;
        LinearLayout infoCardLayout;
        ImageButton closeButton;
        Chip chip;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.cardTitleView);
            ratingText = itemView.findViewById(R.id.ratingView);
            infoCardLayout = itemView.findViewById(R.id.infoCardLayout);
            closeButton = itemView.findViewById(R.id.imageButton);
            chip = itemView.findViewById(R.id.serviceOfferedChip);
        }
    }
}
