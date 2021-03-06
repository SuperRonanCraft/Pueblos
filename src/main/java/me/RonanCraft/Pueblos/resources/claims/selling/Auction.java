package me.RonanCraft.Pueblos.resources.claims.selling;

import me.RonanCraft.Pueblos.resources.claims.Claim;

public class Auction {

    public Claim claim;
    public int price;
    public long time;
    public long auctionId;

    public Auction(Claim claim, int price, long time) {
        this.claim = claim;
        this.price = price;
        this.time = time;
    }

    public int getPrice() {
        return price;
    }

    public long getTime() {
        return time;
    }

}
