package quvesoft.project2.classes;

public class Review {
    private String ImageURL;
    private String UserID;
    private String UserImg;
    private String Nickname;
    private String Contents;
    private long Date;
    private int Rating;

    public Review(String imageURL, String userID, String userImg, String nickname, String contents, long date, int rating) {
        ImageURL = imageURL;
        UserID = userID;
        UserImg = userImg;
        Nickname = nickname;
        Contents = contents;
        Date = date;
        Rating = rating;
    }

    public int getRating() {
        return Rating;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUserImg() {
        return UserImg;
    }

    public String getNickname() {
        return Nickname;
    }

    public String getContents() {
        return Contents;
    }

    public long getDate() {
        return Date;
    }
}