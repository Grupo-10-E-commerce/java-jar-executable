package school.sptech;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Compra {

    private Integer TransactionID;
    private LocalDateTime TransactionDate;
    private Double Amount;
    private Integer MerchantID;
    private String TransactionType;
    private String Location;
    private Boolean IsFraud;

    public Compra() {
    }

    public Compra(Integer transactionID, LocalDateTime transactionDate, Double amount, Integer merchantID, String transactionType, String location, Boolean isFraud) {
        TransactionID = transactionID;
        TransactionDate = transactionDate;
        Amount = amount;
        MerchantID = merchantID;
        TransactionType = transactionType;
        Location = location;
        IsFraud = isFraud;
    }
}
