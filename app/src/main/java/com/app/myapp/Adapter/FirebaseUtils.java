//package com.app.myapp.Adapter;
//
//import android.util.Log;
//
//import com.app.myapp.Class.MovieSession;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.List;
//
//public class FirebaseUtils {
//
//    private static final String TAG = "FirebaseUtils";
//
//    public static void addMovieSessionToDatabase(String movieId, String roomId, String locationId, String startTime, String endTime, int availableSeats) {
//        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("MovieSession");
//        String sessionId = sessionsRef.push().getKey();
//        if (sessionId != null) {
//            MovieSession movieSession = new MovieSession(sessionId, movieId, roomId, locationId, startTime, endTime, availableSeats);
//            sessionsRef.child(sessionId).setValue(movieSession)
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Movie session added successfully: " + sessionId))
//                    .addOnFailureListener(e -> Log.e(TAG, "Failed to add movie session: " + e.getMessage()));
//        }
//    }
//
//    public static void addSeatsToDatabase(String roomId, int rows, int columns) {
//        DatabaseReference seatsRef = FirebaseDatabase.getInstance().getReference("Seats");
//
//        for (int row = 0; row < rows; row++) {
//            for (int column = 0; column < columns; column++) {
//                String seatId = roomId + "_" + "R" + row + "C" + column;
//                Seat seat = new Seat(seatId, roomId, true); // Tạo ghế với roomId
//                seat.setSelected(false); // Mặc định ghế không được chọn
//                seatsRef.child(seatId).setValue(seat)
//                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Seat added successfully: " + seatId))
//                        .addOnFailureListener(e -> Log.e(TAG, "Failed to add seat: " + e.getMessage()));
//            }
//        }
//    }
//
//    public static void addSessionSeatsToDatabase(String sessionId, List<Seat> seats) {
//        DatabaseReference sessionSeatsRef = FirebaseDatabase.getInstance().getReference("SessionSeats").child(sessionId);
//
//        for (Seat seat : seats) {
//            sessionSeatsRef.child(seat.getSeatId()).child("isAvailable").setValue(seat.isAvailable());
//            sessionSeatsRef.child(seat.getSeatId()).child("isSelected").setValue(seat.isSelected())
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Session seat added successfully: " + seat.getSeatId()))
//                    .addOnFailureListener(e -> Log.e(TAG, "Failed to add session seat: " + e.getMessage()));
//        }
//    }
//
//    // Bạn có thể thêm các phương thức khác cho các đối tượng khác nếu cần
//}
