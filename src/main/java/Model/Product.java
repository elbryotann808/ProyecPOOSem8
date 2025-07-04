package Model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Product {
    private String code;
    private String name;
    private String description;
    private BigDecimal priceBase;
    private BigDecimal priceSale;
    private String category;
    private int stock;

    private static final Locale COLOMBIA = new Locale("es", "CO");
    private static final NumberFormat COP_FORMATTER = NumberFormat.getNumberInstance(COLOMBIA);
    static {
        COP_FORMATTER.setGroupingUsed(true);
        COP_FORMATTER.setMaximumFractionDigits(0);
    }

    public Product(String code, String name, String description, BigDecimal priceBase, BigDecimal priceSale, String category ,int stock ){
        this.code = code;
        this.name = name;
        this.description = description;
        this.priceBase = priceBase;
        this.priceSale = priceSale;
        this.category = category;
        this.stock = stock;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPriceBase() { return priceBase; }
    public BigDecimal getPriceSale() { return priceSale; }
    public String getCategory() { return category; }
    public int getStock() { return stock; }

    public void setName(String name){ this.name = name;}
    public void setDescription(String description){ this.description = description; }
    public void setPriceBase(BigDecimal priceBase){ this.priceBase = priceBase; }
    public void setPriceSale(BigDecimal priceSale){ this.priceSale = priceSale; }
    public void setCategory(String category){ this.category = category; }
    public void setStock(int stock){ this.stock = stock; }

    @Override
    public String toString() {
        return "Product {" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", priceBase='" + COP_FORMATTER.format(priceBase)  + '\'' +
                ", priceSale='" + COP_FORMATTER.format(priceSale) + '\'' +
                ", category='" + category + '\'' +
                ", stock='" + stock +
                '}';
    }
}


