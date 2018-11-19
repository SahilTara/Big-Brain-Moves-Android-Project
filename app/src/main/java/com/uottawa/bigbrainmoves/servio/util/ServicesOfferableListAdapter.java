package com.uottawa.bigbrainmoves.servio.util;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.presenters.ServiceProviderRecyclerPresenter;
import com.uottawa.bigbrainmoves.servio.views.ServiceProviderRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServicesOfferableListAdapter extends RecyclerView.Adapter<ServicesOfferableListAdapter.ViewHolder>
        implements ServiceProviderRecyclerView {
    private List<Service> services = new ArrayList<>();
    private boolean isEditMode;
    private ServiceProviderRecyclerPresenter presenter;

    public ServicesOfferableListAdapter(List<Service> services, boolean isEditMode) {
        this.services.addAll(services);
        this.isEditMode = isEditMode;
    }

    public void setOtherAdapter(ServicesOfferedListAdapter other) {
        presenter = new ServiceProviderRecyclerPresenter(this, other);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.service_provider_provideable_service_chip, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Service service = services.get(position);
        viewHolder.chip.setText(service.getType());
        if (isEditMode) {
            editMode(viewHolder, service);
        }

    }

    private void editMode(ViewHolder viewHolder, Service service) {
        viewHolder.chip.setOnClickListener(view -> {
            if (presenter != null) {
                presenter.transferItem(service);
            }
        });
    }

    public List<Service> getServices() {
        return new ArrayList<>(services);
    }

    @Override
    public int getItemCount() {
        return services.size();
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
        Chip chip;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.serviceOfferableChip);
        }
    }
}
