/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_FLAG;

import java.util.HashMap;

public class ClaimFlags {
    HashMap<CLAIM_FLAG, Object> flags = new HashMap<>();

    private final Claim claim;

    ClaimFlags(Claim claim) {
        this.claim = claim;
    }

    public void setFlag(CLAIM_FLAG flag, Object value, boolean update) {
        flags.put(flag, value);
        claim.updated(update);
    }

    public Object getFlag(CLAIM_FLAG flag) {
        return flags.getOrDefault(flag, flag.getDefault());
    }

    public HashMap<CLAIM_FLAG, Object> getFlags() {
        return flags;
    }
}
