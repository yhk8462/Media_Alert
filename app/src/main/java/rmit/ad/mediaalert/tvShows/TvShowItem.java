package rmit.ad.mediaalert.tvShows;

public class TvShowItem {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private String releaseDate;
    private String tvShowType;

    public TvShowItem(){}

    public TvShowItem(String id, String name, String imageUrl, String description, String releaseDate, String tvShowType) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.releaseDate = releaseDate;
        this.tvShowType = tvShowType;
    }

    public TvShowItem(String name, String imageUrl, String description, String releaseDate, String tvShowType) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.releaseDate = releaseDate;
        this.tvShowType = tvShowType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTvShowType() {
        return tvShowType;
    }

    public void setTvShowType(String tvShowType) {
        this.tvShowType = tvShowType;
    }
}
