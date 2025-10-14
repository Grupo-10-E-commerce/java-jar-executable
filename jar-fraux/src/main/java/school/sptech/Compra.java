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
}

