package com.uottawa.bigbrainmoves.servio.util;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceTypeListAdapter extends RecyclerView.Adapter<ServiceTypeListAdapter.ViewHolder> {
    private List<ServiceType> serviceTypes = new ArrayList<>();
    private Context context;
    private final Repository repository = new DbHandler();

    public ServiceTypeListAdapter(List<ServiceType> serviceTypes, Context context) {
        this.serviceTypes.addAll(serviceTypes);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.service_type_list_item, viewGroup, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ServiceType serviceType = serviceTypes.get(position);
        viewHolder.serviceTypeNameText.setText(serviceType.getType());
        final String rateText = "$" + String.format(Locale.getDefault(), "%.2f",serviceType.getRate());
        viewHolder.serviceRateText.setText(rateText);

        viewHolder.parentLayout.setOnClickListener((view) -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle(serviceType.getType()).setPositiveButton("Edit Value", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(serviceType.getType());

                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    builder.setView(input);

                    builder.setPositiveButton("OK", (dialog1, which1) -> {
                        String result = input.getText().toString();
                        try {
                            double val = Double.valueOf(result);
                            if (val <= Double.valueOf("1.7E308")) {
                                val = Math.round(val * 100.0) / 100.0;
                                repository.editServiceType(serviceType.getType(), val);
                                Toast.makeText(context,
                                        "Value of service type successfully changed",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Value must be less than or equal to 1.7*10^308", Toast.LENGTH_LONG).show();

                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Value must be non blank.", Toast.LENGTH_LONG).show();
                        }
                        dialog1.dismiss();
                    });
                    builder.setNegativeButton("Cancel", (dialog12, which12) -> dialog12.cancel());

                    builder.show();
                }
            }).setNegativeButton("Delete Service Type", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    repository.deleteServiceType(serviceType.getType());
                    dialog.dismiss();
                }
            }).show();
        });
    }

    @Override
    public int getItemCount() {
        return serviceTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTypeNameText;
        TextView serviceRateText;
        CardView parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTypeNameText = itemView.findViewById(R.id.itemTypeTextView);
            serviceRateText = itemView.findViewById(R.id.itemValueTextView);
            parentLayout = itemView.findViewById(R.id.serviceTypeParentLayout);
        }
    }
}
