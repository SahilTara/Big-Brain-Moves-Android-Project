package com.uottawa.bigbrainmoves.servio.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;

import java.util.ArrayList;
import java.util.List;

public class ServiceTypeListAdapter extends RecyclerView.Adapter<ServiceTypeListAdapter.ViewHolder> {
    private List<ServiceType> serviceTypes = new ArrayList<>();
    private Context context;
    Repository repository = new DbHandler();
    public ServiceTypeListAdapter(List<ServiceType> serviceTypes, Context context) {
        this.serviceTypes.addAll(serviceTypes);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row, viewGroup, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ServiceType serviceType = serviceTypes.get(position);
        viewHolder.serviceTypeNameText.setText(serviceType.getType());
        final String rateText = "$" + String.valueOf(serviceType.getRate());
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
        ConstraintLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTypeNameText = itemView.findViewById(R.id.userText);
            serviceRateText = itemView.findViewById(R.id.typeText);
            parentLayout = itemView.findViewById(R.id.userParentLayout);
        }
    }
}
