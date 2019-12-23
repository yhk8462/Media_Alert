package rmit.ad.mediaalert;

public class GameList {
    private String name;
    private String date;
    private String company;
    private String platform;
    private String description;

    public GameList(){

    }

    public GameList(String name, String date, String company, String platform, String description) {
        this.name = name;
        this.date = date;
        this.company = company;
        this.platform = platform;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
