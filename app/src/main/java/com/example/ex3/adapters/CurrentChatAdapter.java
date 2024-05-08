package com.example.ex3.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.R;
import com.example.ex3.entities.Msg;
import com.example.ex3.entities.User;
import com.example.ex3.objects.MsgSmall;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public class CurrentChatAdapter extends RecyclerView.Adapter<CurrentChatAdapter.CurrentChatViewHolder> {
    private final LayoutInflater mInflater;
    private List<MsgSmall> messages;
    private User me;

    public CurrentChatAdapter(Context context, User me) {
        this.mInflater = LayoutInflater.from(context);
        this.me = me;
    }

    @NonNull
    @Override
    public CurrentChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.message_layout, parent, false);
        return new CurrentChatViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull CurrentChatViewHolder holder, int position) {
        if (messages != null) {
            final MsgSmall current = messages.get(position);

            // Set the appropriate alignment based on the message sender
            if (Objects.equals(me.getUsername(), current.getSender().getUsername())) {
                // Align message to the right
                holder.msg_container.setGravity(Gravity.END);
            } else {
                // Align message to the left
                holder.msg_container.setGravity(Gravity.START);
            }
            // Set the message time
            holder.content.setText(current.getContent());
            String timestamp = current.getCreated();
            String convertedTimestamp = current.getCreated();
            // Parse the timestamp string to an Instant object
            Instant instant = null;
            // Define the desired format

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    // Parse the timestamp using the formatter
                    formatter.parse(timestamp);
                    holder.time.setText(timestamp);
                } catch (DateTimeParseException e) {
                    System.out.println("The timestamp is not in the desired format.");
                    instant = Instant.parse(timestamp);
                    // Convert the Instant to the desired time zone (optional)
                    ZoneId zoneId = ZoneId.of("UTC");
                    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
                    int hour = zonedDateTime.getHour();
                    int minute = zonedDateTime.getMinute();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    convertedTimestamp = formatter.format(LocalTime.of(hour, minute));
                    holder.time.setText(convertedTimestamp);
                }
            }
        }
    }

    public void setMessages(List<MsgSmall> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    static class CurrentChatViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout msg_container;
        TextView time;
        TextView content;

        CurrentChatViewHolder(View itemView) {
            super(itemView);
            msg_container = itemView.findViewById(R.id.msg_container);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }
}
