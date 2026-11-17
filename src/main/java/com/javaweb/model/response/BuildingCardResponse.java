package com.javaweb.model.response;

/** Dữ liệu gọn để hiển thị một thẻ tòa nhà ở trang chủ. */
public class BuildingCardResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String typeNames;
    private String areaText;
    private String priceText;
    private Integer rentPrice;
    private String imageUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTypeNames() { return typeNames; }
    public void setTypeNames(String typeNames) { this.typeNames = typeNames; }

    public String getAreaText() { return areaText; }
    public void setAreaText(String areaText) { this.areaText = areaText; }

    public String getPriceText() { return priceText; }
    public void setPriceText(String priceText) { this.priceText = priceText; }

    public Integer getRentPrice() { return rentPrice; }
    public void setRentPrice(Integer rentPrice) { this.rentPrice = rentPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
