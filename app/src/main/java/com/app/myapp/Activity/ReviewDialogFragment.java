package com.app.myapp.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.app.myapp.Class.Review;
import com.app.myapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class ReviewDialogFragment extends DialogFragment {

    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button cancelButton;
    private Button confirmButton;

    private String movieId;
    private String userId;

    public ReviewDialogFragment(String movieId, String userId) {
        this.movieId = movieId;
        this.userId = userId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_review, null);

        ratingBar = view.findViewById(R.id.ratingBar);
        reviewEditText = view.findViewById(R.id.reviewEditText);
        cancelButton = view.findViewById(R.id.cancelButton);
        confirmButton = view.findViewById(R.id.confirmButton);

        cancelButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> submitReview());

        builder.setView(view);
        return builder.create();
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String reviewText = reviewEditText.getText().toString();

        if (rating == 0 || reviewText.isEmpty()) {
            Toast.makeText(getActivity(), "Please provide a rating and review", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Review").push();
        String reviewId = reviewRef.getKey();
        String reviewTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Review review = new Review(reviewId, movieId, userId, reviewText, reviewTime, rating);

        reviewRef.setValue(review).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Review submitted successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Failed to submit review", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
