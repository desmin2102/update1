package com.app.myapp.Class;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Movie {
    private String id;
    private String title;
    private String duration;
    private String movieDateStart;
    private String movieDateEnd;
    private String genre;
    private String rating;
    private String summary;
    private String trailerUrl;
    private String imageUrl; // Thêm thuộc tính imageUrl
//    private List<Review> userReviews;

    public Movie() {
        // Constructor mặc định
    }

    public Movie(String id, String title, String duration, String movieDateStart, String genre, String rating, String summary, String trailerUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.movieDateStart = movieDateStart;
        this.genre = genre;
        this.rating = rating;
        this.summary = summary;
        this.trailerUrl = trailerUrl;
        this.imageUrl = imageUrl; // Khởi tạo thuộc tính imageUrl
//        this.userReviews = userReviews;
    }

    // Các phương thức getter và setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMovieDateStart() {
        return movieDateStart;
    }

    public void setMovieDateStart(String movieDateStart) {
        this.movieDateStart = movieDateStart;
    }


    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    public List<Review> getUserReviews() {
//        return userReviews;
//    }
//
//    public void setUserReviews(List<Review> userReviews) {
//        this.userReviews = userReviews;
//    }

//    // Phương thức kiểm tra trạng thái chiếu của phim
//    public String getMovieStatus() throws ParseException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
//        Date currentDate = new Date();
//        Date startDate = dateFormat.parse(movieDateStart);
//        Date endDate = dateFormat.parse(movieDateEnd);
//
//        if (currentDate.before(startDate)) {
//            // Trả về ngày bắt đầu chiếu nếu phim sắp chiếu
//            return "Sắp chiếu";
//        } else if (currentDate.after(endDate)) {
//            // Trả về ngày kết thúc chiếu nếu phim đã kết thúc chiếu
//            return "Đã kết thúc chiếu";
//        } else {
//            // Trả về ngày hiện tại nếu phim đang chiếu
//            return "Đang chiếu";
//        }
//    }

}
