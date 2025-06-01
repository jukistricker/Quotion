package com.example.quotion.ui.focus;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FocusRepository {

    private final DatabaseReference focusRef;

    public interface SessionLoadCallback {
        void onResult(Map<String, Long> result); // result: { "MON": totalMillis, ... }
    }

    public interface SaveCallback {
        void onComplete(boolean success);
    }

    public FocusRepository() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        focusRef = FirebaseDatabase.getInstance().getReference("focus_sessions").child(userId);
    }

    public void saveFocusSession(FocusSession session, @NonNull final SaveCallback callback) {
        String sessionId = focusRef.push().getKey();
        if (sessionId != null) {
            focusRef.child(sessionId).setValue(session)
                    .addOnSuccessListener(aVoid -> callback.onComplete(true))
                    .addOnFailureListener(e -> callback.onComplete(false));
        } else {
            callback.onComplete(false);
        }
    }

    public void loadFocusSessionsPerDay(@NonNull final SessionLoadCallback callback) {
        focusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> focusPerWeekday = new HashMap<>();

                for (DataSnapshot sessionSnapshot : snapshot.getChildren()) {
                    FocusSession session = sessionSnapshot.getValue(FocusSession.class);
                    if (session != null && session.startTime > 0 && session.endTime > 0) {
                        String dateString = session.date;
                        long duration = session.getDuration();

                        if (dateString != null) {
                            String weekday = getWeekdayFromDate(dateString);
                            long total = focusPerWeekday.getOrDefault(weekday, 0L);
                            focusPerWeekday.put(weekday, total + duration);
                        }
                    }
                }

                callback.onResult(focusPerWeekday);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi tải dữ liệu", error.toException());
                callback.onResult(Collections.emptyMap());
            }
        });
    }

    // Helper để lấy tên thứ từ ngày yyyy-MM-dd
    private String getWeekdayFromDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(dateStr);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1=SUN ... 7=SAT

                switch (dayOfWeek) {
                    case Calendar.SUNDAY:
                        return "SUN";
                    case Calendar.MONDAY:
                        return "MON";
                    case Calendar.TUESDAY:
                        return "TUE";
                    case Calendar.WEDNESDAY:
                        return "WED";
                    case Calendar.THURSDAY:
                        return "THU";
                    case Calendar.FRIDAY:
                        return "FRI";
                    case Calendar.SATURDAY:
                        return "SAT";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "UNKNOWN";
    }
}
