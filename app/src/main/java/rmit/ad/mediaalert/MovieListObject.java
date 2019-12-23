package rmit.ad.mediaalert;

public class MovieListObject {
    protected String imgURL;
    protected String originalTitle;
    protected String releaseDate;

    public MovieListObject(String imgURL, String originalTitle, String releaseDate) {
        this.imgURL = imgURL;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
