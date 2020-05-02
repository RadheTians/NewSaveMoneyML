package com.example.newsavemoneyml;

public class SMS_Extracted {

    private int MEDICAL;
    private int BILLS;
    private int SUBSCRIPTION;
    private int EMI;
    private int REC;
    private int NON_REC;
    private int ESS;
    private int NON_ESS;
    private int CREDIT;
    private int DEBIT;
    private int ATM;
    private int NET_BANKING;
    private int FOOD;
    private int SHOPPING;
    private int GROCERY;
    private int TRAVEL;
    private String DATE;
    private String TIME;
    private String BANK;
    private String MERCHANT;
    private double TRX_AMT;
    private double AVL_BAL;

    public String printData(){
        return this.getDATE() +"," + getTIME()+"," +getBANK()+"," +getCREDIT()+"," +getDEBIT()+"," +getATM() +"," + getNET_BANKING()+"," +getTRX_AMT()+"," +getAVL_BAL()+"," +
                getMERCHANT()+"," +getGROCERY()+"," +getFOOD() +"," +getSHOPPING() +"," +getTRAVEL()  +"," +getMEDICAL() +"," +getBILLS() +"," +getSUBSCRIPTION() +"," +getREC() +"," +
                getNON_REC() +"," +getESS() +"," +getNON_ESS();
    }


    public int getMEDICAL() {
        return MEDICAL;
    }

    public void setMEDICAL(int MEDICAL) {
        this.MEDICAL = MEDICAL;
    }

    public int getBILLS() {
        return BILLS;
    }

    public void setBILLS(int BILLS) {
        this.BILLS = BILLS;
    }

    public int getSUBSCRIPTION() {
        return SUBSCRIPTION;
    }

    public void setSUBSCRIPTION(int SUBSCRIPTION) {
        this.SUBSCRIPTION = SUBSCRIPTION;
    }

    public int getEMI() {
        return EMI;
    }

    public void setEMI(int EMI) {
        this.EMI = EMI;
    }

    public int getREC() {
        return REC;
    }

    public void setREC(int REC) {
        this.REC = REC;
    }

    public int getNON_REC() {
        return NON_REC;
    }

    public void setNON_REC(int NON_REC) {
        this.NON_REC = NON_REC;
    }

    public int getESS() {
        return ESS;
    }

    public void setESS(int ESS) {
        this.ESS = ESS;
    }

    public int getNON_ESS() {
        return NON_ESS;
    }

    public void setNON_ESS(int NON_ESS) {
        this.NON_ESS = NON_ESS;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getBANK() {
        return BANK;
    }

    public void setBANK(String BANK) {
        this.BANK = BANK;
    }

    public int getCREDIT() {
        return CREDIT;
    }

    public void setCREDIT(int CREDIT) {
        this.CREDIT = CREDIT;
    }

    public int getDEBIT() {
        return DEBIT;
    }

    public void setDEBIT(int DEBIT) {
        this.DEBIT = DEBIT;
    }

    public double getTRX_AMT() {
        return TRX_AMT;
    }

    public void setTRX_AMT(double TRX_AMT) {
        this.TRX_AMT = TRX_AMT;
    }

    public double getAVL_BAL() {
        return AVL_BAL;
    }

    public void setAVL_BAL(double AVL_BAL) {
        this.AVL_BAL = AVL_BAL;
    }

    public int getATM() {
        return ATM;
    }

    public void setATM(int ATM) {
        this.ATM = ATM;
    }

    public int getNET_BANKING() {
        return NET_BANKING;
    }

    public void setNET_BANKING(int NET_BANKING) {
        this.NET_BANKING = NET_BANKING;
    }

    public String getMERCHANT() {
        return MERCHANT;
    }

    public void setMERCHANT(String MERCHANT) {
        this.MERCHANT = MERCHANT;
    }

    public int getFOOD() {
        return FOOD;
    }

    public void setFOOD(int FOOD) {
        this.FOOD = FOOD;
    }

    public int getSHOPPING() {
        return SHOPPING;
    }

    public void setSHOPPING(int SHOPPING) {
        this.SHOPPING = SHOPPING;
    }

    public int getGROCERY() {
        return GROCERY;
    }

    public void setGROCERY(int GROCERY) {
        this.GROCERY = GROCERY;
    }

    public int getTRAVEL() {
        return TRAVEL;
    }

    public void setTRAVEL(int TRAVEL) {
        this.TRAVEL = TRAVEL;
    }
}