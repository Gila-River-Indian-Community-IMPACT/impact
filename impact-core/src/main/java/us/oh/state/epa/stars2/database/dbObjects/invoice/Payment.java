package us.oh.state.epa.stars2.database.dbObjects.invoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class Payment extends BaseDB {

    private String checkId;
    private String paidBy;
    private Timestamp datePosted;
    private Float amountPaid;
    private Float balance;

    public Payment() {
        super();
    }

    public Payment(Payment old) {
        super(old);

        if (old != null) {
            setCheckId(old.getCheckId());
            setPaidBy(old.getPaidBy());
            setDatePosted(old.getDatePosted());
            setAmountPaid(old.getAmountPaid());
            setBalance(old.getBalance());
        }
    }

    public final Float getAmountPaid() {
        return amountPaid;
    }

    public final void setAmountPaid(Float amountPaid) {
        this.amountPaid = amountPaid;
    }

    public final Float getBalance() {
        return balance;
    }

    public final void setBalance(Float balance) {
        this.balance = balance;
    }

    public final String getCheckId() {
        return checkId;
    }

    public final void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public final Timestamp getDatePosted() {
        return datePosted;
    }

    public final void setDatePosted(Timestamp datePosted) {
        this.datePosted = datePosted;
    }

    public final String getPaidBy() {
        return paidBy;
    }

    public final void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public final void populate(ResultSet rs) throws SQLException {
    }
}
