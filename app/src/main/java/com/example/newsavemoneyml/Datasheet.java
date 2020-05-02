package com.example.newsavemoneyml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Datasheet {


    // For merchant.csv File.
    private List<String> bank = Arrays.asList("sbi", "cen", "boi", "axb", "bob", "pnb", "uob", "cbi", "ioba", "tmbl", "idb", "cnb", "ici", "synb", "alb", "bom", "fbl", "hdf", "inb", "iib", "kmb", "pmc", "psb", "sbj", "sbh", "sbm", "sbp", "sbt", "sib", "ubi", "ybl");
    private List<String> merchant = Arrays.asList("swiggy","zomato","uber eats","dominos","food panda","kfc","amazon","flipkart","myntra ","jabong","ajio","flpkrt","paytmm","club factory","bwkoof","Big basket","grofers","bigbkt","naturebasket","easyday","amazonfresh","spencers","irctci","goibib","MMT","ola","uber","zoom car","oyorms","trivago","irctci","IRCTC","oyo","olacab","redbus","makemytrip","OYO","netmeds","apollo","pharmeasy","1mg","jio","airtel","vodafone","BSNL","tata sky","dishtv","videocon","netflix","amazon prime","hotstar","spotify","gaana","linkedin","youtube","EMI","emi","loan","policy","lic");
    private List<String> food = Arrays.asList("swiggy","zomato","uber eats","dominos","food panda","kfc");
    private List<String> shopping = Arrays.asList("amazon","flipkart","myntra ","jabong","ajio","flpkrt","paytmm","club factory","bwkoof");
    private List<String> grocery = Arrays.asList("Big basket","grofers","bigbkt","naturebasket","easyday","amazonfresh","spencers");
    private List<String> travel = Arrays.asList("irctci","goibib","MMT","ola","uber","zoom car","oyorms","trivago","IRCTC","oyo","olacab","redbus","makemytrip","OYO");
    private List<String> medical = Arrays.asList("netmeds","apollo","pharmeasy","1mg");
    private List<String> bills = Arrays.asList("jio","airtel","vodafone","BSNL","tata sky","dishtv","videocon");
    private List<String> subscription = Arrays.asList("netflix","amazon prime","hotstar","spotify","gaana","linkedin","youtube");
    private List<String> emi = Arrays.asList("EMI","emi","loan","policy","lic");
    private List<String> otp = Arrays.asList("OTP","requested");
    private List<String> ess = Arrays.asList("jio","airtel","vodafone","BSNL","tata sky","dishtv","videocon","EMI","emi","loan","policy","lic","netmeds","apollo","pharmeasy","1mg","irctci","IRCTC","Big basket","grofers","bigbkt","naturebasket","easyday","amazonfresh","spencers","ola","olacab","uber","uberride","zoom car");



    // For Autocorrect.csv File.
    private List<String> credit = Arrays.asList("CREDIT","credit","credited","credt","crd","transaction","trnxn.","trnx.","trxn.","trx.","trnxn","trnx","trxn","trx");
    private List<String> debit = Arrays.asList("DEBIT","debited","debit","dbtd.","dbt.","deposited");
    private List<String> rs = Arrays.asList("rs","inr","rupees","rupee","INR");
    private List<String> balance = Arrays.asList("BALANCE","balance","bal","balnc","blnce","bal");



    // For unigram.csv File.
    private List<String> unigram = Arrays.asList("keywords","credit","credited","credt","crd","transaction","trnxn.","trnx.","trxn.","trx.","trnxn","trnx","trxn","trx","debited","debit","dbtd."
            ,"dbt.","deposited","rs","inr","rupees","rupee","balance","bal","balnc","blnce","bal.","inr","rupees","rupee","swiggy","zomato","uber eats"
            ,"dominos","food panda","kfc,amazon","flipkart","myntra","jabong","ajio","flpkrt","paytmm","club factory","bwkoof","Big basket","grofers",
            "bigbkt","naturebasket","easyday","amazonfresh","spencers","irctci","goibib","MMT","ola","uber","zoom car","oyorms","trivago","irctci","IRCTC",
            "oyo","olacab","redbus","makemytrip","OYO","netmeds","apollo","pharmeasy","1mg","jio,airtel","vodafone,BSNL","tata sky","dishtv","videocon",
            "netflix","amazon prime","hotstar","spotify","gaana","linkedin","youtube","EMI","emi","loan","policy","lic","INR");


    // Function to create a autocorrect List of Lists containing all List of autocorrect.csv file.
    public List<List<String>> createAutocorrect(){

        List<List<String>> autocorrect = new ArrayList<>();

        autocorrect.add(credit);
        autocorrect.add(debit);
        autocorrect.add(rs);
        autocorrect.add(balance);

        return autocorrect;

    }


    // Function to create a merchantcsv List of Lists containing all List of merchant.csv file.
    public List<List<String>> createMerchant(){

        List<List<String>> merchantcsv = new ArrayList<>();

        merchantcsv.add(bank);
        merchantcsv.add(merchant);
        merchantcsv.add(food);
        merchantcsv.add(shopping);
        merchantcsv.add(grocery);
        merchantcsv.add(travel);
        merchantcsv.add(medical);
        merchantcsv.add(bills);
        merchantcsv.add(subscription);
        merchantcsv.add(emi);
        merchantcsv.add(otp);
        merchantcsv.add(ess);

        return merchantcsv;
    }


    // Function to create a unigram List of Lists containing all List of unigram.csv file.
    public List<String> createUnigram(){
        return unigram;
    }
}
