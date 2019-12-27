package rmit.ad.mediaalert;

public class MovieListObject {
    protected String imgURL;
    protected String originalTitle;
    protected String releaseDate;
    protected int numberID;
    protected boolean isAdult;
    protected String originalLanguage;
    protected double voteAverage;
    protected String overview;


    public MovieListObject(String imgURL, String originalTitle, String releaseDate) {
        this.imgURL = imgURL;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
    }

    public MovieListObject(String imgURL, String originalTitle, String releaseDate, int numberID, boolean isAdult, String originalLanguage, double voteAverage, String overview) {
        this.imgURL = imgURL;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
        this.numberID = numberID;
        this.isAdult = isAdult;
        this.originalLanguage = originalLanguage;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }


    public int getNumberID() {
        return numberID;
    }

    public void setNumberID(int numberID) {
        this.numberID = numberID;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setAdult(boolean adult) {
        isAdult = adult;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
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
