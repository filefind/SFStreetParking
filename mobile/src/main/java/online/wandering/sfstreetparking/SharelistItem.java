package online.wandering.sfstreetparking;

public class SharelistItem {
    private String name;
    private String status;
    private String lat;
    private String lng;

    public String getHeadline() {
        return name;
    }

    public void setHeadline(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
