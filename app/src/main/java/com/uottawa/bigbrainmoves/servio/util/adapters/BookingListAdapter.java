package com.uottawa.bigbrainmoves.servio.util.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.activities.ViewServiceActivity;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.Rating;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.presenters.BookingRecyclerPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;
import com.uottawa.bigbrainmoves.servio.util.enums.BookingStatus;
import com.uottawa.bigbrainmoves.servio.views.BookingRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.ViewHolder> implements BookingRecyclerView {
    private List<Booking> bookings = new ArrayList<>();
    private Context context;
    private BookingRecyclerPresenter presenter;
    private Calendar now = Calendar.getInstance();
    private LayoutInflater inflater;

    public BookingListAdapter(List<Booking> bookings, LayoutInflater inflater, Context context) {
        this.bookings.addAll(bookings);
        this.context = context;
        this.inflater = inflater;
        Repository repository = new DbHandler();
        presenter = new BookingRecyclerPresenter(this, repository);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.booking_list_item, viewGroup, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Booking booking = bookings.get(position);
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        AccountType accountType = account.getType();

        BookingStatus bookingStatus = booking.getStatus();

        String dateTime = booking.getDate() + " " + booking.getStartTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);
        Calendar start = Calendar.getInstance();

        try {
            start.setTime(format.parse(dateTime));
        } catch (ParseException e) {
            displayDbError();
            return;
        }


        switch (bookingStatus) {
            case PENDING:
                if (start.before(now)) {
                    presenter.updateStatusOfBooking(booking, BookingStatus.CANCELLED);
                }
                viewHolder.bookingStatusText.setTextColor(ContextCompat.getColor(context, R.color.pending));
                break;
            case ACCEPTED:
                if (start.before(now)) {
                    presenter.updateStatusOfBooking(booking, BookingStatus.COMPLETED);
                }
                viewHolder.bookingStatusText.setTextColor(ContextCompat.getColor(context, R.color.accepted));
                break;
            case CANCELLED:
                viewHolder.bookingStatusText.setTextColor(ContextCompat.getColor(context, R.color.cancelled));
                break;
            case COMPLETED:
                viewHolder.bookingStatusText.setTextColor(ContextCompat.getColor(context, R.color.completed));
                break;
        }

        if (accountType.equals(AccountType.HOME_OWNER)) {
            viewHolder.btnView.setVisibility(View.VISIBLE);

            if (bookingStatus.equals(BookingStatus.PENDING) || bookingStatus.equals(BookingStatus.ACCEPTED)) {
                viewHolder.btnCancel.setVisibility(View.VISIBLE);
            } else if (bookingStatus.equals(BookingStatus.COMPLETED)) {
                viewHolder.btnRate.setVisibility(View.VISIBLE);
            }

        } else if (accountType.equals(AccountType.SERVICE_PROVIDER)) {
            if (bookingStatus.equals(BookingStatus.PENDING)) {
                viewHolder.btnAccept.setVisibility(View.VISIBLE);
                viewHolder.btnCancel.setVisibility(View.VISIBLE);
            } else if (bookingStatus.equals(BookingStatus.ACCEPTED)) {
                viewHolder.btnCancel.setVisibility(View.VISIBLE);
            }
        } else {
            if (bookingStatus.isParsable()) {
                viewHolder.btnForce.setVisibility(View.VISIBLE);
            }
        }

        viewHolder.bookingStatusText.setText(bookingStatus.toString());
        viewHolder.serviceTypeText.setText(booking.getServiceType());

        if (accountType.equals(AccountType.SERVICE_PROVIDER)) {
            viewHolder.bookingProviderText.setText(booking.getCustomerUser());
        } else {
            viewHolder.bookingProviderText.setText(booking.getProviderUser());
        }

        viewHolder.bookingDateTimeText.setText(
                String.format(Locale.ENGLISH, "%s %s~%s", booking.getDate(),
                              booking.getStartTime(), booking.getEndTime())
        );


        viewHolder.btnAccept.setOnClickListener(__ -> {
            presenter.updateStatusOfBooking(booking, BookingStatus.ACCEPTED);
            Toast.makeText(context, "Successfully accepted booking", Toast.LENGTH_LONG).show();
        });

        viewHolder.btnCancel.setOnClickListener(__ -> {
            presenter.updateStatusOfBooking(booking, BookingStatus.CANCELLED);
            Toast.makeText(context, "Successfully cancelled booking", Toast.LENGTH_LONG).show();
        });

        viewHolder.btnRate.setOnClickListener(__ -> {
            presenter.loadRating(booking);
        });

        viewHolder.btnView.setOnClickListener(__ -> {
            presenter.getService(booking.getServiceType() + booking.getProviderUser());
        });


        viewHolder.btnForce.setOnClickListener(__ -> {
            presenter.updateStatusOfBooking(booking, BookingStatus.COMPLETED);
            Toast.makeText(context, "Successfully forced booking to be completed", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    @Override
    public void displayRated() {
        Toast.makeText(context,
                "Successfully set rating for the service provider's service",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayFailedRating() {
        Toast.makeText(context, "Could not apply the rating at this time", Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayDbError() {
        Toast.makeText(context, "Error while communicating with database", Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayService(Service service) {
        Intent intent = new Intent(context, ViewServiceActivity.class);
        intent.putExtra("service", service);
        context.startActivity(intent);
    }

    @Override
    public void displayServiceNotOffered() {
        Toast.makeText(context, "Sorry this service is no longer offered", Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayRating(Rating rating, boolean isRated) {
        AlertDialog.Builder ratingDialog = new AlertDialog.Builder(context);

        View ratingDialogView = inflater.inflate(R.layout.rating_dialog, null, false);
        ratingDialog.setView(ratingDialogView);
        String positiveText = "Save Rating";

        RatingBar ratingBar = ratingDialogView.findViewById(R.id.ratingBar);
        EditText commentText = ratingDialogView.findViewById(R.id.commentText);

        double oldValue = rating.getRating();
        ratingBar.setRating((float) oldValue);
        commentText.setText(rating.getComment());

        if (isRated) {
            positiveText = "Update Rating";
        }

        ratingDialog.setPositiveButton(positiveText, (dialog, __) -> {
            rating.setRating(ratingBar.getRating());
            rating.setComment(commentText.getText().toString());
            presenter.setRating(rating, oldValue, isRated);
        }).setNegativeButton("Cancel", (dialog, __) -> {
            dialog.dismiss();
        }).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTypeText;
        TextView bookingStatusText;
        TextView bookingProviderText;
        TextView bookingDateTimeText;

        Button btnAccept;
        Button btnCancel;
        Button btnForce;
        Button btnRate;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTypeText = itemView.findViewById(R.id.serviceTypeTextView);
            bookingStatusText = itemView.findViewById(R.id.bookingStatusTextView);
            bookingProviderText = itemView.findViewById(R.id.bookingProviderTextView);
            bookingDateTimeText = itemView.findViewById(R.id.bookingDateTimeTextView);

            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnForce = itemView.findViewById(R.id.btnForceComplete);
            btnView = itemView.findViewById(R.id.btnViewService);
            btnRate = itemView.findViewById(R.id.btnRate);

        }
    }
}
