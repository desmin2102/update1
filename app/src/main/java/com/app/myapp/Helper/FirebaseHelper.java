package com.app.myapp.Helper;

import com.app.myapp.Class.Invoice;
import com.app.myapp.Class.Ticket;
import com.app.myapp.Class.Seat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;

public class FirebaseHelper {

    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveInvoice(Invoice invoice, OnCompleteListener<Void> onCompleteListener) {
        String invoiceId = databaseReference.child("Invoice").push().getKey();
        if (invoiceId != null) {
            invoice.setInvoiceId(invoiceId);
            databaseReference.child("Invoice").child(invoiceId).setValue(invoice).addOnCompleteListener(onCompleteListener);
        }
    }

    public void saveTicket(Ticket ticket, OnCompleteListener<Void> onCompleteListener) {
        String ticketId = databaseReference.child("Ticket").push().getKey();
        if (ticketId != null) {
            ticket.setTicketId(ticketId);
            databaseReference.child("Ticket").child(ticketId).setValue(ticket).addOnCompleteListener(onCompleteListener);
        }
    }

    public void saveSeat(Seat seat, OnCompleteListener<Void> onCompleteListener) {
        String seatId = databaseReference.child("Seats").push().getKey();
        if (seatId != null) {
            seat.setSeatId(seatId);
            databaseReference.child("Seats").child(seatId).setValue(seat).addOnCompleteListener(onCompleteListener);
        }
    }

    public void getMovieName(String movieId, ValueEventListener valueEventListener) {
        databaseReference.child("Movies").child(movieId).addListenerForSingleValueEvent(valueEventListener);
    }

    public void getRoomName(String roomId, ValueEventListener valueEventListener) {
        databaseReference.child("Rooms").child(roomId).addListenerForSingleValueEvent(valueEventListener);
    }

    public void getLocationName(String locationId, ValueEventListener valueEventListener) {
        databaseReference.child("Locations").child(locationId).addListenerForSingleValueEvent(valueEventListener);
    }

    public void getSessionDetails(String sessionId, ValueEventListener valueEventListener) {
        databaseReference.child("MovieSession").child(sessionId).addListenerForSingleValueEvent(valueEventListener);
    }

    public void getSeatIdFromSeatName(String seatName, ValueEventListener valueEventListener) {
        databaseReference.child("Seats").orderByChild("name").equalTo(seatName).addListenerForSingleValueEvent(valueEventListener);
    }

    public void getPurchasedSeats(String sessionId, ValueEventListener valueEventListener) {
        databaseReference.child("Seat").orderByChild("sessionId").equalTo(sessionId).addListenerForSingleValueEvent(valueEventListener);
    }

    public void getRoomInfo(String roomId, ValueEventListener valueEventListener) {
        databaseReference.child("Room").child(roomId).addListenerForSingleValueEvent(valueEventListener);
    }
    public void getMovieSessions(String dateString, ValueEventListener valueEventListener) {
        databaseReference.child("MovieSession").orderByChild("startDay").equalTo(dateString).addListenerForSingleValueEvent(valueEventListener);
    }
    public void getRooms(ValueEventListener valueEventListener) { databaseReference.child("Room").addListenerForSingleValueEvent(valueEventListener); }
}
