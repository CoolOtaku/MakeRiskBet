package com.example.makeriskbet;

public class Bet {

    private String betItem;
    private int bet;

    public Bet(String betItem, int bet) {
        this.betItem = betItem;
        this.bet = bet;
    }

    public String getBetItem() {
        return betItem;
    }

    public void setBetItem(String betItem) {
        this.betItem = betItem;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public void plusBet(int plusBet) {
        this.bet += plusBet;
    }

}
