/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_TYPE;
import me.RonanCraft.Pueblos.resources.claims.selling.Auction;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Claim extends ClaimUpdates {

    public long claimId;
    UUID ownerId;
    String ownerName;
    private final BoundingBox boundingBox;
    public boolean deleted;
    //Loaded after
    private String claimName;
    private final ClaimFlags flags = new ClaimFlags(this);
    final ClaimMembers members = new ClaimMembers(this);
    private final List<ClaimRequest> requests = new ArrayList<>();
    public Date dateCreated;
    //Claim info
    boolean adminClaim;
    public final CLAIM_TYPE claimType;

    Claim(BoundingBox boundingBox) {
        this(null, null, boundingBox);
    }

    Claim(UUID ownerId, String ownerName, @NotNull BoundingBox boundingBox) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.boundingBox = boundingBox;
        this.adminClaim = this.ownerId == null;
        claimType = this instanceof ClaimMain ? CLAIM_TYPE.MAIN : CLAIM_TYPE.CHILD;
    }

    public boolean contains(Location loc) {
        if (!Objects.equals(loc.getWorld(), getWorld()))
            return false;
        return boundingBox.contains(loc);
    }

    public ClaimMember getMember(Player p) {
        return members.getMember(p.getUniqueId());
    }

    public boolean canBuild(Player p) {
        return isOwner(p) || isMember(p);
    }

    public String getClaimName() {
        return claimName != null ? claimName : (ownerName != null ? ownerName : (!isAdminClaim() ? getOwner().getName() : "Admin Claim"));
    }

    public ClaimFlags getFlags() {
        return flags;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public boolean isAdminClaim() {
        return adminClaim;
    }

    //Owner
    public boolean isOwner(Player p) {
        return p.getUniqueId().equals(ownerId);
    }

    public UUID getOwnerID() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Nullable
    public OfflinePlayer getOwner() {
        return ownerId == null ? null : Bukkit.getOfflinePlayer(ownerId);
    }
    //Members
    public List<ClaimMember> getMembers() {
        return members.getMembers();
    }

    public void removeMember(ClaimMember member, boolean update) {
        members.remove(member, update);
    }

    public void addMember(ClaimMember member, boolean update) {
        if (member != null) {
            for (ClaimMember _member : members.getMembers()) //Duplicate Member, lets remove them from the database too
                if (_member.uuid.equals(member.uuid)) {
                    updated(true);
                    return;
                }
            members.addMember(member, update);
        }
    }

    public boolean isMember(Player p) {
        return members.isMember(p.getUniqueId());
    }

    //Requests
    public List<ClaimRequest> getRequests() {
        return requests;
    }

    public ClaimRequest getRequest(Player p) {
        for (ClaimRequest request : requests)
            if (request.id.equals(p.getUniqueId()))
                return request;
        return null;
    }

    public void addRequest(ClaimRequest request, boolean update) {
        updated(update);
        this.requests.add(request);
    }

    public void removeRequest(ClaimRequest request, boolean update) {
        updated(update);
        requests.remove(request);
    }

    public boolean hasRequestFrom(Player p) {
        return getRequest(p) != null;
    }

    //Tools
    public boolean checkPermLevel(@NotNull Player p, CLAIM_PERMISSION_LEVEL level) {
        if (level != null)
            switch (level) {
                case OWNER: return isOwner(p) || (isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p));
                case MEMBER: return isMember(p);
                default: return true;
            }
        return true;
    }

    public CLAIM_ERRORS editCorners(@NotNull Player editor, Vector loc_1, Vector loc_2) {
        if (!HelperEvent.claimResize(editor, this, editor, loc_1, loc_2).isCancelled()) {
            if (Pueblos.getInstance().getClaimHandler().canResize(editor, this, new BoundingBox(editor.getWorld(), loc_1, loc_2))) {
                getBoundingBox().editCorners(loc_1, loc_2);
                updated(true);
                return CLAIM_ERRORS.NONE;
            } else {
                if (this instanceof ClaimMain)
                    return CLAIM_ERRORS.RESIZE_OVERLAPPING_CHILD;
                return CLAIM_ERRORS.RESIZE_OVERLAPPING_PARENT;
            }
        } else
            return CLAIM_ERRORS.CANCELLED;
    }

    public Location getLesserBoundaryCorner() {
        return new Location(boundingBox.getWorld(), boundingBox.getLeft(), 0, boundingBox.getBottom());
    }

    public Location getGreaterBoundaryCorner() {
        return new Location(boundingBox.getWorld(), boundingBox.getRight(), 0, boundingBox.getTop());
    }

    @NotNull
    public World getWorld() {
        return boundingBox.getWorld();
    }

    public boolean isChild() {
        return this instanceof ClaimChild;
    }

    public Auction getAuction() {
        return Pueblos.getInstance().getClaimHandler().getAuctionManager().getAuction(this);
    }

    public void setClaimName(String name, boolean update) {
        updated(update);
        this.claimName = name;
    }
}
