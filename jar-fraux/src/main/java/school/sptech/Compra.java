package school.sptech;

public class Compra {

    private Integer TransactionID;
    private String TransactionDate;
    private Double Amount;
    private Integer MerchantID;
    private String TransactionType;
    private String Location;
    private Boolean IsFraud;

    public Compra() {
    }

    public Compra(Integer transactionID, String transactionDate, Double amount, Integer merchantID, String transactionType, String location, Boolean isFraud) {
        TransactionID = transactionID;
        TransactionDate = transactionDate;
        Amount = amount;
        MerchantID = merchantID;
        TransactionType = transactionType;
        Location = location;
        IsFraud = isFraud;
    }

    @Override
    public String toString() {
        return "Compra{" +
                "TransactionID=" + TransactionID +
                ", TransactionDate='" + TransactionDate + '\'' +
                ", Amount=" + Amount +
                ", MerchantID=" + MerchantID +
                ", TransactionType='" + TransactionType + '\'' +
                ", Location='" + Location + '\'' +
                ", IsFraud=" + IsFraud +
                '}';
    }

    public Integer getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(Integer transactionID) {
        TransactionID = transactionID;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }

    public Integer getMerchantID() {
        return MerchantID;
    }

    public void setMerchantID(Integer merchantID) {
        MerchantID = merchantID;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Boolean getFraud() {
        return IsFraud;
    }

    public void setFraud(Boolean fraud) {
        IsFraud = fraud;
    }
}

